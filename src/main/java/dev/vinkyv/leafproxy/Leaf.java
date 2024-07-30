package dev.vinkyv.leafproxy;

import dev.vinkyv.leafproxy.config.LeafConfiguration;
import dev.vinkyv.leafproxy.logger.MainLogger;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.LogManager;

import java.text.DecimalFormat;

public class Leaf {
	private static LeafConfiguration config;

	public static void main(String[] args) {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		long startTime = System.currentTimeMillis();

		MainLogger logger = MainLogger.getLogger();

		Thread.currentThread().setName("LeafMain");
		logger.info("\u001b[38;5;158mLoading LeafProxy...");

		config = new LeafConfiguration();
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

	public static LeafConfiguration getConfig() {
		return config;
	}
}