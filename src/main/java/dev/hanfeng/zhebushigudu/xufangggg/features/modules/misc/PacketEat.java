package dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc;


import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;

public class PacketEat
        extends Module {
    private static PacketEat INSTANCE = new PacketEat();

    public PacketEat() {
        super("PacketEat", "PacketEat", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static PacketEat getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new PacketEat();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

