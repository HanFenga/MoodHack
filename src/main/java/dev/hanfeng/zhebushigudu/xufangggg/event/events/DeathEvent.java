package dev.hanfeng.zhebushigudu.xufangggg.event.events;

import dev.hanfeng.zhebushigudu.xufangggg.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent
        extends EventStage {
    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }
}

