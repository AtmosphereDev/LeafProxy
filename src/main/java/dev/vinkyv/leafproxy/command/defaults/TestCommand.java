package dev.vinkyv.leafproxy.command.defaults;

import dev.vinkyv.leafproxy.command.Command;
import dev.vinkyv.leafproxy.logger.MainLogger;

public class TestCommand extends Command {
    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute() {
        MainLogger.getLogger().info("TestCommand executed!");
        return true;
    }
}