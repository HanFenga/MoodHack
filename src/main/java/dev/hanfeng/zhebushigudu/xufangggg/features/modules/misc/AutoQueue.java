package dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.util.ChatUtil;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Canrad
 */
public class AutoQueue extends Module {
    private static AutoQueue m_instance = new AutoQueue();

    Map<String, String> m_questionAndAnswer = new HashMap<String, String>() {{
        put("龙蛋", "B");
        put("传送", "B");
        put("大箱子", "C");
        put("小箱子", "B");
        put("HIM", "A");
        put("闪电击中", "B");
        put("官方译名", "C");
        put("数字ID", "A");//钻石
        put("火焰弹", "B");
        put("南瓜", "A");
        put("什么动物", "B");
        put("羊驼", "B");
        put("挖掘", "C");
        put("凋零", "C");
        put("圈地", "B");
        put("几格空间", "A");//无限水
        put("末影之眼", "A");
        put("红石火把", "B");
        put("几页", "A");
        put("錾", "B");
        put("附魔金", "B");
        put("开服年份", "A");
    }};
    private boolean m_firstRun;

    public AutoQueue() {
        super("AutoQueue", "Automatically queue in 2b2t.xin", Module.Category.MISC, true, false, false);
        m_firstRun = true;
        setInstance();
    }

    public static AutoQueue getINSTANCE() {
        if (m_instance == null) {
            m_instance = new AutoQueue();
        }
        return m_instance;
    }

    private void setInstance() {
        m_instance = this;
    }

    public void onEnable() {
        if (playerNullCheck())
            return;
        if ((Objects.requireNonNull(mc.getCurrentServerData())).serverIP == null && !(mc.getCurrentServerData()).serverIP.equals("2b2t.xin")) {
            ChatUtil.PrivateMessageSender.sendModuleNotifyMessagePersist(getName(), "Only support 2b2t.xin!");
            disable();
            return;
        }
        if (this.m_firstRun) {
            if (SystemTray.isSupported()) {
                ChatUtil.PrivateMessageSender.sendModuleNotifyMessagePersist(getName(), "Start Queueing!");
            } else {
                System.err.println("System tray not supported!");
            }
            this.m_firstRun = false;
        }
    }

    private boolean playerNullCheck() {
        return false;
    }

    @SubscribeEvent
    public void onGuiUpdate(GuiScreenEvent event) {
        if (fullNullCheck())
            return;
        if (event.getGui() instanceof net.minecraft.client.gui.GuiDownloadTerrain) {
            System.err.println("System tray not supported!");
            disable();
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (!(Objects.requireNonNull(mc.getCurrentServerData())).serverIP.equals("2b2t.xin"))
            return;
        if (event.getMessage().getUnformattedText().contains("§")) {
            int sec;
            String s = event.getMessage().getUnformattedText().substring(15, 17);
            if (s.contains(" ")) {
                sec = Integer.parseInt(s.substring(0, 1));
            } else {
                sec = Integer.parseInt(s);
            }
            if (SystemTray.isSupported())
                if (sec <= 2)
                    return;
        }
        String msg = event.getMessage().getUnformattedText();
        this.m_questionAndAnswer.entrySet().stream().filter(p -> msg.contains(p.getKey())).findFirst().ifPresent(Answer -> mc.player.connection.sendPacket(new CPacketChatMessage(Answer.getValue())));
    }
}