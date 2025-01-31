package dev.vinkyv.leafproxy.command.defaults;

import dev.vinkyv.leafproxy.Leaf;
import dev.vinkyv.leafproxy.command.Command;

public class EndCommand extends Command {
    public EndCommand() {
        super("end");
    }

    @Override
    public boolean execute() {
        Leaf.getLeafServer().shutdown();
        return true;
    }
}
