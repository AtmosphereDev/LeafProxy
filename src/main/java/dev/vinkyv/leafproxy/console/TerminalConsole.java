package dev.vinkyv.leafproxy.console;

import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import lombok.Getter;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class TerminalConsole extends SimpleTerminalConsole {

  private final LeafServer proxy;
  @Getter
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
    if (command.startsWith("/")) {
      command = command.substring(1);
    }
    proxy.commandMap.executeCommand(command);
  }

  @Override
  protected LineReader buildReader(LineReaderBuilder builder) {
    builder.completer(new CommandCompleter(proxy));
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
}
