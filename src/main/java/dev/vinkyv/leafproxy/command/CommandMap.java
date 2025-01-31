package dev.vinkyv.leafproxy.command;

import dev.vinkyv.leafproxy.command.defaults.EndCommand;
import dev.vinkyv.leafproxy.command.defaults.TestCommand;
import dev.vinkyv.leafproxy.logger.MainLogger;

import java.util.HashMap;

public class CommandMap {
    protected final HashMap<String, Command> commands = new HashMap<>();

    public CommandMap() {
        this.registerDefault();
    }

    private void registerDefault() {
        this.register(new EndCommand());
        this.register(new TestCommand());
    }

    public void register(Command command) {
        this.commands.put(command.getName(), command);
    }

    public Command getCommand(String name) {
        name = name.toLowerCase();
        if (this.commands.containsKey(name)) {
            return this.commands.get(name);
        }
        return null;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public void executeCommand(String command) {
        Command target = this.getCommand(command);
        if (target == null) {
            MainLogger.getLogger().error("This command doesn't exists");
            return;
        }
        int output;
        try {
            output = target.execute() ? 1 : 0;
        } catch (UnsupportedOperationException e) {
            MainLogger.getLogger().error("Something wrong with this command");
            output = 0;
        }
    }
}
