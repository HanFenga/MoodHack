package dev.hanfeng.zhebushigudu.xufangggg.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : OyVey.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + OyVey.commandManager.getPrefix() + command.getName());
        }
    }
}

