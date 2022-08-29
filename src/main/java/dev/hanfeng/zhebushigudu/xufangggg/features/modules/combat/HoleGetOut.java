package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.UpdateWalkingPlayerEvent;
import dev.hanfeng.zhebushigudu.xufangggg.util.BlockUtils;
import dev.hanfeng.zhebushigudu.xufangggg.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class HoleGetOut extends Module {
    private final Setting<Float> range = this.register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    private final Setting<Boolean> autoDisable = this.register(new Setting<>("AutoDisable", false));

    int progress = 0;
    public HoleGetOut() {
        super("HoleKicker", "HoleKicker", Category.COMBAT, true, false, false);
    }
    final static Minecraft mc = Minecraft.getMinecraft();
    EnumFacing facing;

    @Override
    public void onEnable() {
        progress = 0;
        super.onEnable();
    }

    @Override
    public void onUpdate() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == mc.player) continue;
            if (!OyVey.friendManager.isFriend(entity.getName()) && mc.player.getDistance(entity) > range.getValue())
                continue;

            if (entity instanceof EntityPlayer) {
                int pistonSlot = InventoryUtil.pickItem(33);
                int powerSlot = InventoryUtil.pickItem(152);
                int oldSlot = mc.player.inventory.currentItem;


                BlockPos pos = new BlockPos(entity).offset(EnumFacing.UP);

                if (pistonSlot == -1 || powerSlot == -1 || progress < 2) {
                    facing = getFacing(pos);
                    if (facing != null) {
                        progress++;
                    } else {
                        progress = 0;
                    }
                    return;
                }
                mc.player.inventory.currentItem = pistonSlot;
                BlockUtils event = BlockUtils.isPlaceable(pos.offset(facing), 0, true);
                if (event != null) {
                    if (!event.doPlace(true)) {
                        return;
                    }
                    mc.player.inventory.currentItem = powerSlot;
                    for (EnumFacing f : EnumFacing.values()) {
                        pos = new BlockPos(entity).offset(EnumFacing.UP).offset(facing);
                        event = BlockUtils.isPlaceable(pos.offset(f), 0, true);
                        if (BlockUtils.doPlace(event, true)) {
                            BlockUtils.doPlace(BlockUtils.isPlaceable(new BlockPos(entity), 0, false), false);
                            if (autoDisable.getValue()){
                                this.disable();
                            }
                            InventoryUtil.switchToHotbarSlot(pistonSlot,false);
                            InventoryUtil.switchToHotbarSlot(powerSlot,false);
                            InventoryUtil.switchToHotbarSlot(oldSlot,false);
                            return;
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onMove(UpdateWalkingPlayerEvent event){

        if (progress > 0) {
            switch (facing) {

                case NORTH: {
                    this.setYaw(180);
                    break;
                }
                case SOUTH: {
                    this.setYaw(0);
                    break;
                }
                case WEST: {
                    this.setYaw(90);
                    break;
                }
                case EAST: {
                    this.setYaw(-90);
                    break;
                }

            }
            event.setStage(0);
            progress++;
        }
    }
    private void setYaw(int yaw) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.isRiding()) {
            Objects.requireNonNull(mc.player.getRidingEntity()).rotationYaw = yaw;
        }
        mc.player.rotationYaw = yaw;
    }
    public EnumFacing getFacing(BlockPos position) {
        for (EnumFacing f : EnumFacing.values()) {
            final BlockPos pos = new BlockPos(position);

            if (pos.offset(f).getY() != position.getY())
                continue;

            if (!mc.world.isAirBlock(pos.offset(f, -1).offset(EnumFacing.DOWN))) {
                if (mc.world.isAirBlock(pos.offset(f, -1))) {
                    if (mc.world.isAirBlock(pos.offset(f))) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
}
