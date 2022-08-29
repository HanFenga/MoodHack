package dev.hanfeng.zhebushigudu.xufangggg.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer() {
        return Wrapper.getMinecraft().player;
    }

    public static World getWorld() {
        return Wrapper.getMinecraft().world;
    }

    public static int getKey(String keyname) {
        return Keyboard.getKeyIndex((String)keyname.toUpperCase());
    }

}