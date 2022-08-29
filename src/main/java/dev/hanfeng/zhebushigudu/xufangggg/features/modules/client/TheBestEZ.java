package dev.hanfeng.zhebushigudu.xufangggg.features.modules.client;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.util.ChatUtil;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class TheBestEZ extends Module {

    private final Setting<Boolean> open = this.register(new Setting<Boolean>("Open",true));

    public TheBestEZ() {
        super("TheBestEz","IF The Player A1mWare_Net Say:EZ,Then You will say EZ With him",Category.CLIENT,true,false,true);
    }
    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if ((Boolean) this.open.getValue()) {
            if (event.getMessage().getUnformattedText().contains("A1mWare_Net")) {
                StringBuilder msg = new StringBuilder("e");
                for (int i = 0; i <= getRandomNumberInRange(1, 9); ++i) {
                    msg.append("z");
                }
                ChatUtil.PublicMessageSender.sendMessageToSever(msg.toString());
            }
        }
    }
    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }



}
