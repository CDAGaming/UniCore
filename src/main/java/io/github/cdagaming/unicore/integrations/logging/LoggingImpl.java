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

package io.github.cdagaming.unicore.integrations.logging;

import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.List;

/**
 * Set of Utilities used to Parse Logging Information
 *
 * @author CDAGaming
 */
public abstract class LoggingImpl {
    /**
     * Name of the Logger
     */
    private final String loggerName;
    /**
     * Whether this Logger is operating in Debug Mode
     */
    private boolean debugMode;

    /**
     * Initializes a new Logger
     *
     * @param loggerName The name of the Logger
     * @param debug      Whether to initialize the logger in debug mode
     */
    public LoggingImpl(final String loggerName, final boolean debug) {
        this.loggerName = loggerName;
        this.debugMode = debug;
    }

    /**
     * Initializes a new Logger
     *
     * @param loggerName The name of the Logger
     */
    public LoggingImpl(final String loggerName) {
        this(loggerName, false);
    }

    /**
     * Retrieve the name of the logger
     *
     * @return the name of the logger
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * Get whether this {@link LoggingImpl} is in Debug Mode
     *
     * @return the debug mode status
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Set whether this {@link LoggingImpl} is in Debug Mode
     *
     * @param debugMode the new debug mode status
     */
    public void setDebugMode(final boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Sends a Message with an ERROR Level to Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void error(final String logMessage, Object... logArguments) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a Message with an ERROR Level to Logs
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void error(final String logMessage, Throwable ex) {
        error(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an ERROR Level to Logs
     *
     * @param ex The exception to print
     */
    public void error(Throwable ex) {
        error(StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an WARNING Level to Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void warn(final String logMessage, Object... logArguments) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a Message with an WARNING Level to Logs
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void warn(final String logMessage, Throwable ex) {
        warn(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an WARNING Level to Logs
     *
     * @param ex The exception to print
     */
    public void warn(Throwable ex) {
        warn(StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an INFO Level to Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void info(final String logMessage, Object... logArguments) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a Message with an INFO Level to Logs
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void info(final String logMessage, Throwable ex) {
        info(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an INFO Level to Logs
     *
     * @param ex The exception to print
     */
    public void info(Throwable ex) {
        info(StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an INFO Level to Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugInfo(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            info("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an INFO Level to Logs, if in Debug Mode
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void debugInfo(final String logMessage, Throwable ex) {
        debugInfo(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an INFO Level to Logs, if in Debug Mode
     *
     * @param ex The exception to print
     */
    public void debugInfo(Throwable ex) {
        debugInfo(StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an WARNING Level to Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugWarn(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            warn("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an WARNING Level to Logs, if in Debug Mode
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void debugWarn(final String logMessage, Throwable ex) {
        debugWarn(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an WARNING Level to Logs, if in Debug Mode
     *
     * @param ex The exception to print
     */
    public void debugWarn(Throwable ex) {
        debugWarn(StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an ERROR Level to Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugError(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            error("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an ERROR Level to Logs, if in Debug Mode
     *
     * @param logMessage The Log Message to Send
     * @param ex         The exception to print alongside the message
     */
    public void debugError(final String logMessage, Throwable ex) {
        debugError(logMessage + "\n" + StringUtils.getStackTrace(ex));
    }

    /**
     * Sends a Message with an ERROR Level to Logs, if in Debug Mode
     *
     * @param ex The exception to print
     */
    public void debugError(Throwable ex) {
        debugError(StringUtils.getStackTrace(ex));
    }

    /**
     * Retrieve the prefix to be displayed before logged messages
     *
     * @return the logging prefix, if any
     */
    public String getPrefix() {
        return "";
    }

    /**
     * Parse the specified message for Log Messages
     *
     * @param message The message to interpret
     * @param args    The formatting arguments to be applied to the message
     * @return the formatted message
     */
    public String parse(final String message, Object... args) {
        return getPrefix() + StringUtils.normalize(
                String.format(message, args)
        );
    }

    /**
     * Prints a detailed exception message, based on specified arguments
     *
     * @param ex            The exception to print alongside the message
     * @param showLogging   Whether to display additional logging for this function
     * @param prefix        The logging prefix to use before the message
     * @param verbosePrefix The logging prefix to use, if showLogging is false
     * @param outputs       If specified, attach the logging to these {@link Appendable} objects
     */
    public void printStackTrace(final Throwable ex, final boolean showLogging, final String prefix, final String verbosePrefix, final Appendable... outputs) {
        final List<String> splitEx = StringUtils.splitTextByNewLine(
                StringUtils.getStackTrace(ex)
        );

        // Dispatch to Appendable WriteStream(s) if possible
        if (outputs != null) {
            for (Appendable output : outputs) {
                if (output != null) {
                    try {
                        if (showLogging) {
                            for (String line : splitEx) {
                                line = line.replace("\t", StringUtils.TAB_SPACE);
                                output.append(line).append('\n');
                            }
                        } else {
                            output.append(splitEx.get(0)).append('\n');
                            if (splitEx.size() > 1) {
                                output.append('\n').append(verbosePrefix).append('\n');
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (showLogging) {
            error(prefix);
            error(ex);
        } else {
            error("%1$s \"%2$s\"", prefix, splitEx.get(0));
            if (splitEx.size() > 1) {
                error(verbosePrefix);
            }
        }
    }

    /**
     * Prints a detailed exception message, based on specified arguments
     *
     * @param ex            The exception to print alongside the message
     * @param prefix        The logging prefix to use before the message
     * @param verbosePrefix The logging prefix to use, if showLogging is false
     * @param outputs       If specified, attach the logging to these {@link Appendable} objects
     */
    public void printStackTrace(final Throwable ex, final String prefix, final String verbosePrefix, final Appendable... outputs) {
        printStackTrace(ex, isDebugMode(), prefix, verbosePrefix, outputs);
    }
}
