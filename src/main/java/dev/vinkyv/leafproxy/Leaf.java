package dev.vinkyv.leafproxy;

import dev.vinkyv.leafproxy.config.LeafConfig;
import dev.vinkyv.leafproxy.config.LeafConfigLoader;
import dev.vinkyv.leafproxy.logger.MainLogger;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;

import java.text.DecimalFormat;

public class Leaf {
	@Getter
    private static LeafConfig config;
	@Getter
	private static LeafServer leafServer;

	public static void main(String[] args) {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
		long startTime = System.currentTimeMillis();

		MainLogger logger = MainLogger.getLogger();

		Thread.currentThread().setName("LeafMain");
		logger.info("\u001b[38;5;158mLoading LeafProxy...");

		config = new LeafConfigLoader().load();

		leafServer = new LeafServer(config);
		leafServer.start();

		double bootTime = (System.currentTimeMillis() - startTime) / 1000d;
		logger.info("Done ({}s)!", new DecimalFormat("#.##").format(bootTime));
	}

	protected static void shutdownHook() {
		LogManager.shutdown();
		Runtime.getRuntime().halt(0);
	}

}