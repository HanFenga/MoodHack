package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class AutoTotem extends Module {
    private final Setting<Boolean> strict = register(new Setting("Strict", Boolean.valueOf(true)));

    private final Setting<Integer> health = register(new Setting("Health", Integer.valueOf(13), Integer.valueOf(0), Integer.valueOf(36), v -> ((Boolean) this.strict.getValue()).booleanValue()));

    public AutoTotem() {
        super("AutoTotem", "Main Hand AutoTotem", Module.Category.COMBAT, true, false, false);
    }

    public static int getItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    public void onUpdate() {
        if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !(Offhand.mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
            return;
        if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !(Offhand.mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
            return;
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            return;
        if (mc.player.getHeldItemMainhand().getItem() == Items.TOTEM_OF_UNDYING)
            return;
        int hotBarSlot = -1;
        int totemSlot = getItemSlot(Items.TOTEM_OF_UNDYING);
        for (int l = 0; l < 9; ) {
            if (mc.player.inventory.getStackInSlot(l).getItem() != Items.TOTEM_OF_UNDYING) {
                l++;
                continue;
            }
            hotBarSlot = l;
        }
        int slot = (totemSlot < 9) ? (totemSlot + 36) : totemSlot;
        if (totemSlot != -1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer) mc.player);
            mc.playerController.windowClick(0, 36, 0, ClickType.PICKUP, (EntityPlayer) mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer) mc.player);
            if ((hotBarSlot != -1 && mc.player.getHealth() <= ((Integer) this.health.getValue()).intValue() && ((Boolean) this.strict.getValue()).booleanValue()) || (hotBarSlot != -1 && !((Boolean) this.strict.getValue()).booleanValue()))
                mc.player.inventory.currentItem = hotBarSlot;
        }
    }
}
