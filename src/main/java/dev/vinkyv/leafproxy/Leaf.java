package dev.vinkyv.leafproxy;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.vinkyv.leafproxy.config.LeafConfiguration;

public class Leaf {
  private static final Logger logger;

  static {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    logger = LogManager.getLogger(Leaf.class);
  }

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    LeafConfiguration config = new LeafConfiguration();
    config.load();
    
    LeafServer server = new LeafServer(config);
    server.start();

    double bootTime = (System.currentTimeMillis() - startTime) / 1000d;
    logger.info("Done ({}s)!", new DecimalFormat("#.##").format(bootTime));
  }

  public static Logger getLogger() {
    return logger;
  }
}
