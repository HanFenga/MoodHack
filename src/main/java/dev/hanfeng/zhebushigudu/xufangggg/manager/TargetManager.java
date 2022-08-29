package dev.hanfeng.zhebushigudu.xufangggg.manager;

import dev.hanfeng.zhebushigudu.xufangggg.util.Globals;
import net.minecraft.entity.EntityLivingBase;

public class TargetManager implements Globals {

    private EntityLivingBase currentTarget = null;

    public void updateTarget(EntityLivingBase targetIn) {
        currentTarget = targetIn;
    }

    public EntityLivingBase getTarget() {
        return currentTarget;
    }
}

