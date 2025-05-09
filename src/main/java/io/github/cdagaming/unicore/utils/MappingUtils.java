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

import io.github.cdagaming.unicore.UniCore;
import io.github.classgraph.ClassInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Mapping Utilities used to convert between different Mojang Mapping Types
 *
 * @author CDAGaming, wagyourtail
 */
public class MappingUtils {
    private static Map<String, String> classMap = null;
    private static String filePath = "/mappings.srg";

    /**
     * Retrieve if the Mappings are currently present
     *
     * @return {@link Boolean#TRUE} if and only if the mappings are currently present
     */
    public static boolean areMappingsLoaded() {
        return classMap != null;
    }

    /**
     * Set the specified file path to retrieve data from
     *
     * @param filePath The new path to pull data from
     */
    public static void setFilePath(String filePath) {
        MappingUtils.filePath = filePath;
    }

    /**
     * Retrieve a mapping for class names from the Searge Data
     *
     * @return the resulting mappings
     */
    public static Map<String, String> getClassMap() {
        if (!areMappingsLoaded()) {
            final Map<String, String> cm = StringUtils.newHashMap();
            // load from /mappings.srg
            try {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                FileUtils.getResourceAsStream(MappingUtils.class, filePath),
                                StringUtils.DEFAULT_CHARSET
                        )
                )) {
                    UniCore.LOG.debugInfo("Loading Mappings...");
                    final Instant time = TimeUtils.getCurrentTime();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        final String[] parts = line.split(" ");
                        if (parts[0].equals("CL:")) {
                            cm.put(parts[1], parts[2]);
                        }
                    }
                    UniCore.LOG.debugInfo("Loaded Mappings in " + TimeUtils.getDurationFrom(time).toMillis() + "ms");
                }
            } catch (Throwable ex) {
                UniCore.LOG.debugError(ex);
            }
            classMap = cm;
        }
        return StringUtils.newHashMap(classMap);
    }

    /**
     * Retrieve the mapped class path for the specified argument, if present
     *
     * @param input The string to interpret
     * @return the resulting mapped class path
     */
    public static String getMappedPath(String input) {
        if (areMappingsLoaded() && classMap.containsKey(input)) {
            return classMap.get(input);
        }
        return input;
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start          The string to interpret
     * @param matchCondition The condition that, when satisfied, will add to the resulting list
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start, BiPredicate<String, String> matchCondition) {
        final Set<String> matches = StringUtils.newHashSet();
        if (areMappingsLoaded()) {
            for (Map.Entry<String, String> entry : classMap.entrySet()) {
                if (matchCondition.test(entry.getValue(), start)) {
                    matches.add(entry.getKey());
                }
            }
        }
        return matches;
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start The string to interpret
     * @param exact Whether to only return exact matches (using startsWith by default)
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start, boolean exact) {
        return getUnmappedClassesMatching(start, exact ? String::equals : String::startsWith);
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start The string to interpret
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start) {
        return getUnmappedClassesMatching(start, false);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object     The class object to interpret
     * @param simpleName Whether to return the simple name of the found class
     * @return the mapped class name
     */
    public static String getClassName(ClassInfo object, boolean simpleName) {
        return getClassName(simpleName, object.getName(), object.getSimpleName());
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object     The class object to interpret
     * @param simpleName Whether to return the simple name of the found class
     * @return the mapped class name
     */
    public static String getClassName(Class<?> object, boolean simpleName) {
        return getClassName(simpleName, object.getName(), object.getSimpleName());
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param simpleName Whether to return the simple name of the found class
     * @param primary    The primary object to interpret
     * @param secondary  The secondary object to interpret
     * @return the mapped class name
     */
    private static String getClassName(final boolean simpleName, final String primary, final String secondary) {
        String result = areMappingsLoaded() ? classMap.get(
                primary
        ) : null;
        if (result == null) {
            result = simpleName ? secondary : primary;
        } else {
            result = simpleName ? result.substring(result.lastIndexOf(".") + 1) : result;
        }
        return result;
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getCanonicalName(ClassInfo object) {
        return getClassName(object, false);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getCanonicalName(Class<?> object) {
        return getClassName(object, false);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The object to interpret
     * @return the mapped class name
     */
    public static String getCanonicalName(Object object) {
        return getCanonicalName(object.getClass());
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getClassName(ClassInfo object) {
        return getClassName(object, true);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getClassName(Class<?> object) {
        return getClassName(object, true);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The object to interpret
     * @return the mapped class name
     */
    public static String getClassName(Object object) {
        return getClassName(object.getClass());
    }
}
