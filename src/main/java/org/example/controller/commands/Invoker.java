package org.example.controller.commands;

import java.util.Stack;

/**
 * Invoker keeps track of executed commands and allows undo.
 */
public class Invoker {
    private final Stack<Command> history = new Stack<>();

    /** Execute a command and record it */
    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    /** Undo the last command */
    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}
