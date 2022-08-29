package dev.hanfeng.zhebushigudu.xufangggg.features.modules.misc;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Bind;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.util.*;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.PacketEvent;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.Render3DEvent;
import dev.hanfeng.zhebushigudu.xufangggg.features.modules.client.ClickGui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InstantMine extends Module {
    public static BlockPos breakPos2;

    public static BlockPos breakPos;

    private static InstantMine INSTANCE = new InstantMine();
    public final Timer Rendertimer = new Timer();
    private final Timer breakSuccess = new Timer();
    private final Setting<Boolean> creativeMode = register(new Setting("CreativeMode", Boolean.valueOf(true)));

    private final Setting<Float> range = register(new Setting("Range", Float.valueOf(256.0F), Float.valueOf(1.0F), Float.valueOf(256.0F)));

    private final Setting<Boolean> ghostHand = register(new Setting("GhostHand", Boolean.valueOf(true), v -> ((Boolean) this.creativeMode.getValue()).booleanValue()));

    private final Setting<Boolean> superghost = register(new Setting("Super GhostHand", Boolean.FALSE, v -> ((Boolean) this.ghostHand.getValue()).booleanValue()));

    private final Setting<Boolean> doubleBreak = register(new Setting("Double Break", Boolean.FALSE, v -> ((Boolean) this.ghostHand.getValue()).booleanValue()));

    private final Setting<Boolean> instant = register(new Setting("Instant", Boolean.valueOf(true)));

    private final Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true)));
    private final Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), v -> ((Boolean) this.render.getValue()).booleanValue()));
    private final List<Block> godBlocks = Arrays.asList(new Block[]{Blocks.AIR, (Block) Blocks.FLOWING_LAVA, (Block) Blocks.LAVA, (Block) Blocks.FLOWING_WATER, (Block) Blocks.WATER, Blocks.BEDROCK});
    public Setting<Mode> rendermode = register(new Setting("Render Mode", Mode.Fill, v -> ((Boolean) this.render.getValue()).booleanValue()));
    private final Setting<Integer> fillAlpha = register(new Setting("Fill Alpha", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean) this.render.getValue()).booleanValue() && this.rendermode.getValue() == Mode.Fill)));
    private final Setting<Integer> boxAlpha = register(new Setting("Box Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean) this.render.getValue()).booleanValue() && this.rendermode.getValue() == Mode.Box)));
    public Block block;
    double manxi;
    private boolean cancelStart = false;
    private boolean empty = false;
    private EnumFacing facing;

    public InstantMine() {
        super("InstantBreak+", "Instant Mine", Category.MISC, true, false, false);
        setInstance();
    }

    public static InstantMine getInstance() {
        if (INSTANCE == null)
            INSTANCE = new InstantMine();
        return INSTANCE;
    }

    public static void attackcrystal() {
        for (Entity crystal : mc.world.loadedEntityList.stream().filter(e -> (e instanceof net.minecraft.entity.item.EntityEnderCrystal && !e.isDead)).sorted(Comparator.comparing(e -> Float.valueOf(mc.player.getDistance(e)))).collect(Collectors.toList())) {
            if (crystal instanceof net.minecraft.entity.item.EntityEnderCrystal && crystal.getDistanceSq(breakPos) <= 2.0D) {
                mc.player.connection.sendPacket((Packet) new CPacketUseEntity(crystal));
                mc.player.connection.sendPacket((Packet) new CPacketAnimation(EnumHand.OFF_HAND));
            }
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (fullNullCheck())
            return;
        if (mc.player.capabilities.isCreativeMode)
            return;
        if (!this.cancelStart)
            return;
        if (((Bind) this.bind.getValue()).isDown()&& InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1 && mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR) {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int old = mc.player.inventory.currentItem;
            switchToSlot(obbySlot);
            BlockUtil.placeBlock(breakPos, EnumHand.MAIN_HAND, false, true, false);
            switchToSlot(old);
        }
        if (breakPos != null &&
                mc.player != null && mc.player.getDistanceSq(breakPos) > MathUtil.square(((Float) this.range.getValue()).floatValue())) {
            breakPos = null;
            breakPos2 = null;
            this.cancelStart = false;
            return;
        }
        if (mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR && !((Boolean) this.instant.getValue()).booleanValue()) {
            breakPos = null;
            breakPos2 = null;
            this.cancelStart = false;
            return;
        }
        if (this.godBlocks.contains(mc.world.getBlockState(breakPos).getBlock()))
        if (((Boolean) this.ghostHand.getValue()).booleanValue() || (((Boolean) this.ghostHand.getValue()).booleanValue() && ((Boolean) this.superghost.getValue()).booleanValue())) {
            float breakTime = mc.world.getBlockState(breakPos).getBlockHardness((World) mc.world, breakPos);
            int slotMain = mc.player.inventory.currentItem;
            if (!this.breakSuccess.passedMs((int) breakTime))
                return;
            if (((Boolean) this.superghost.getValue()).booleanValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) == -1)
                for (int i = 9; i < 36; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_PICKAXE) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer) mc.player);
                        mc.playerController.updateController();
                        mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer) mc.player);
                        mc.playerController.updateController();
                        return;
                    }
                }
            try {
                this.block = mc.world.getBlockState(breakPos).getBlock();
            } catch (Exception exception) {
            }
            int toolSlot = getBestAvailableToolSlot(this.block.getBlockState().getBaseState());
            if (mc.player.inventory.currentItem != toolSlot && toolSlot != -1) {
                mc.player.inventory.currentItem = toolSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                mc.player.inventory.currentItem = slotMain;
                mc.playerController.updateController();
                return;
            }
        }
        mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
    }

    public void onRender3D(Render3DEvent event) {
        if (fullNullCheck())
            return;
        if (!this.cancelStart)
            return;
        if ((breakPos != null || (((Boolean) this.instant.getValue()).booleanValue() && mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR)) &&
                mc.player != null && mc.player.getDistanceSq(breakPos) > MathUtil.square(((Float) this.range.getValue()).floatValue())) {
            breakPos = null;
            breakPos2 = null;
            this.cancelStart = false;
            return;
        }
        if (((Boolean) this.doubleBreak.getValue()).booleanValue() && ((Boolean) this.ghostHand.getValue()).booleanValue() && breakPos2 != null) {
            int slotMains = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(breakPos2).getBlock() != Blocks.AIR) {
                if (mc.world.getBlockState(breakPos2).getBlock() == Blocks.OBSIDIAN && !this.breakSuccess.passedMs(1234L))
                    return;
                breakPos2 = breakPos;
                try {
                    this.block = mc.world.getBlockState(breakPos2).getBlock();
                } catch (Exception exception) {
                }
                int toolSlot = getBestAvailableToolSlot(this.block.getBlockState().getBaseState());
                if (mc.player.inventory.currentItem != toolSlot && toolSlot != -1) {
                    mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(toolSlot));
                    mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos2, this.facing));
                }
            }
            if (mc.world.getBlockState(breakPos2).getBlock() == Blocks.AIR) {
                breakPos2 = null;
                mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(slotMains));
            }
        }
    }

    public int getBestAvailableToolSlot(IBlockState blockState) {
        int toolSlot = -1;
        double max = 0.0D;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            float speed;
            if (!stack.isEmpty && (speed = stack.getDestroySpeed(blockState)) > 1.0F) {
                int eff;
                if ((speed = (float) (speed + (((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0D) + 1.0D) : 0.0D))) > max) {
                    max = speed;
                    toolSlot = i;
                }
            }
        }
        return toolSlot;
    }

    public boolean check() {
        return (breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 3.0D, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ)) || breakPos
                .equals(new BlockPos(mc.player.posX + 1.0D, mc.player.posY, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX - 1.0D, mc.player.posY, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ + 1.0D)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ - 1.0D)) || breakPos
                .equals(new BlockPos(mc.player.posX + 1.0D, mc.player.posY + 1.0D, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX - 1.0D, mc.player.posY + 1.0D, mc.player.posZ)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ + 1.0D)) || breakPos.equals(new BlockPos(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ - 1.0D)));
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck())
            return;
        if (mc.player.capabilities.isCreativeMode)
            return;
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK)
                event.setCanceled(this.cancelStart);
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (fullNullCheck())
            return;
        if (((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.creativeMode.getValue()).booleanValue() && this.cancelStart) {
            if (this.godBlocks.contains(mc.world.getBlockState(breakPos).getBlock()))
                this.empty = true;
            float RenderTime = mc.world.getBlockState(breakPos).getBlockHardness((World) mc.world, breakPos) / 5.0F;
            if (mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN)
                RenderTime = 11.0F;
            if (this.Rendertimer.passedMs((int) RenderTime)) {
                if (this.manxi <= 10.0D)
                    this.manxi += 0.11D;
                this.Rendertimer.reset();
            }
            AxisAlignedBB axisAlignedBB = mc.world.getBlockState(breakPos).getSelectedBoundingBox((World) mc.world, breakPos);
            double centerX = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) / 2.0D;
            double centerY = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) / 2.0D;
            double centerZ = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2.0D;
            double progressValX = this.manxi * (axisAlignedBB.maxX - centerX) / 10.0D;
            double progressValY = this.manxi * (axisAlignedBB.maxY - centerY) / 10.0D;
            double progressValZ = this.manxi * (axisAlignedBB.maxZ - centerZ) / 10.0D;
            AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
            Color color = ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) (ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255);
            if (this.rendermode.getValue() == Mode.Fill) {
                RenderUtil.drawBBFill(axisAlignedBB1, color, ((Integer) this.fillAlpha.getValue()).intValue());
            } else {
                RenderUtil.drawBBBox(axisAlignedBB1, color, ((Integer) this.boxAlpha.getValue()).intValue());
            }
        }
    }

    @SubscribeEvent
    public void onBlockEvent(PlayerDamageBlockEvent event) {
        if (fullNullCheck())
            return;
        if (breakPos != null && breakPos.getX() == event.getPos().getX() && breakPos.getY() == event.getPos().getY() && breakPos.getZ() == event.getPos().getZ())
            return;
        if (breakPos2 != null && breakPos2.getX() == event.getPos().getX() && breakPos2.getY() == event.getPos().getY() && breakPos2.getZ() == event.getPos().getZ() && ((Boolean) this.doubleBreak.getValue()).booleanValue())
            return;
        if (mc.player.capabilities.isCreativeMode)
            return;
        if (BlockUtil.canBreak(event.pos)) {
            this.manxi = 0.0D;
            breakPos2 = breakPos;
            this.empty = false;
            this.cancelStart = false;
            breakPos = event.pos;
            this.breakSuccess.reset();
            this.facing = event.facing;
            if (breakPos != null) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, this.facing));
                this.cancelStart = true;
                mc.player.connection.sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                event.setCanceled(true);
            }
        }
    }

    private void switchToSlot(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public String getDisplayInfo() {
        return ((Boolean) this.ghostHand.getValue()).booleanValue() ? "Ghost" : "Normal";
    }

    public enum Mode {
        Fill, Box;
    }
}
