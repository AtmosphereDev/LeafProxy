package dev.vinkyv.leafproxy.command;

import lombok.Getter;

@Getter
public abstract class Command {
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    // TODO: IMPLEMENT ARGS
    public boolean execute() {
        throw new UnsupportedOperationException();
    }
}
