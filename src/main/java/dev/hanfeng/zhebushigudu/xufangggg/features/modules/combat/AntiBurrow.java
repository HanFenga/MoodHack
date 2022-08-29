package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc.InstantMine;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AntiBurrow extends Module {
    public static BlockPos pos;
    private final Setting<Double> range;
    private final Setting<Boolean> toggle;

    public AntiBurrow() {
        super("AntiBurrow", "AntiBurrow", Module.Category.COMBAT, true, false, false);
        this.range = register(new Setting("Range", Double.valueOf(5.0D), Double.valueOf(1.0D), Double.valueOf(8.0D)));
        this.toggle = register(new Setting("Toggle", Boolean.valueOf(false)));
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        for (EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity) player, range) || OyVey.speedManager.getPlayerSpeed(player) > 10.0D)
                continue;
            if (target == null) {
                target = player;
                distance = AutoTrap.mc.player.getDistanceSq((Entity) player);
                continue;
            }
            if (AutoTrap.mc.player.getDistanceSq((Entity) player) >= distance)
                continue;
            target = player;
            distance = AutoTrap.mc.player.getDistanceSq((Entity) player);
        }
        return target;
    }

    public void onUpdate() {
        if (fullNullCheck())
            return;
        if (mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper)
            return;
        EntityPlayer player = getTarget(((Double) this.range.getValue()).doubleValue());
        if (((Boolean) this.toggle.getValue()).booleanValue())
            toggle();
        if (player == null)
            return;
        pos = new BlockPos(player.posX, player.posY + 0.5D, player.posZ);
        if (pos == null)
            return;
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals(pos))
                return;
            if (OyVey.moduleManager.isModuleEnabled("AutoCity") && AutoCity.target != null && ((Boolean) (AutoCity.getInstance()).priority.getValue()).booleanValue())
                return;
            if (InstantMine.breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ)))
                return;
            if (InstantMine.breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ)))
                return;
            if (mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB)
                return;
            if (OyVey.moduleManager.isModuleEnabled("Anti32k") && mc.world.getBlockState(InstantMine.breakPos).getBlock() instanceof net.minecraft.block.BlockHopper)
                return;
            if (OyVey.moduleManager.isModuleEnabled("AntiShulkerBox") && mc.world.getBlockState(InstantMine.breakPos).getBlock() instanceof net.minecraft.block.BlockShulkerBox)
                return;
        }
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !isOnLiquid() && !isInLiquid() && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
        }
    }

    private boolean isOnLiquid() {
        double y = mc.player.posY - 0.03D;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)
                    return true;
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        double y = mc.player.posY + 0.01D;
        for (int x = MathHelper.floor(mc.player.posX); x < MathHelper.ceil(mc.player.posX); x++) {
            for (int z = MathHelper.floor(mc.player.posZ); z < MathHelper.ceil(mc.player.posZ); z++) {
                BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid)
                    return true;
            }
        }
        return false;
    }
}
