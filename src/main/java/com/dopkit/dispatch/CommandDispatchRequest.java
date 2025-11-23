package com.dopkit.dispatch;

/**
 * Dispatch request for command table routing.
 */
public final class CommandDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final String command;
    private final Object input;

    public CommandDispatchRequest(String command, Object input) {
        super(DispatchStrategyType.COMMAND_TABLE);
        this.command = command;
        this.input = input;
    }

    public String getCommand() {
        return command;
    }

    public Object getInput() {
        return input;
    }
}
