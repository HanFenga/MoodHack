package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc.InstantMine;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.EntityUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoCity extends Module {
    public static EntityPlayer target;
    private static AutoCity INSTANCE = new AutoCity();
    public final Setting<Boolean> priority = register(new Setting("City Priority ", Boolean.valueOf(true)));
    private final Setting<Float> range = register(new Setting("Range", Float.valueOf(5.0F), Float.valueOf(1.0F), Float.valueOf(8.0F)));
    private final Setting<Boolean> toggle = register(new Setting("AutoToggle", Boolean.valueOf(false)));

    public AutoCity() {
        super("AutoCity", "AutoCity", Module.Category.COMBAT, true, false, false);
        setInstance();
    }

    public static AutoCity getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AutoCity();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onTick() {
        if (fullNullCheck())
            return;
        if (OyVey.moduleManager.isModuleEnabled("AutoCev"))
            return;
        if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) == -1)
            return;
        target = getTarget(((Float) this.range.getValue()).floatValue());
        surroundMine();
    }

    public String getDisplayInfo() {
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    private void surroundMine() {
        if (target != null) {
            Vec3d a = target.getPositionVector();
            if (EntityUtil.getSurroundWeakness(a, 1, -1)) {
                surroundMine(a, -1.0D, 0.0D, 0.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 2, -1)) {
                surroundMine(a, 1.0D, 0.0D, 0.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 3, -1)) {
                surroundMine(a, 0.0D, 0.0D, -1.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 4, -1)) {
                surroundMine(a, 0.0D, 0.0D, 1.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 5, -1)) {
                surroundMine(a, -1.0D, 0.0D, 0.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 6, -1)) {
                surroundMine(a, 1.0D, 0.0D, 0.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 7, -1)) {
                surroundMine(a, 0.0D, 0.0D, -1.0D);
            } else if (EntityUtil.getSurroundWeakness(a, 8, -1)) {
                surroundMine(a, 0.0D, 0.0D, 1.0D);
            } else {
                target = null;
            }
        }
        if (((Boolean) this.toggle.getValue()).booleanValue())
            toggle();
    }

    private void surroundMine(Vec3d pos, double x, double y, double z) {
        BlockPos position = (new BlockPos(pos)).add(x, y, z);
        if (InstantMine.getInstance().isOff()) {
            InstantMine.getInstance().enable();
            return;
        }
        if (!InstantMine.getInstance().isOn())
            return;
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals(position))
                return;
            if (InstantMine.breakPos.equals(new BlockPos(target.posX, target.posY, target.posZ)) && mc.world.getBlockState(new BlockPos(target.posX, target.posY, target.posZ)).getBlock() != Blocks.AIR && !((Boolean) this.priority.getValue()).booleanValue())
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
        mc.playerController.onPlayerDamageBlock(position, BlockUtil.getRayTraceFacing(position));
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = range;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity) player, range))
                continue;
            if (check(player) || OyVey.friendManager.isFriend(player.getName()) || mc.player.posY - player.posY >= 5.0D)
                continue;
            if (target == null) {
                target = player;
                distance = EntityUtil.mc.player.getDistanceSq((Entity) player);
                continue;
            }
            if (EntityUtil.mc.player.getDistanceSq((Entity) player) >= distance)
                continue;
            target = player;
            distance = EntityUtil.mc.player.getDistanceSq((Entity) player);
        }
        return target;
    }

    public boolean check(EntityPlayer player) {
        return (mc.world.getBlockState(new BlockPos(player.posX + 1.0D, player.posY, player.posZ)).getBlock() == Blocks.AIR || mc.world.getBlockState(new BlockPos(player.posX - 1.0D, player.posY, player.posZ)).getBlock() == Blocks.AIR || mc.world
                .getBlockState(new BlockPos(player.posX, player.posY, player.posZ + 1.0D)).getBlock() == Blocks.AIR || mc.world.getBlockState(new BlockPos(player.posX, player.posY, player.posZ - 1.0D)).getBlock() == Blocks.AIR);
    }
}

