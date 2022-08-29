package dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.PacketEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {
    public ChatSuffix() {
        super("ChatSuffix", "Appends your message", Module.Category.MISC, true, false, false);
    }
    public Setting<Boolean> prefix = register(new Setting<Boolean>("prefix", true));
    public Setting<String> custom = register(new Setting<String>("Custom", ">",v ->prefix.getValue()));
    public Setting<Boolean> bb = register(new Setting<Boolean>("edition", true));
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() != 0) return;
        if(OyVey.moduleManager.getModuleByName("AutoQueue").isEnabled()) return;
        if (!(event.getPacket() instanceof CPacketChatMessage)) return;
        CPacketChatMessage packet = event.getPacket();
        String message = packet.getMessage();
        if (message.startsWith("/")) {
            return;
        }

        if(prefix.getValue()){
            if ((message = custom.getValue()+message + "  >  KKGod").length() >= 256) {
                message = message.substring(0, 256);
            }
        }else {
            if ((message = message + "  >  KKGod").length() >= 256) {
                message = message.substring(0, 256);
            }
        }
        if(bb.getValue()){
            if ((message = message +"-"+ OyVey.ID).length() >= 256) {
                message = message.substring(0, 256);
            }
        }
        packet.message = message;
    }
}