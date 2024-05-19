package dev.vinkyv.leafproxy.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainLogger {
	private static final Logger log = LogManager.getLogger(MainLogger.class);

	private static final MainLogger logger = new MainLogger();

	public static MainLogger getLogger() {
		return logger;
	}

	public void debug(String message) {
		log.debug(message);
	}

	public void debug(String message, Object... params) {
		log.debug(message, params);
	}

	public void debug(String message, Throwable t) {
		log.debug(message, t);
	}
	
	public void info(String message) {
		log.info(message);
	}
	
	public void info(String message, Object... params) {
		log.info(message, params);
	}

	public void info(String message, Throwable t) {
		log.info(message, t);
	}

	public void warning(String message) {
		log.warn(message);
	}

	public void warning(String message, Object... params) {
		log.warn(message, params);
	}

	public void warning(String message, Throwable t) {
		log.warn(message, t);
	}

	public void error(String message) {
		log.error(message);
	}

	public void error(String message, Object... params) {
		log.error(message, params);
	}

	public void error(String message, Throwable t) {
		log.error(message, t);
	}

	public void critical(String message) {
		log.fatal(message);
	}

	public void critical(String message, Object... params) {
		log.fatal(message, params);
	}

	public void critical(String message, Throwable t) {
		log.fatal(message, t);
	}

	public void throwing(Throwable throwable) {
		log.throwing(throwable);
	}
}
