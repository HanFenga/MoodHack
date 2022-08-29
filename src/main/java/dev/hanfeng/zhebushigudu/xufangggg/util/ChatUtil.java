package dev.hanfeng.zhebushigudu.xufangggg.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil {

    public static char SECTIONSIGN = '\u00A7';

    private static final int ChatLineId = 777;

    /**
     * Print the message in the chat bar.
     * And other players can not see this message.
     *
     * @author HanFeng
     * @version 1.0 2022/6/22
     */
    public static class PrivateMessageSender {

        /**
         * The method is used to send the private message.
         * If the second param is false,then the message will stay in the char bar.
         *
         * @param message the message
         * @param delete  is delete
         */
        public static void sendPrivateMessage(String message, boolean delete) {
            if (Module.fullNullCheck()) {
                return;
            }
            if (delete) {
                Util.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(message), ChatLineId);
            } else {
                Util.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
            }
        }

        /**
         * This method is used by the module to send notify messages.
         * Messages will not persist.
         *
         * @param message message to notify
         */
        public static void sendModuleNotifyMessageNoPersist(String moduleName, String message) {
            sendPrivateMessage(OyVey.commandManager.getClientMessage() + ChatFormatting.WHITE + " [" + ChatFormatting.BLUE + moduleName + ChatFormatting.WHITE + "] " + ChatFormatting.GRAY + message, true);
        }

        /**
         * This method is used by the module to send notify messages.
         * Messages will persist.
         *
         * @param message message to notify
         */
        public static void sendModuleNotifyMessagePersist(String moduleName, String message) {
            sendPrivateMessage(OyVey.commandManager.getClientMessage() + ChatFormatting.WHITE + " [" + ChatFormatting.BLUE + moduleName + ChatFormatting.WHITE + "] " + ChatFormatting.GRAY + message, false);
        }
    }

    public static void printChatMessage(String message) {
        printRawChatMessage(SECTIONSIGN + "7[" + SECTIONSIGN + "b" + OyVey.MODNAME + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message);
    }

    private static void ChatMessage(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
    }

    public static void printRawChatMessage(String message) {
        if (Minecraft.getMinecraft().player == null) return;
        ChatMessage(message);
    }

    public static void printErrorChatMessage(String message) {
        printRawChatMessage(SECTIONSIGN + "7[" + SECTIONSIGN + "4" + SECTIONSIGN + "lERROR" + SECTIONSIGN + "7] " + SECTIONSIGN + "r" + message);
    }

    /**
     * Let the current player send message to the sever.
     * And other players will see this message.
     *
     * @author Hanfeng
     * @version 1.0 2022/6/22
     */
    public static class PublicMessageSender {
        /**
         * Let the current player send message to the sever.
         * The message finally will in the chat bar.
         * And other players will see this message.
         *
         * @param message the message
         */
        public static void sendMessageToSever(String message) {
            if (Module.fullNullCheck()) {
                return;
            }
            Util.mc.player.connection.sendPacket(new CPacketChatMessage(message));
        }
    }
}