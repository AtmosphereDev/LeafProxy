package dev.vinkyv.leafproxy.console;

public class ConsoleThread extends Thread {
  private final TerminalConsole console;

  public ConsoleThread(TerminalConsole console) {
    super("LeafProxyConsole");
    this.console = console;
  }

  @Override
  public void run() {
    this.console.start();
  }
}
