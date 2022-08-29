package dev.hanfeng.zhebushigudu.xufangggg.util;

public class NotificationManager {
    public static void raw(String message) {
        ChatUtil.printChatMessage(message);
    }

    public static void info(String message) {
        raw("[Info]" + message);
    }

    public static void warn(String message) {
        raw(color("6") + "[Warning]" + color("r") + message);
    }

    public static void error(String message) {
        ChatUtil.printErrorChatMessage(color("c") + "[Error]" + color("r") + message);
    }

    public static void fatal(String message) {
        ChatUtil.printErrorChatMessage(color("4") + "[Fatal]" + color("r") + message);
    }

    public static void debug(String message) {
        raw(color("a") + "[Debug]" + color("r") + message);
    }


    public static String color(String color) {
        return ChatUtil.SECTIONSIGN + color;
    }

}
