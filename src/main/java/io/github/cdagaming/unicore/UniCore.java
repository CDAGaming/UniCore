/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdagaming.unicore.integrations.logging.LoggingImpl;
import io.github.cdagaming.unicore.integrations.logging.SLF4JLogger;
import io.github.cdagaming.unicore.utils.FileUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Constant Variables and Methods used throughout the Application
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
public class UniCore {
    /**
     * The Application's Name
     */
    public static final String NAME = "@APP_NAME@";

    /**
     * The Application's Version ID
     */
    public static final String VERSION_ID = "v@VERSION_ID@";

    /**
     * The Application's Identifier
     */
    public static final String APP_ID = "unicore";

    /**
     * The Application's Instance of {@link LoggingImpl} for Logging Information
     */
    public static final LoggingImpl LOG = new SLF4JLogger(APP_ID);

    /**
     * Retrieve the Timer Instance for this Class, used for Scheduling Events
     *
     * @return the Timer Instance for this Class
     */
    public static ScheduledExecutorService getThreadPool() {
        return FileUtils.getThreadPool(NAME);
    }

    /**
     * Retrieve the Thread Factory Instance for this Class, used for Scheduling Events
     *
     * @return the Thread Factory Instance for this class
     */
    public static ThreadFactory getThreadFactory() {
        return FileUtils.getThreadFactory(NAME);
    }
}
