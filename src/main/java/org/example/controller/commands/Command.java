package org.example.controller.commands;

public interface Command {
    /** Execute this command */
    void execute();
    /** Undo this command */
    void undo();
}
