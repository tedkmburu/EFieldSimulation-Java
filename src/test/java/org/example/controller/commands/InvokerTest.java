package org.example.controller.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvokerTest {

    private static class TestCommand implements Command {
        boolean executed = false;
        boolean undone = false;

        @Override
        public void execute() {
            executed = true;
        }

        @Override
        public void undo() {
            undone = true;
        }
    }

    @Test
    void testExecuteRecordsAndRuns() {
        Invoker inv = new Invoker();
        TestCommand cmd = new TestCommand();

        inv.execute(cmd);

        assertTrue(cmd.executed, "Command.execute() should have been called");
        assertFalse(cmd.undone, "Command.undo() should not have been called during execute");
    }

    @Test
    void testUndoRunsUndoOnLast() {
        Invoker inv = new Invoker();
        TestCommand cmd1 = new TestCommand();
        TestCommand cmd2 = new TestCommand();

        inv.execute(cmd1);
        inv.execute(cmd2);

        inv.undo();
        assertTrue(cmd2.undone, "undo() should be called on the last executed command");
        assertFalse(cmd1.undone, "undo() should not be called on earlier commands yet");

        inv.undo();
        assertTrue(cmd1.undone, "undo() should be called on the first executed command after second undo");
    }

    @Test
    void testUndoOnEmptyDoesNothing() {
        Invoker inv = new Invoker();
        assertDoesNotThrow(inv::undo, "Calling undo() on empty history should not throw");
    }
}