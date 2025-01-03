/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.cdagaming.unicore.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdagaming.unicore.UniCore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Translation and Localization Utilities based on Language Code
 *
 * @author CDAGaming
 */
public class TranslationUtils {
    private static final Pattern JSON_PATTERN = Pattern.compile("(?s)\\\\(.)");
    /**
     * The Stored Mapping of Language Request History
     * <p>
     * Format: languageId:doesExist
     */
    private final Map<String, Map<String, String>> requestMap = StringUtils.newHashMap();
    /**
     * The list of removed translations, if any
     */
    private final List<String> removed = StringUtils.newArrayList();
    /**
     * The list of renamed translations, if any
     */
    private final Map<String, String> renamed = StringUtils.newHashMap();
    /**
     * The default/fallback Language ID to Locate and Retrieve Translations
     */
    private String defaultLanguageId = "en_us";
    /**
     * The current Language ID to Locate and Retrieve Translations
     */
    private String languageId = defaultLanguageId;
    /**
     * The Target ID to locate the Language File
     */
    private String modId;
    /**
     * The Charset Encoding to parse translations in
     */
    private String encoding;
    /**
     * If this module should check for deprecated data
     */
    private boolean checkDeprecations = true;
    /**
     * If using a .Json or .Lang Language File
     */
    private boolean usingJson = false;
    /**
     * If using the modern "assets/xxx" file-path
     */
    private boolean usingAssetsPath = true;
    /**
     * Whether to Remove Color Codes from Translated Strings
     */
    private boolean stripColors = false;
    /**
     * Whether to Remove Formatting Codes from Translated Strings
     */
    private boolean stripFormatting = false;
    /**
     * The function to use when retrieving additional {@link InputStream} data for resources
     * <p>
     * Function: [instance, langPath] => List of {@link InputStream} instances
     */
    private BiFunction<TranslationUtils, String, List<InputStream>> resourceSupplier = (instance, langPath) -> StringUtils.newArrayList();
    /**
     * The function to use when retrieving the current language to use
     * <p>
     * Function: [fallbackLanguage] => currentLanguage
     */
    private Function<String, String> languageSupplier = (fallback) -> fallback;
    /**
     * The event to trigger upon language sync, useful for external integrations
     */
    private Consumer<Map<String, String>> onLanguageSync = null;
    /**
     * If this module needs a full sync
     */
    private boolean needsSync;
    /**
     * If this module has checked for internal deprecations
     */
    private boolean checkedDeprecations = false;

    /**
     * Sets initial Data and Retrieves Valid Translations
     */
    public TranslationUtils() {
        this(false);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param useJson Toggles whether to use .Json or .Lang, if present
     */
    public TranslationUtils(final boolean useJson) {
        this("", useJson);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId Sets the Target Mod ID to locate Language Files
     */
    public TranslationUtils(final String modId) {
        this(modId, false);
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId   Sets the Target Mod ID to locate Language Files
     * @param useJson Toggles whether to use .Json or .Lang, if present
     */
    public TranslationUtils(final String modId, final boolean useJson) {
        this(modId, useJson, "UTF-8");
    }

    /**
     * Sets initial Data and Retrieves Valid Translations
     *
     * @param modId    Sets the Target Mod ID to locate Language Files
     * @param useJson  Toggles whether to use .Json or .Lang, if present
     * @param encoding The Charset Encoding to parse Language Files
     */
    public TranslationUtils(final String modId, final boolean useJson, final String encoding) {
        setUsingJson(useJson);
        setModId(modId);
        setEncoding(encoding);
    }

    /**
     * Attempt to retrieve the localized equivalent of the specified string
     *
     * @param original The string to interpret
     * @return The equivalent localized string, if present
     */
    public String getLocalizedMessage(final String original) {
        String result = original.trim();
        if (result.contains(" ")) {
            String adjusted = result;
            for (String dataPart : result.split(" ")) {
                if (hasTranslation(dataPart)) {
                    adjusted = adjusted.replace(dataPart, translate(dataPart));
                }
            }
            result = adjusted;
        } else if (hasTranslation(original)) {
            result = translate(result);
        }
        return result;
    }

    /**
     * Build and Perform Synchronization on this instance
     *
     * @return the current instance, used for chain-building
     */
    public TranslationUtils build() {
        // Retrieve localized default translations
        syncTranslations(getDefaultLanguage());

        needsSync = true;
        return this;
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Translation Data as needed
     */
    public void onTick() {
        final String currentLanguageId = getCurrentLanguage();
        final boolean hasLanguageChanged = (!languageId.equals(currentLanguageId) &&
                (!hasTranslationsFrom(currentLanguageId) || !requestMap.get(currentLanguageId).isEmpty()));
        if (needsSync) {
            // Sync All if we need to (Normally for initialization or reload purposes)
            final List<String> requestedKeys = StringUtils.newArrayList(requestMap.keySet());
            for (String key : requestedKeys) {
                syncTranslations(key, false);
            }
            needsSync = false;
        } else if (hasLanguageChanged) {
            // Otherwise, only sync the current language if needed
            syncTranslations(currentLanguageId);
        }
    }

    /**
     * Synchronize the translation mappings for the specified language ID
     *
     * @param languageId  the language ID to interpret
     * @param setLanguage Whether we want the language ID to be the one in use
     */
    public void syncTranslations(final String languageId, final boolean setLanguage) {
        if (setLanguage) {
            setLanguage(languageId);
        }
        final Map<String, String> results = getTranslationMapFrom(languageId, encoding);
        if (onLanguageSync != null) {
            onLanguageSync.accept(results);
        }
    }

    /**
     * Synchronize the translation mappings for the specified language ID
     *
     * @param languageId the language ID to interpret
     */
    public void syncTranslations(final String languageId) {
        syncTranslations(languageId, true);
    }

    /**
     * Synchronize the translation mappings for all language ids
     */
    public void syncTranslations() {
        needsSync = true;
    }

    /**
     * Determine the current language ID to be using
     *
     * @return the current language id to be used
     */
    private String getCurrentLanguage() {
        final String result = languageSupplier.apply(defaultLanguageId);
        return usingJson ? result.toLowerCase() : result;
    }

    /**
     * Determine the default language ID to be using
     *
     * @return the default language id to be used
     */
    public String getDefaultLanguage() {
        return usingJson ? defaultLanguageId.toLowerCase() : defaultLanguageId;
    }

    /**
     * Sets the Default Language ID to Retrieve Translations for, if present
     *
     * @param languageId The Language ID (Default: en_US)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setDefaultLanguage(final String languageId) {
        this.defaultLanguageId = languageId;
        return this;
    }

    /**
     * Toggles whether to use the modern "assets/xxx" file path when locating translations
     *
     * @param usingAssetsPath Toggles whether to use the modern "assets/xxx" file path
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setUsingAssetsPath(final boolean usingAssetsPath) {
        this.usingAssetsPath = usingAssetsPath;
        return this;
    }

    /**
     * Toggles whether to remove Color Codes from Translated Strings
     *
     * @param stripColors the new "Strip Colors" status
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setStripColors(final boolean stripColors) {
        this.stripColors = stripColors;
        return this;
    }

    /**
     * Toggles whether to remove Formatting Codes from Translated Strings
     *
     * @param stripFormatting the new "Strip Formatting" status
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setStripFormatting(final boolean stripFormatting) {
        this.stripFormatting = stripFormatting;
        return this;
    }

    /**
     * Sets the function to use when retrieving additional {@link InputStream} data for resources
     *
     * @param resourceSupplier the new resource-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setResourceSupplier(final BiFunction<TranslationUtils, String, List<InputStream>> resourceSupplier) {
        this.resourceSupplier = resourceSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving additional {@link InputStream} data for resources
     *
     * @param resourceSupplier the new resource-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setResourceSupplier(final List<InputStream> resourceSupplier) {
        this.resourceSupplier = (instance, langPath) -> resourceSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving the current language to use
     *
     * @param languageSupplier the new language-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setLanguageSupplier(final Function<String, String> languageSupplier) {
        this.languageSupplier = languageSupplier;
        return this;
    }

    /**
     * Sets the function to use when retrieving the current language to use
     *
     * @param languageSupplier the new language-supplying function
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setLanguageSupplier(final String languageSupplier) {
        this.languageSupplier = (fallback) -> languageSupplier;
        return this;
    }

    /**
     * Sets the event to trigger upon language sync
     *
     * @param onLanguageSync the event to trigger
     * @return the current instance, used for chain-building
     */
    public TranslationUtils setOnLanguageSync(final Consumer<Map<String, String>> onLanguageSync) {
        this.onLanguageSync = onLanguageSync;
        return this;
    }

    /**
     * Toggles whether to use .Lang or .Json Language Files
     *
     * @param usingJson Toggles whether to use .Json or .Lang, if present
     * @return the current instance, for chain-building
     */
    public TranslationUtils setUsingJson(final boolean usingJson) {
        this.usingJson = usingJson;
        return this;
    }

    /**
     * Toggles whether this module should check for deprecated data
     *
     * @param checkDeprecations Toggles whether this module should check for deprecated data
     * @return the current instance, for chain-building
     */
    public TranslationUtils setCheckDeprecations(final boolean checkDeprecations) {
        this.checkDeprecations = checkDeprecations;
        return this;
    }

    /**
     * Sets the Language ID to Retrieve Translations for, if present
     *
     * @param languageId The Language ID (Default: en_US)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setLanguage(final String languageId) {
        final String result = StringUtils.getOrDefault(languageId, defaultLanguageId);
        this.languageId = usingJson ? result.toLowerCase() : result;
        return this;
    }

    /**
     * Sets the Charset Encoding to parse Translations in, if present
     *
     * @param encoding The Charset Encoding (Default: UTF-8)
     * @return the current instance, for chain-building
     */
    public TranslationUtils setEncoding(final String encoding) {
        this.encoding = StringUtils.getOrDefault(encoding, "UTF-8");
        return this;
    }

    /**
     * Gets the Mod ID to target when locating Language Files
     *
     * @return The Mod ID to target
     */
    public String getModId() {
        return modId;
    }

    /**
     * Sets the Mod ID to target when locating Language Files
     *
     * @param modId The Mod ID to target
     * @return the current instance, for chain-building
     */
    public TranslationUtils setModId(final String modId) {
        this.modId = StringUtils.getOrDefault(modId);
        return this;
    }

    /**
     * Retrieve the active assets path
     *
     * @return the active assets path
     */
    public String getAssetsPath() {
        return usingAssetsPath ? String.format("/assets/%s/", getModId()) : "/";
    }

    /**
     * Retrieve the active deprecations path
     *
     * @return the active deprecations path
     */
    public String getDeprecatedPath() {
        return getAssetsPath() + "lang/deprecated.json";
    }

    /**
     * Apply Translation Deprecations to a given translation map
     *
     * @param map      The translations map to process
     * @param encoding The Charset Encoding (Default: UTF-8)
     */
    public void applyDeprecations(final Map<String, String> map, final String encoding) {
        if (!checkDeprecations) return;

        if (!checkedDeprecations) {
            final InputStream local = FileUtils.getResourceAsStream(TranslationUtils.class, getDeprecatedPath());
            if (local != null) {
                try (InputStreamReader reader = new InputStreamReader(local, Charset.forName(encoding))) {
                    final JsonElement element = FileUtils.parseJson(reader);
                    if (element != null && element.isJsonObject()) {
                        final JsonObject obj = element.getAsJsonObject();

                        for (JsonElement section : obj.getAsJsonArray("removed")) {
                            removed.add(section.getAsString());
                        }
                        for (Map.Entry<String, JsonElement> entry : obj.getAsJsonObject("renamed").entrySet()) {
                            renamed.put(entry.getKey(), entry.getValue().getAsString());
                        }
                    }
                    local.close();
                } catch (Exception ex) {
                    UniCore.LOG.error("An exception has occurred while parsing deprecated Translation data, aborting scan and clearing data to prevent issues...");
                    UniCore.LOG.debugError(ex);

                    removed.clear();
                    renamed.clear();
                }
            }
            checkedDeprecations = true;
        }

        for (String data : removed) {
            map.remove(data);
        }

        renamed.forEach((oldKey, newKey) -> {
            final String oldValue = map.remove(oldKey);
            if (oldValue == null) {
                UniCore.LOG.warn("Missing translation key for rename: " + oldKey);
                map.remove(newKey);
            } else {
                map.put(newKey, oldValue);
            }
        });
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the specified language
     *
     * @param languageId The language ID to interpret
     * @param ext        The file extension to look for (Default: lang or json)
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreamsFrom(final String languageId, final String ext) {
        final String assetsPath = getAssetsPath();
        final String langPath = String.format("lang/%s.%s", languageId, ext);
        final List<InputStream> results = StringUtils.newArrayList();

        final InputStream local = FileUtils.getResourceAsStream(TranslationUtils.class, assetsPath + langPath);
        if (local != null) {
            results.add(local);
        }
        results.addAll(resourceSupplier.apply(this, langPath));
        return results;
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the specified language
     *
     * @param languageId The language ID to interpret
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreamsFrom(final String languageId) {
        return getLocaleStreamsFrom(languageId, (usingJson ? "json" : "lang"));
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the current language
     *
     * @param ext The file extension to look for (Default: lang or json)
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreams(final String ext) {
        return getLocaleStreamsFrom(languageId, ext);
    }

    /**
     * Fetches a list of valid {@link InputStream}'s that can be used for the current language
     *
     * @return the interpreted list of valid {@link InputStream}'s
     */
    private List<InputStream> getLocaleStreams() {
        return getLocaleStreamsFrom(languageId);
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     * @param encoding   The Charset Encoding (Default: UTF-8)
     * @param data       The {@link InputStream}'s to accept data from
     * @return the processed list of translations
     */
    private Map<String, String> getTranslationMapFrom(final String languageId, final String encoding, final List<InputStream> data) {
        boolean hasError = false, hadBefore = hasTranslationsFrom(languageId);
        requestMap.remove(languageId);
        final Map<String, String> translationMap = StringUtils.newHashMap();

        if (data != null && !data.isEmpty()) {
            for (InputStream in : data) {
                if (in != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(encoding)))) {
                        String currentString;
                        while ((currentString = reader.readLine()) != null) {
                            currentString = currentString.trim();
                            if (!currentString.startsWith("#") && !currentString.startsWith("[{}]") && (usingJson ? currentString.contains(":") : currentString.contains("="))) {
                                final String[] splitTranslation = usingJson ? currentString.split(":", 2) : currentString.split("=", 2);
                                if (usingJson) {
                                    String str1 = splitTranslation[0].substring(1, splitTranslation[0].length() - 1).trim();
                                    String str2 = splitTranslation[1].substring(2, splitTranslation[1].length() - (splitTranslation[1].endsWith(",") ? 2 : 1)).trim();
                                    translationMap.put(
                                            StringUtils.replaceMatches(JSON_PATTERN, str1, "$1"),
                                            StringUtils.replaceMatches(JSON_PATTERN, str2, "$1")
                                    );
                                } else {
                                    translationMap.put(splitTranslation[0].trim(), splitTranslation[1].trim());
                                }
                            }
                        }

                        in.close();
                    } catch (Exception ex) {
                        UniCore.LOG.error("An exception has occurred while loading Translation Mappings, aborting scan to prevent issues...");
                        UniCore.LOG.debugError(ex);
                        hasError = true;
                        break;
                    }
                } else {
                    hasError = true;
                    break;
                }
            }
        } else {
            hasError = true;
        }

        if (hasError) {
            UniCore.LOG.error("Translations for " + getModId() + " do not exist for " + languageId);
            translationMap.clear();
            requestMap.put(languageId, translationMap);
            setLanguage(defaultLanguageId);
        } else {
            applyDeprecations(translationMap, encoding);
            UniCore.LOG.debugInfo((hadBefore ? "Refreshed" : "Added") + " translations for " + getModId() + " for " + languageId);
            requestMap.put(languageId, translationMap);
        }
        return translationMap;
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     * @param encoding   The Charset Encoding (Default: UTF-8)
     */
    private Map<String, String> getTranslationMapFrom(final String languageId, final String encoding) {
        return getTranslationMapFrom(languageId, encoding, getLocaleStreamsFrom(languageId));
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     *
     * @param languageId The language ID to interpret
     */
    private Map<String, String> getTranslationMapFrom(final String languageId) {
        return getTranslationMapFrom(languageId, "UTF-8");
    }

    /**
     * Retrieves and Synchronizes a List of Translations from a Language File
     */
    private Map<String, String> getTranslationMap() {
        return getTranslationMapFrom(languageId);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param languageId      The language ID to interpret
     * @param stripColors     Whether to Remove Color Codes
     * @param stripFormatting Whether to Remove Formatting Codes
     * @param translationKey  The raw String to translate
     * @param parameters      Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String languageId, final boolean stripColors, final boolean stripFormatting, final String translationKey, final Object... parameters) {
        boolean hasError = false;
        String translatedString = translationKey;
        try {
            if (hasTranslationFrom(languageId, translationKey)) {
                String rawString = getTranslationFrom(languageId, translationKey);
                translatedString = parameters.length > 0 ? String.format(rawString, parameters) : rawString;
            } else {
                hasError = true;
            }
        } catch (Exception ex) {
            UniCore.LOG.error("Exception parsing " + translationKey + " from " + languageId);
            UniCore.LOG.debugError(ex);
            return translationKey;
        }

        if (hasError) {
            UniCore.LOG.debugError("Unable to retrieve a translation for " + translationKey + " from " + languageId);
            if (!languageId.equals(getDefaultLanguage())) {
                UniCore.LOG.debugError("Attempting to retrieve default translation for " + translationKey);
                return translateFrom(getDefaultLanguage(), stripColors, stripFormatting, translationKey, parameters);
            }
        }
        String result = translatedString;
        if (stripFormatting && stripColors) {
            result = StringUtils.stripAllFormatting(result);
        } else {
            if (stripColors) {
                result = StringUtils.stripColors(result);
            }
            if (stripFormatting) {
                result = StringUtils.stripFormatting(result);
            }
        }
        return result;
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param stripColors     Whether to Remove Color Codes
     * @param stripFormatting Whether to Remove Formatting Codes
     * @param translationKey  The raw String to translate
     * @param parameters      Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final boolean stripColors, final boolean stripFormatting, final String translationKey, final Object... parameters) {
        return translateFrom(getDefaultLanguage(), stripColors, stripFormatting, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the current language
     *
     * @param stripColors     Whether to Remove Color Codes
     * @param stripFormatting Whether to Remove Formatting Codes
     * @param translationKey  The raw String to translate
     * @param parameters      Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translate(final boolean stripColors, final boolean stripFormatting, final String translationKey, final Object... parameters) {
        return translateFrom(languageId, stripColors, stripFormatting, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The raw String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String languageId, final String translationKey, final Object... parameters) {
        return translateFrom(languageId, stripColors, stripFormatting, translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the specified language
     *
     * @param translationKey The raw String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translateFrom(final String translationKey, final Object... parameters) {
        return translateFrom(getDefaultLanguage(), translationKey, parameters);
    }

    /**
     * Translates an Unlocalized String, based on the translations retrieved for the current language
     *
     * @param translationKey The raw String to translate
     * @param parameters     Extra Formatting Arguments, if needed
     * @return The Localized Translated String
     */
    public String translate(final String translationKey, final Object... parameters) {
        return translateFrom(languageId, translationKey, parameters);
    }

    /**
     * Determines whether translations are present for the specified language
     *
     * @param languageId The language ID to interpret
     * @return whether translations are present for this language
     */
    public boolean hasTranslationsFrom(final String languageId) {
        return requestMap.containsKey(languageId);
    }

    /**
     * Determines whether the specified translation exists for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The raw String to interpret
     * @return whether the specified translation exists
     */
    public boolean hasTranslationFrom(final String languageId, final String translationKey) {
        if (hasTranslationsFrom(languageId)) {
            return requestMap.get(languageId).containsKey(translationKey);
        } else {
            return getTranslationMapFrom(languageId).containsKey(translationKey);
        }
    }

    /**
     * Determines whether the specified translation exists for the current or default language
     *
     * @param translationKey The raw String to interpret
     * @return whether the specified translation exists
     */
    public boolean hasTranslation(final String translationKey) {
        return hasTranslationFrom(languageId, translationKey) || hasTranslationFrom(getDefaultLanguage(), translationKey);
    }

    /**
     * Retrieves the specified translation, if it exists for the specified language
     *
     * @param languageId     The language ID to interpret
     * @param translationKey The raw String to interpret
     * @return whether the specified translation exists
     */
    public String getTranslationFrom(final String languageId, final String translationKey) {
        if (hasTranslationFrom(languageId, translationKey)) {
            return requestMap.get(languageId).get(translationKey);
        }
        return null;
    }

    /**
     * Retrieves the specified translation, if it exists for the current or default language
     *
     * @param translationKey The raw String to interpret
     * @return whether the specified translation exists
     */
    public String getTranslation(final String translationKey) {
        return StringUtils.getOrDefault(
                getTranslationFrom(languageId, translationKey),
                getTranslationFrom(getDefaultLanguage(), translationKey)
        );
    }
}
