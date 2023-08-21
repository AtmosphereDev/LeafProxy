package dev.vinkyv.leafproxy;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Leaf {
  private static final Logger logger;

  static {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    logger = LogManager.getLogger(Leaf.class);
  }
  
  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    LeafServer server = new LeafServer(new InetSocketAddress("0.0.0.0", 19132));
    server.start();

    double bootTime = (System.currentTimeMillis() - startTime) / 1000d;
    logger.info("Done ({}s)!", new DecimalFormat("#.##").format(bootTime));
  }

  public static Logger getLogger() {
    return logger;
  }
}
