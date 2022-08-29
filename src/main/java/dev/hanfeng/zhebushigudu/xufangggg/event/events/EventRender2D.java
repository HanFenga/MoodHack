package dev.hanfeng.zhebushigudu.xufangggg.event.events;

import dev.hanfeng.zhebushigudu.xufangggg.event.EventStage;

public class EventRender2D
        extends EventStage {
    private float partialTicks;

    public EventRender2D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

