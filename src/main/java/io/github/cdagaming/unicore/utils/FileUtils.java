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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.cdagaming.unicore.UniCore;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.lenni0451.reflect.Classes;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

/**
 * File Utilities for interpreting Files and Class Objects
 *
 * @author CDAGaming
 */
public class FileUtils {
    /**
     * The Current Thread's Class Loader, used to dynamically receive data as needed
     */
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    /**
     * A GSON Json Builder Instance
     */
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    /**
     * The list of the currently detected class names
     */
    private static final Map<String, ClassInfo> CLASS_MAP = StringUtils.newHashMap();
    /**
     * The list of the currently cached class nameToObject retrievals
     */
    private static final Map<String, Class<?>> CLASS_CACHE = StringUtils.newHashMap();
    /**
     * The list of currently allocated Thread Factories
     */
    private static final Map<String, Pair<ScheduledExecutorService, ThreadFactory>> THREAD_FACTORY_MAP = StringUtils.newHashMap();
    /**
     * Whether the class list from {@link FileUtils#getClassMap()} is being iterated upon
     */
    private static boolean ARE_CLASSES_LOADING = false;
    /**
     * Whether we have already performed a full class scan through {@link FileUtils#getClassMap()}
     */
    private static boolean ARE_CLASSES_SCANNED = false;
    /**
     * Whether functions utilizing ClassGraph are enabled
     */
    private static boolean CLASS_GRAPH_ENABLED = true;

    /**
     * Shutdown the specified Thread Factories
     *
     * @param args the thread factories to shut down
     */
    public static void shutdownScheduler(final String... args) {
        for (String name : args) {
            if (THREAD_FACTORY_MAP.containsKey(name)) {
                THREAD_FACTORY_MAP.get(name).getFirst().shutdown();
            }
        }
    }

    /**
     * Shutdown all registered Thread Factories
     */
    public static void shutdownSchedulers() {
        for (Pair<ScheduledExecutorService, ThreadFactory> data : THREAD_FACTORY_MAP.values()) {
            data.getFirst().shutdown();
        }
    }

    /**
     * Retrieve or create a Scheduler pair for this Class, used for Scheduling Events
     *
     * @param name The name for the instance
     * @return the created Scheduler pair, containing the {@link ScheduledExecutorService} and {@link ThreadFactory}
     */
    public static Pair<ScheduledExecutorService, ThreadFactory> getOrCreateScheduler(final String name) {
        if (!THREAD_FACTORY_MAP.containsKey(name)) {
            final ThreadFactory threadFactory = r -> {
                final Thread t = new Thread(r, name);
                t.setDaemon(true);
                return t;
            };
            final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(threadFactory);
            THREAD_FACTORY_MAP.put(name, new Pair<>(exec, threadFactory));
        }
        return THREAD_FACTORY_MAP.get(name);
    }

    /**
     * Retrieve the Timer Instance for this Class, used for Scheduling Events
     *
     * @param name The name for the instance
     * @return the Timer Instance for this Class
     */
    public static ScheduledExecutorService getThreadPool(final String name) {
        return getOrCreateScheduler(name).getFirst();
    }

    /**
     * Retrieve the Thread Factory Instance for this Class, used for Scheduling Events
     *
     * @param name The name for the instance
     * @return the Thread Factory Instance for this class
     */
    public static ThreadFactory getThreadFactory(final String name) {
        return getOrCreateScheduler(name).getSecond();
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access
     * @param encoding    The Charset Encoding to parse URL Contents in
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @param args        The Command Arguments to parse
     * @return The URLs Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJsonData(final URL url, final String encoding, final Class<T> targetClass, final Modifiers... args) throws Exception {
        return getJsonData(UrlUtils.getURLText(url, encoding), targetClass, args);
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @param args        The Command Arguments to parse
     * @return The URLs Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJsonData(final URL url, final Class<T> targetClass, final Modifiers... args) throws Exception {
        return getJsonData(url, "UTF-8", targetClass, args);
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access (To be converted into a URL)
     * @param encoding    The Charset Encoding to parse URL Contents in
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @param args        The Command Arguments to parse
     * @return The URLs Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJsonFromURL(final String url, final String encoding, final Class<T> targetClass, final Modifiers... args) throws Exception {
        return getJsonData(new URL(url), encoding, targetClass, args);
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access (To be converted into a URL)
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @param args        The Command Arguments to parse
     * @return The URLs Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJsonFromURL(final String url, final Class<T> targetClass, final Modifiers... args) throws Exception {
        return getJsonFromURL(url, "UTF-8", targetClass, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The File to access
     * @param encoding The encoding to parse the file as
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final String encoding, final Class<T> classObj, final Modifiers... args) throws Exception {
        return getJsonData(fileToString(data, encoding), classObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The File to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final Class<T> classObj, final Modifiers... args) throws Exception {
        return getJsonData(data, "UTF-8", classObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The json string to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final String data, final Class<T> classObj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().fromJson(data, classObj);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The File to access
     * @param encoding The encoding to parse the file as
     * @param typeObj  The target type to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final String encoding, final Type typeObj, final Modifiers... args) throws Exception {
        return getJsonData(fileToString(data, encoding), typeObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data    The File to access
     * @param typeObj The target type to base the output on
     * @param <T>     The Result and Class Type
     * @param args    The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final Type typeObj, final Modifiers... args) throws Exception {
        return getJsonData(data, "UTF-8", typeObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data    The json string to access
     * @param typeObj The target type to base the output on
     * @param <T>     The Result and Class Type
     * @param args    The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final String data, final Type typeObj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().fromJson(data, typeObj);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The data to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final T data, final Class<T> classObj, final Modifiers... args) {
        return getJsonData(data.toString(), classObj, args);
    }

    /**
     * Interpret compatible objects into Json Elements
     *
     * @param obj  The object data to access
     * @param args The Command Arguments to parse
     * @return the resulting json string
     */
    public static String toJsonData(Object obj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        if (obj instanceof String || obj instanceof Reader || obj instanceof JsonReader) {
            obj = parseJson(obj);
        }
        return builder.create().toJson(obj);
    }

    /**
     * Attempt to parse the specified object into a JsonElement
     *
     * @param json the object to interpret
     * @return the processed JsonElement, if able
     */
    public static JsonElement parseJson(Object json) {
        if (json instanceof String) {
            return new JsonParser().parse((String) json);
        } else if (json instanceof Reader) {
            return new JsonParser().parse((Reader) json);
        } else if (json instanceof JsonReader) {
            return new JsonParser().parse((JsonReader) json);
        }
        return null;
    }

    /**
     * Writes Raw Json Data Objects to the specified file
     *
     * @param json     The json data to access
     * @param file     The resulting file to output to
     * @param encoding The encoding to parse the output as
     * @param args     The Command Arguments to parse
     */
    public static void writeJsonData(final Object json, final File file, final String encoding, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);

        try {
            assertFileExists(file);
        } catch (Exception ex1) {
            UniCore.LOG.error("Failed to create json data @ " + file.getAbsolutePath());
            UniCore.LOG.debugError(ex1);
        }

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), Charset.forName(encoding))) {
            builder.create().toJson(json, writer);
        } catch (Exception ex2) {
            UniCore.LOG.error("Failed to write json data @ " + file.getAbsolutePath());
            UniCore.LOG.debugError(ex2);
        }
    }

    /**
     * Asserts whether a file exists and is available
     *
     * @param file The target file to interpret or create
     * @throws Exception if an exception occurs in the method
     */
    public static void assertFileExists(final File file) throws Exception {
        final File parentDir = file.getParentFile();
        final boolean parentDirPresent = file.getParentFile().exists() || file.getParentFile().mkdirs();
        final boolean fileAvailable = (file.exists() && file.isFile()) || file.createNewFile();
        if (!parentDirPresent) {
            throw new UnsupportedOperationException("Failed to setup parent directory @ " + parentDir.getAbsolutePath());
        }
        if (!fileAvailable) {
            throw new UnsupportedOperationException("Failed to setup target file (Unable to create or is not a file) @ " + file.getAbsolutePath());
        }
    }

    /**
     * Downloads a File from a {@link URL}, then stores it at the target location
     *
     * @param urlString The Download Link
     * @param file      The destination and filename to store the download as
     */
    public static void downloadFile(final String urlString, final File file) {
        try {
            UniCore.LOG.info("Downloading \"%s\" to \"%s\"... (From: \"%s\")", file.getName(), file.getAbsolutePath(), urlString);
            final URL url = new URL(urlString);
            if (file.exists() && !file.delete()) {
                UniCore.LOG.error("Failed to remove: " + file.getName());
            }
            copyStreamToFile(UrlUtils.getURLStream(url), file);
            UniCore.LOG.info("\"%s\" has been successfully downloaded to \"%s\"! (From: \"%s\")", file.getName(), file.getAbsolutePath(), urlString);
        } catch (Exception ex) {
            UniCore.LOG.error("Failed to download \"%s\" from \"%s\"", file.getName(), urlString);
            UniCore.LOG.debugError(ex);
        }
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is closed, if specified.
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will be closed if specified
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @param close  whether to close the source stream, upon success
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyStreamToFile(final InputStream stream, final File file, final boolean close) throws Exception {
        // Create File and Parent Directories as needed
        assertFileExists(file);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        if (close) {
            stream.close();
        }
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is closed upon success.
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will be closed upon success
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyStreamToFile(final InputStream stream, final File file) throws Exception {
        copyStreamToFile(stream, file, true);
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream remains open upon success.
     * See {@link #copyStreamToFile(InputStream, File)} for a method that does close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will remain open upon success
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyToFile(final InputStream stream, final File file) throws Exception {
        copyStreamToFile(stream, file, false);
    }

    /**
     * Attempts to convert a File's data into a readable String
     *
     * @param file     The file to access
     * @param encoding The encoding to parse the file as
     * @return The file's data as a String
     * @throws Exception If Unable to read the file
     */
    public static String fileToString(final File file, final String encoding) throws Exception {
        return fileToString(Files.newInputStream(file.toPath()), encoding);
    }

    /**
     * Attempts to convert a InputStream's data into a readable String
     *
     * @param stream   The InputStream to interpret
     * @param encoding The encoding to parse the file as
     * @return The file's data as a String
     * @throws Exception If Unable to read the file
     */
    public static String fileToString(final InputStream stream, final String encoding) throws Exception {
        return UrlUtils.readerToString(
                new BufferedReader(
                        new InputStreamReader(
                                stream, Charset.forName(encoding)
                        )
                )
        );
    }

    /**
     * Convert the specified String into an InputStream
     *
     * @param stream   The string to interpret
     * @param encoding The encoding to parse the file as
     * @return The string's data as an InputStream
     */
    public static InputStream stringToStream(final String stream, final String encoding) {
        return new ByteArrayInputStream(
                StringUtils.getBytes(stream, encoding)
        );
    }

    /**
     * Gets the File Extension of a File (Ex: txt)
     *
     * @param file The file to access
     * @return The file's extension String
     */
    public static String getFileExtension(final File file) {
        return getFileExtension(file.getName());
    }

    /**
     * Gets the File Extension of a File (Ex: txt)
     *
     * @param name The file to access
     * @return The file's extension String
     */
    public static String getFileExtension(final String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    /**
     * Gets the File Name without the File Extension
     *
     * @param file The file to access
     * @return the trimmed file name
     */
    public static String getFileNameWithoutExtension(final File file) {
        return getFileNameWithoutExtension(file.getName());
    }

    /**
     * Gets the File Name without the File Extension
     *
     * @param name The file to access
     * @return the trimmed file name
     */
    public static String getFileNameWithoutExtension(final String name) {
        if (name.indexOf(".") > 0) {
            return name.substring(0, name.lastIndexOf("."));
        } else {
            return name;
        }
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchList     The Super Type Classes to look for
     * @param sourcePackages The root package directories to search within
     * @return The List of found class names from the search
     */
    public static Map<String, ClassInfo> getClassNamesMatchingSuperType(final List<Class<?>> searchList, final String... sourcePackages) {
        final Map<String, ClassInfo> matchingClasses = StringUtils.newHashMap();
        if (!isClassGraphEnabled()) return matchingClasses;

        final Map<String, ClassInfo> subClassData = StringUtils.newHashMap();
        for (Map.Entry<String, ClassInfo> classInfo : getClasses(sourcePackages).entrySet()) {
            for (Class<?> searchClass : searchList) {
                if (isSubclassOf(classInfo.getValue(), searchClass, subClassData)) {
                    // If superclass data was found, add the scanned classes
                    for (Map.Entry<String, ClassInfo> subClassInfo : subClassData.entrySet()) {
                        if (!matchingClasses.containsKey(subClassInfo.getKey())) {
                            matchingClasses.put(subClassInfo.getKey(), subClassInfo.getValue());
                        }
                    }

                    break;
                } else {
                    // If no superclass data found, reset for next data
                    subClassData.clear();
                }
            }
        }

        return matchingClasses;
    }

    /**
     * Retrieves sub/super class data for the specified data
     *
     * @param originalClass  The original class to scan for the specified sub/super-class
     * @param superClass     The sub/super-class target to locate
     * @param scannedClasses The class hierarchy of scanned data (Output Value)
     * @return whether we found sub/super class data
     */
    protected static boolean isSubclassOf(final ClassInfo originalClass, final Class<?> superClass, final Map<String, ClassInfo> scannedClasses) {
        if (!isClassGraphEnabled() || originalClass == null || superClass == null) {
            return false;
        }

        // Store the target name for the superclass
        final String superClassName = MappingUtils.getCanonicalName(superClass);

        // To track visited classes to prevent cycles and redundant checks
        final Set<ClassInfo> visitedClasses = StringUtils.newHashSet();

        // Stack to simulate the recursion
        final Deque<ClassInfo> stack = new ArrayDeque<>();
        stack.push(originalClass);

        while (!stack.isEmpty()) {
            final ClassInfo currentClass = stack.pop();

            // Mark the current class as visited
            if (visitedClasses.add(currentClass)) {
                // Get the canonical name and add it to scannedClasses
                final String className = MappingUtils.getCanonicalName(currentClass);
                scannedClasses.put(className, currentClass);

                // Check if the current class is the target superclass
                if (className.equals(superClassName)) {
                    return true;
                }

                // Add superclass to the stack if not already visited
                final ClassInfo superClassInfo = currentClass.getSuperclass();
                if (superClassInfo != null && !visitedClasses.contains(superClassInfo)) {
                    stack.push(superClassInfo);
                }
            }
        }
        return false;
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchTarget   The Super Type Class to look for
     * @param sourcePackages The root package directories to search within
     * @return The List of found classes from the search
     */
    public static Map<String, ClassInfo> getClassNamesMatchingSuperType(final Class<?> searchTarget, final String... sourcePackages) {
        return getClassNamesMatchingSuperType(StringUtils.newArrayList(searchTarget), sourcePackages);
    }

    /**
     * Attempts to cast or convert an object to the specified target class.
     * It supports casting for compatible reference types and conversion for
     * some specific incompatible types, such as String to numeric types.
     *
     * @param obj         The object to be casted or converted.
     * @param targetClass The target class to which the object should be casted or converted.
     * @param <T>         The type of the class
     * @return The casted or converted object if successful, otherwise null.
     */
    public static <T> T castOrConvert(final Object obj, final Class<T> targetClass) {
        // If the object is already assignable to the target class, cast it directly.
        if (targetClass.isAssignableFrom(obj.getClass())) {
            return targetClass.cast(obj);
        } else if (obj instanceof String) {
            // Handle conversion from String to various types.
            return convertStringToType((String) obj, targetClass);
        } else {
            UniCore.LOG.debugError("Conversion or casting not supported between " + obj.getClass().getSimpleName() + " and " + targetClass.getSimpleName());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertStringToType(final String value, final Class<T> targetType) {
        Object obj;
        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            obj = Boolean.valueOf(value);
        } else if (targetType.equals(Byte.class) || targetType.equals(byte.class)) {
            obj = Byte.valueOf(value);
        } else if (targetType.equals(Short.class) || targetType.equals(short.class)) {
            obj = Short.valueOf(value);
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            obj = Integer.valueOf(value);
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            obj = Long.valueOf(value);
        } else if (targetType.equals(Float.class) || targetType.equals(float.class)) {
            obj = Float.valueOf(value);
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            obj = Double.valueOf(value);
        } else {
            UniCore.LOG.debugError("Conversion not supported for: " + targetType.getSimpleName());
            return null;
        }
        return (T) obj;
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param loader     The {@link ClassLoader} to attempt loading with
     * @param init       Whether to initialize the class, if found
     * @param forceCache Whether to re-populate the cache entry
     * @param paths      The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> getValidClass(final ClassLoader loader, final boolean init, final boolean forceCache, final String... paths) {
        final List<String> classList = StringUtils.newArrayList(paths);
        for (String path : paths) {
            StringUtils.addEntriesNotPresent(classList, MappingUtils.getUnmappedClassesMatching(path, true));
        }

        for (String path : classList) {
            switch (path) {
                case "boolean":
                    return boolean.class;
                case "byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "int":
                    return int.class;
                case "long":
                    return long.class;
                case "float":
                    return float.class;
                case "double":
                    return double.class;
                case "char":
                    return char.class;
                case "void":
                    return void.class;
                default: {
                    if (!CLASS_CACHE.containsKey(path) || forceCache) {
                        Class<?> result = null;
                        try {
                            result = Class.forName(path, init, loader != null ? loader : Classes.getCallerClass(1).getClassLoader());
                        } catch (Throwable ignored) {
                        }
                        if (result != null) {
                            CLASS_CACHE.put(path, result);
                        }
                        return result;
                    }
                    final Class<?> result = CLASS_CACHE.get(path);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param init   Whether to initialize the class, if found
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> getValidClass(final ClassLoader loader, final boolean init, final String... paths) {
        return getValidClass(loader, init, false, paths);
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findClass(final ClassLoader loader, final String... paths) {
        return getValidClass(loader, false, paths);
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> loadClass(final ClassLoader loader, final String... paths) {
        return getValidClass(loader, true, paths);
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param paths The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findClass(final String... paths) {
        return findClass(null, paths);
    }

    /**
     * Return a valid class object, or null if not found
     *
     * @param paths The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> loadClass(final String... paths) {
        return loadClass(null, paths);
    }

    /**
     * Return whether the class list from {@link FileUtils#getClassMap()} is being iterated upon
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean canScanClasses() {
        return !ARE_CLASSES_LOADING;
    }

    /**
     * Return whether we have already performed a full class scan through {@link FileUtils#getClassMap()}
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean hasScannedClasses() {
        return ARE_CLASSES_SCANNED;
    }

    /**
     * Return whether functions utilizing ClassGraph are enabled
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isClassGraphEnabled() {
        return CLASS_GRAPH_ENABLED;
    }

    /**
     * Sets whether functions utilizing ClassGraph are enabled
     *
     * @param isEnabled if ClassGraph functions are enabled
     */
    public static void setClassGraphEnabled(final boolean isEnabled) {
        CLASS_GRAPH_ENABLED = isEnabled;
    }

    /**
     * Begin a new Thread, executing {@link FileUtils#getClassMap()}
     */
    public static void detectClasses() {
        if (isClassGraphEnabled()) {
            UniCore.getThreadFactory().newThread(FileUtils::getClassMap).start();
        }
    }

    /**
     * Retrieve and Cache all known classes within the Class Loader
     *
     * @return a map of all known classes
     */
    public static Map<String, ClassInfo> getClassMap() {
        if (isClassGraphEnabled() && canScanClasses() && !hasScannedClasses()) {
            ARE_CLASSES_LOADING = true;

            // Attempt to get all possible classes from the JVM Class Loader
            final ClassGraph graphInfo = new ClassGraph()
                    .enableClassInfo()
                    .rejectPackages(
                            "net.java", "com.sun", "com.jcraft", "com.intellij", "jdk", "akka", "ibxm", "scala",
                            "*.mixin.*", "*.mixins.*", "*.jetty.*"
                    )
                    .disableModuleScanning();
            graphInfo.addClassLoader(CLASS_LOADER);

            try (ScanResult scanResult = graphInfo.scan()) {
                for (ClassInfo result : scanResult.getAllClasses()) {
                    final String resultName = MappingUtils.getMappedPath(result.getName());
                    if (!CLASS_MAP.containsKey(resultName) && !resultName.toLowerCase().contains("mixin")) {
                        CLASS_MAP.put(resultName, result);
                    }
                }
            } catch (Throwable ex) {
                UniCore.LOG.debugError(ex);
            }

            ARE_CLASSES_LOADING = false;
            ARE_CLASSES_SCANNED = true;
        }
        return StringUtils.newHashMap(CLASS_MAP);
    }

    /**
     * Clears the list of currently detected class names
     *
     * @param allowReScan Whether to allow further re-scans to occur
     */
    public static void clearClassMap(final boolean allowReScan) {
        if (allowReScan) {
            ARE_CLASSES_SCANNED = false;
        }
        CLASS_MAP.clear();
    }

    /**
     * Clears the list of currently detected class names
     */
    public static void clearClassMap() {
        clearClassMap(false);
    }

    /**
     * Clears the list of currently cached class nameToObject retrievals
     */
    public static void clearClassCache() {
        CLASS_CACHE.clear();
    }

    /**
     * Retrieve a list of all classes matching the specified lists of paths
     *
     * @param paths A nullable list of paths to be interpreted
     * @return the resulting list
     */
    public static Map<String, ClassInfo> getClasses(final String... paths) {
        final Map<String, ClassInfo> results = StringUtils.newHashMap();
        if (!isClassGraphEnabled()) return results;

        final Map<String, Set<String>> unmappedNames = StringUtils.newHashMap();
        final boolean hasNoPaths = paths == null || paths.length == 0;
        if (!hasNoPaths) {
            for (String path : paths) {
                unmappedNames.put(path, MappingUtils.getUnmappedClassesMatching(path));
            }
        }

        for (Map.Entry<String, ClassInfo> classInfo : getClassMap().entrySet()) {
            if (classInfo != null) {
                final String classPath = classInfo.getKey();
                boolean hasMatch = hasNoPaths;
                // Attempt to Add Classes Matching any of the Source Packages
                if (!hasNoPaths) {
                    for (String path : paths) {
                        final Set<String> unmapped = unmappedNames.get(path);
                        if (classPath.startsWith(path) || unmapped.contains(classPath)) {
                            hasMatch = true;
                            break;
                        }
                    }
                }

                if (hasMatch) {
                    try {
                        results.put(classPath, classInfo.getValue());
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return results;
    }

    /**
     * Retrieve a list of files in a directory
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return the list of files found, if any
     */
    public static List<String> filesInDir(final Class<?> fallbackClass, String pathToSearch) {
        final List<String> paths = StringUtils.newArrayList();
        if (!pathToSearch.endsWith("/")) {
            pathToSearch = pathToSearch + "/";
        }

        try {
            final URI uri = getResource(fallbackClass, pathToSearch).toURI();
            FileSystem fileSystem = null;
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (Exception ex) {
                    fileSystem = FileSystems.newFileSystem(uri, StringUtils.newHashMap());
                }

                myPath = fileSystem.getPath(pathToSearch);
            } else {
                myPath = Paths.get(uri);
            }

            final Stream<Path> walk = Files.walk(myPath, 1);

            try {
                final Iterator<Path> it = walk.iterator();
                it.next();

                while (it.hasNext()) {
                    paths.add(pathToSearch + it.next().getFileName());
                }
            } catch (Throwable ex) {
                if (walk != null) {
                    try {
                        walk.close();
                    } catch (Throwable ex2) {
                        ex.addSuppressed(ex2);
                    }
                }

                throw ex;
            }

            walk.close();

            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (Exception ignored) {
        }

        return paths;
    }

    /**
     * Attempts to Retrieve the Specified Resource as an InputStream
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return The InputStream for the specified resource, if successful
     */
    public static InputStream getResourceAsStream(final Class<?> fallbackClass, final String pathToSearch) {
        InputStream in = null;
        boolean useFallback = false;

        try {
            in = CLASS_LOADER.getResourceAsStream(pathToSearch);
        } catch (Exception ex) {
            useFallback = true;
        }

        if (useFallback || in == null) {
            in = fallbackClass.getResourceAsStream(pathToSearch);
        }
        return in;
    }

    /**
     * Attempts to Retrieve the Specified Resource
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return The specified resource, if successful
     */
    public static URL getResource(final Class<?> fallbackClass, final String pathToSearch) {
        URL in = null;
        boolean useFallback = false;

        try {
            in = CLASS_LOADER.getResource(pathToSearch);
        } catch (Exception ex) {
            useFallback = true;
        }

        if (useFallback || in == null) {
            in = fallbackClass.getResource(pathToSearch);
        }
        return in;
    }

    /**
     * Applies the specified {@link Modifiers} to a {@link GsonBuilder} instance
     *
     * @param instance The {@link GsonBuilder} to interpret
     * @param args     The Command Arguments to parse
     * @return The modified {@link GsonBuilder} instance
     */
    public static GsonBuilder applyModifiers(final GsonBuilder instance, final Modifiers... args) {
        for (Modifiers param : args) {
            switch (param) {
                case DISABLE_ESCAPES:
                    instance.disableHtmlEscaping();
                    break;
                case PRETTY_PRINT:
                    instance.setPrettyPrinting();
                    break;
                default:
                    break;
            }
        }
        return instance;
    }

    /**
     * Constants representing various {@link GsonBuilder} toggles,
     * such as Disabling Escape Characters and Toggling Pretty Print
     */
    public enum Modifiers {
        /**
         * Constant for the "Disable Escapes" Modifier.
         */
        DISABLE_ESCAPES,
        /**
         * Constant for the "Pretty Print" Modifier.
         */
        PRETTY_PRINT
    }
}
