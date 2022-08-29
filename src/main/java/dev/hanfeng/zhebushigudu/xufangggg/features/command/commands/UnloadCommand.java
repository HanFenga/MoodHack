package dev.hanfeng.zhebushigudu.xufangggg.features.command.commands;

import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        OyVey.unload(true);
    }
}

