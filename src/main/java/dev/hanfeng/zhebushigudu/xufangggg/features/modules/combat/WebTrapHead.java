package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.EntityUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.InventoryUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class WebTrapHead extends Module {
    private final Setting<Float> range = register(new Setting("Range", Float.valueOf(5.0F), Float.valueOf(1.0F), Float.valueOf(6.0F)));
    public EntityPlayer target;

    public WebTrapHead() {
        super("WebTrapHead", "Trap Head", Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        Surround.breakcrystal();
    }

    public void onTick() {
        if (fullNullCheck())
            return;
        this.target = getTarget(((Float) this.range.getValue()).floatValue());
        if (this.target == null)
            return;
        BlockPos people = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (obbySlot == -1)
            return;
        int old = mc.player.inventory.currentItem;
        if (getBlock(people.add(0, 2, 0)).getBlock() == Blocks.AIR)
            if (getBlock(people.add(1, 2, 0)).getBlock() != Blocks.AIR || getBlock(people.add(0, 2, 1)).getBlock() != Blocks.AIR || getBlock(people.add(-1, 2, 0)).getBlock() != Blocks.AIR || getBlock(people.add(0, 2, -1)).getBlock() != Blocks.AIR) {
                switchToSlot(obbySlot);
                BlockUtil.placeBlock(people.add(0, 2, 0), EnumHand.MAIN_HAND, false, true, false);
                switchToSlot(old);
            } else if (getBlock(people.add(1, 1, 0)).getBlock() != Blocks.AIR) {
                switchToSlot(obbySlot);
                BlockUtil.placeBlock(people.add(1, 2, 0), EnumHand.MAIN_HAND, false, true, false);
                switchToSlot(old);
            } else if (getBlock(people.add(-1, 1, 0)).getBlock() != Blocks.AIR) {
                switchToSlot(obbySlot);
                BlockUtil.placeBlock(people.add(-1, 2, 0), EnumHand.MAIN_HAND, false, true, false);
                switchToSlot(old);
            } else if (getBlock(people.add(0, 1, 1)).getBlock() != Blocks.AIR) {
                switchToSlot(obbySlot);
                BlockUtil.placeBlock(people.add(0, 2, 1), EnumHand.MAIN_HAND, false, true, false);
                switchToSlot(old);
            } else if (getBlock(people.add(0, 1, -1)).getBlock() != Blocks.AIR) {
                switchToSlot(obbySlot);
                BlockUtil.placeBlock(people.add(0, 2, -1), EnumHand.MAIN_HAND, false, true, false);
                switchToSlot(old);
            }
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = range;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity) player, range))
                continue;
            if (OyVey.friendManager.isFriend(player.getName()) || mc.player.posY - player.posY >= 5.0D)
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

    private void switchToSlot(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    private IBlockState getBlock(BlockPos block) {
        return mc.world.getBlockState(block);
    }
}
