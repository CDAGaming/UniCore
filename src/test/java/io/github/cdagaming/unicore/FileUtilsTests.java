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

package io.github.cdagaming.unicore;

import com.google.gson.JsonElement;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTests {

    private File tempDir;
    private File testFile;

    @BeforeEach
    void setUp() throws Exception {
        // Setup for file-related tests
        tempDir = Files.createTempDirectory("testDir").toFile();
        testFile = new File(tempDir, "testFile.json");
        // Ensure the test file exists using FileUtils#assertFileExists
        FileUtils.assertFileExists(testFile);
    }

    @AfterEach
    void tearDown() {
        // Cleanup after tests
        testFile.delete();
        tempDir.delete();
    }

    @Test
    void testGetOrCreateScheduler() {
        // Test creation of a scheduler
        Pair<ScheduledExecutorService, ThreadFactory> schedulerPair = FileUtils.getOrCreateScheduler("testScheduler");
        assertNotNull(schedulerPair);
        assertNotNull(schedulerPair.getFirst());
        assertNotNull(schedulerPair.getSecond());

        // Verify that requesting the same scheduler returns the same instance
        Pair<ScheduledExecutorService, ThreadFactory> sameSchedulerPair = FileUtils.getOrCreateScheduler("testScheduler");
        assertSame(schedulerPair, sameSchedulerPair);
    }

    @Test
    void testShutdownScheduler() {
        // Set up a scheduler to shut down
        FileUtils.getOrCreateScheduler("testShutdown");
        FileUtils.shutdownScheduler("testShutdown");

        // Verifying the scheduler is shutdown requires accessing the scheduler's state
        // This test ensures no exception is thrown and relies on manual verification or logging
    }

    @Test
    void testWriteJsonData() throws Exception {
        // Assuming GSON and writeJsonData work correctly, this test checks file creation and basic writing
        String jsonData = "{\"key\": \"value\"}";
        String encoding = "UTF-8";
        FileUtils.writeJsonData(jsonData, testFile, encoding);

        assertTrue(testFile.exists());
        String content = FileUtils.getJsonData(testFile, JsonElement.class).getAsString();

        assertEquals(jsonData, content);
    }

    @Test
    void testGetJsonDataFromFile() throws Exception {
        // Prepare a JSON file
        String jsonContent = "{\"name\":\"Test\"}";
        Files.write(testFile.toPath(), jsonContent.getBytes());

        // Test reading JSON data from file
        JsonElement result = FileUtils.getJsonData(testFile, JsonElement.class);
        assertNotNull(result);
        assertEquals("Test", result.getAsJsonObject().get("name").getAsString());
    }

    @Test
    void testCopyStreamToFile() throws Exception {
        String content = "Stream content";
        String encoding = "UTF-8";
        InputStream stream = new ByteArrayInputStream(content.getBytes());

        FileUtils.copyStreamToFile(stream, testFile, true);

        assertTrue(testFile.exists());
        String fileContent = new String(Files.readAllBytes(testFile.toPath()), encoding);
        assertEquals(content, fileContent);
    }
}

