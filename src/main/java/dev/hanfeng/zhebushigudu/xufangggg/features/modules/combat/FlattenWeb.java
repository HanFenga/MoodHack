package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.EntityUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.InventoryUtil;
import net.minecraft.block.BlockWeb;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public class FlattenWeb extends Module {

    public EntityPlayer target;
    private final Setting<Float> range = this.register(new Setting<>("Range", 5.0f, 1.0f, 6.0f));
    private final Setting<Boolean> autoDisable = this.register(new Setting<>("AutoDisable", true));
    private final Setting<Boolean> chestplace = this.register(new Setting<>("Y Chest Place", true));
    private final Setting<Boolean> xzchestplace = this.register(new Setting<>("X|Z Chest Place",false));
    private final Setting<Boolean> negative = this.register(new Setting<>("-X|-Z Chest Place",false));
    public FlattenWeb() {
        super("FlattenWeb", "Automatic feetWeb", Module.Category.COMBAT, true, false, false);
    }


    public void onUpdate() {
        if (fullNullCheck())
            return;

        this.target = getTarget(this.range.getValue());
        if(target == null)
            return;
        BlockPos people = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        int chestSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if(obbySlot == -1)
            return;
        int old = mc.player.inventory.currentItem;

        if(getBlock(people.add(0,-1,0)).getBlock() == Blocks.AIR && getBlock(people.add(0,-2,0)).getBlock() != Blocks.AIR)
        {
            if(this.chestplace.getValue() && InventoryUtil.findHotbarBlock(BlockWeb.class)!= -1)
                switchToSlot(chestSlot);
            else
                switchToSlot(obbySlot);
            BlockUtil.placeBlock(people.add(0,-1,0), EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        if(getBlock(people.add(0,-1,0)).getBlock() != Blocks.AIR && getBlock(people.add(1,-1,0)).getBlock() == Blocks.AIR && getBlock(people.add(1,0,0)).getBlock() == Blocks.AIR)
        {
            if(this.negative.getValue() && InventoryUtil.findHotbarBlock(BlockWeb.class)!= -1)
                switchToSlot(chestSlot);
            else
                switchToSlot(obbySlot);
            BlockUtil.placeBlock(people.add(1,-1,0), EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        else if(getBlock(people.add(0,-1,0)).getBlock() != Blocks.AIR && getBlock(people.add(-1,-1,0)).getBlock() == Blocks.AIR && getBlock(people.add(-1,0,0)).getBlock() == Blocks.AIR)
        {
            if(this.xzchestplace.getValue() && InventoryUtil.findHotbarBlock(BlockWeb.class)!= -1)
                switchToSlot(chestSlot);
            else
                switchToSlot(obbySlot);
            BlockUtil.placeBlock(people.add(-1,-1,0), EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        else if(getBlock(people.add(0,-1,0)).getBlock() != Blocks.AIR && getBlock(people.add(0,-1,1)).getBlock() == Blocks.AIR && getBlock(people.add(0,0,1)).getBlock() == Blocks.AIR)
        {
            if(this.negative.getValue() && InventoryUtil.findHotbarBlock(BlockWeb.class)!= -1)
                switchToSlot(chestSlot);
            else
                switchToSlot(obbySlot);
            BlockUtil.placeBlock(people.add(0,-1,1), EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        else if(getBlock(people.add(0,-1,0)).getBlock() != Blocks.AIR && getBlock(people.add(0,-1,-1)).getBlock() == Blocks.AIR && getBlock(people.add(0,0,-1)).getBlock() == Blocks.AIR)
        {
            if(this.xzchestplace.getValue() && InventoryUtil.findHotbarBlock(BlockWeb.class)!= -1)
                switchToSlot(chestSlot);
            else
                switchToSlot(obbySlot);
            BlockUtil.placeBlock(people.add(0,-1,-1), EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        if (this.autoDisable.getValue())
            disable();
    }


    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = range;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, range))
                continue;
            if (OyVey.friendManager.isFriend(player.getName()) || ((mc.player.posY - player.posY) >= 5))
                continue;
            if (target == null) {
                target = player;
                distance = EntityUtil.mc.player.getDistanceSq(player);
                continue;
            }
            if (EntityUtil.mc.player.getDistanceSq(player) >= distance)
                continue;
            target = player;
            distance = EntityUtil.mc.player.getDistanceSq(player);
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