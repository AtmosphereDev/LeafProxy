package dev.vinkyv.leafproxy.command;

import dev.vinkyv.leafproxy.command.defaults.TestCommand;

import java.util.HashMap;

public class CommandMap {
    protected final HashMap<String, Command> commands = new HashMap<>();

    public CommandMap() {
        this.registerDefault();
    }

    private void registerDefault() {
        this.register("leaf", new TestCommand("test"));
    }

    public void register(String prefix, Command command) {
        this.commands.put(prefix + ":" + command.getName(), command);
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

    public int executeCommand(String command) {
        Command target = this.getCommand(command);
        if (target == null) {
            // TODO: SEND NOT FOUND
            return -1;
        }
        int output;
        try {
            output = target.execute() ? 1 : 0;
        } catch (UnsupportedOperationException e) {
            // TODO: SEND UNKNOWN ERROR
            output = 0;
        }
        return output;
    }
}
