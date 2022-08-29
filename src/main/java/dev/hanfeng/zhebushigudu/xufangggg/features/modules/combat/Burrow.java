package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Burrow extends Module {

    private int oldSlot = -1;
    private BlockPos originalPos;
    public Setting<Boolean> rotate;
    private final Setting<Double> offset;

    public Burrow() {
        super("Burrow", "Rubberbands you into a block", Category.COMBAT, true, false, false);
        this.rotate = register(new Setting("Rotate", true));
        this.offset = register(new Setting("Offset", 10.0D, -10.0D, 10.0D));

    }

    public void onEnable() {
        this.originalPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock().equals(Blocks.ENDER_CHEST) ||intersectsWithEntity(this.originalPos)) {
            toggle();
            return;
        }
        this.oldSlot = mc.player.inventory.currentItem;
    }

    public void onUpdate() {
        if (getBlockSlot(Blocks.OBSIDIAN) == -1 && getBlockSlot(Blocks.ENDER_CHEST) == -1) {
            toggle();
            return;
        }
        if(getBlockSlot(Blocks.OBSIDIAN) != -1)
            switchToSlot(getBlockSlot(Blocks.OBSIDIAN), false);
        else
            switchToSlot(getBlockSlot(Blocks.ENDER_CHEST), false);
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.06610926093821D, mc.player.posZ, true));
        placeBlock(this.originalPos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), false);
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.offset.getValue(), mc.player.posZ, false));
        switchToSlot(this.oldSlot, false);
        toggle();
    }

    public List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<>();
        if (mc.world == null || pos == null)
            return facings;
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false) && !blockState.getMaterial().isReplaceable())
                facings.add(side);
        }
        return facings;
    }

    public void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float)(vec.x - pos.getX());
            float f1 = (float)(vec.y - pos.getY());
            float f2 = (float)(vec.z - pos.getZ());
            mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = null;
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext())
            side = iterator.next();
        if (side == null)
            return isSneaking;
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, true);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return (sneaking || isSneaking);
    }

    private boolean intersectsWithEntity(BlockPos pos) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!entity.equals(mc.player) &&
                    !(entity instanceof net.minecraft.entity.item.EntityItem) && (
                    new AxisAlignedBB(pos)).intersects(entity.getEntityBoundingBox()))
                return true;
        }
        return false;
    }

    public static int getBlockSlot(Block block) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack == ItemStack.EMPTY)
                return -1;
            if (itemStack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock)itemStack.getItem();
                if (itemBlock.getBlock() == block)
                    return i;
            }
        }
        return -1;
    }

    public void switchToSlot(int Slot, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(Slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(Slot));
            mc.player.inventory.currentItem = Slot;
            mc.playerController.updateController();
        }
    }
}