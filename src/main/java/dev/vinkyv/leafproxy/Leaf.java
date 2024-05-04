package dev.vinkyv.leafproxy;

import dev.vinkyv.leafproxy.config.LeafConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

public class Leaf {
	private static final Logger logger;

	static {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		logger = LogManager.getLogger(Leaf.class);
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		Thread.currentThread().setName("LeafMain");
		logger.info("\u001b[38;5;158mLoading LeafProxy...");

		LeafConfiguration config = new LeafConfiguration();
		config.load();

		LeafServer server = new LeafServer(config);
		server.start();

		double bootTime = (System.currentTimeMillis() - startTime) / 1000d;
		logger.info("Done ({}s)!", new DecimalFormat("#.##").format(bootTime));
	}

	protected static void shutdownHook() {
		LogManager.shutdown();
		Runtime.getRuntime().halt(0);
	}

	public static Logger getLogger() {
		return logger;
	}
}
