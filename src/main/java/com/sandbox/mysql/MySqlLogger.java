package com.sandbox.mysql;

import java.lang.reflect.Method;

import com.mysql.jdbc.log.Slf4JLogger;
import com.mysql.jdbc.profiler.ProfilerEvent;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

/**
 * Logging the following event for {@link ProfilerEvent}:
 * <ol>
 *	<li>ProfilerEvent.TYPE_QUERY</li>
 *	<li>ProfilerEvent.TYPE_EXECUTE</li>
 *	<li>ProfilerEvent.TYPE_SLOW_QUERY</li>
 *	<li>ProfilerEvent.TYPE_WARN</li>
 * </ol>
 */
public class MySqlLogger extends Slf4JLogger
{
	private static Logger logger = LoggerFactory.getLogger(MySqlLogger.class);

	private enum Slf4jLevel {
		Error("isErrorEnabled", "error"), Warn("isWarnEnabled", "warn"), Info("isInfoEnabled",
			"info"), Debug("isDebugEnabled", "debug"), Trace("isTraceEnabled", "trace");

		private Method isLevelEnabled;
		private Method logForSingleArgument;
		private Method logForMultipleArguments;
		private Method logForThrowable;

		Slf4jLevel(String methodNameOfIsLevelEnabled, String levelMethodName) {
			this.isLevelEnabled = findMethod(Logger.class, methodNameOfIsLevelEnabled);
			this.logForSingleArgument = findMethod(Logger.class, levelMethodName, String.class,
				Object.class);
			this.logForMultipleArguments = findMethod(Logger.class, levelMethodName, String.class,
				Object[].class);
			this.logForThrowable = findMethod(Logger.class, levelMethodName, String.class,
				Throwable.class);

			Validate.notNull(isLevelEnabled, "Can't find method for is<Level>Enabled");
			Validate.notNull(logForSingleArgument,
				"Can't find method for <level>(String, Object) of logging");
			Validate.notNull(logForMultipleArguments,
				"Can't find method for <level>(String, Object...) of logging");
			Validate.notNull(logForThrowable,
				"Can't find method for <level>(String, Throwable) of logging");
		}

		boolean isEnabled()
		{
			return (boolean) invokeMethod(isLevelEnabled, logger);
		}

		void log(String message, Object arg)
		{
			invokeMethod(logForSingleArgument, logger, message, arg);
		}

		void log(String message, Object... args)
		{
			invokeMethod(logForMultipleArguments, logger, message, args);
		}

		void log(String message, Throwable t)
		{
			invokeMethod(logForThrowable, logger, message, t);
		}
	}

	public MySqlLogger(String name) {
		super(name);
	}

	@Override
	public void logDebug(Object o)
	{
		performLog(Slf4jLevel.Debug, o);
	}

	@Override
	public void logDebug(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Debug, o, t);
	}

	@Override
	public void logError(Object o)
	{
		performLog(Slf4jLevel.Error, o);
	}

	@Override
	public void logError(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Error, o, t);
	}

	@Override
	public void logFatal(Object o)
	{
		performLog(Slf4jLevel.Error, o);
	}

	@Override
	public void logFatal(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Error, o, t);
	}

	@Override
	public void logInfo(Object o)
	{
		performLog(Slf4jLevel.Info, o);
	}

	@Override
	public void logInfo(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Info, o, t);
	}

	@Override
	public void logTrace(Object o)
	{
		performLog(Slf4jLevel.Trace, o);
	}

	@Override
	public void logTrace(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Trace, o, t);
	}

	@Override
	public void logWarn(Object o)
	{
		performLog(Slf4jLevel.Warn, o);
	}

	@Override
	public void logWarn(Object o, Throwable t)
	{
		performLog(Slf4jLevel.Warn, o, t);
	}

	private void performLog(Slf4jLevel level, Object o, Throwable t)
	{
		level.log("[MySQL JDBC] Exception", t);

		if (!needToLog(level, o)) {
			return;
		}

		outputProfileEventToLog(level, o);
	}

	private void performLog(Slf4jLevel level, Object o)
	{
		if (!needToLog(level, o)) {
			return;
		}

		outputProfileEventToLog(level, o);
	}

	private void outputProfileEventToLog(Slf4jLevel level, Object o)
	{
		ProfilerEvent profileEvent = (ProfilerEvent) o;

		level.log(
			"Type[{}] - Connection: [{}]. Duration: [{}] ms",
			new Object[] { getEventType(profileEvent), profileEvent.getConnectionId(),
				profileEvent.getEventDuration(), });
		level.log("SQL:[\n\t{}\n]", profileEvent.getMessage());
	}

	private String getEventType(ProfilerEvent profileEvent)
	{
		switch (profileEvent.getEventType()) {
		case ProfilerEvent.TYPE_PREPARE:
			return "PREPARE";
		case ProfilerEvent.TYPE_QUERY:
			return "QUERY";
		case ProfilerEvent.TYPE_EXECUTE:
			return "EXECUTE";
		case ProfilerEvent.TYPE_SLOW_QUERY:
			return "SLOW_QUERY";
		case ProfilerEvent.TYPE_WARN:
			return "WARN";
		}

		return "Unknown type of profile event";
	}

	private boolean needToLog(Slf4jLevel level, Object o)
	{
		if (!level.isEnabled()) {
			return false;
		}

		if (!(o instanceof ProfilerEvent)) {
			return false;
		}

		ProfilerEvent profileEvent = (ProfilerEvent) o;
		switch (profileEvent.getEventType()) {
		case ProfilerEvent.TYPE_PREPARE:
		case ProfilerEvent.TYPE_QUERY:
		case ProfilerEvent.TYPE_EXECUTE:
		case ProfilerEvent.TYPE_SLOW_QUERY:
		case ProfilerEvent.TYPE_WARN:
			return true;
		}

		return false;
	}

}
