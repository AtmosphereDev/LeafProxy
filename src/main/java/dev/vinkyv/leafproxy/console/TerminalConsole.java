package dev.vinkyv.leafproxy.console;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.LeafServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

public class TerminalConsole extends SimpleTerminalConsole {

  private final LeafServer proxy;
  private final ConsoleThread consoleThread;

  public TerminalConsole(LeafServer proxy) {
    this.proxy = proxy;
    this.consoleThread = new ConsoleThread(this);
  }

  @Override
  protected boolean isRunning() {
    return this.proxy.isRunning();
  }

  @Override
  protected void runCommand(String command) {
    Leaf.getLogger().error("This command doesn't exists");
  }

  @Override
  protected LineReader buildReader(LineReaderBuilder builder) {
    builder.appName("LeafProxy");
    builder.option(LineReader.Option.HISTORY_BEEP, false);
    builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
    builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
    return super.buildReader(builder);
  }

  @Override
  protected void shutdown() {
    this.proxy.shutdown();
  }

  public ConsoleThread getConsoleThread() {
    return this.consoleThread;
  }
}
