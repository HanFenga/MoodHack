package dev.hanfeng.zhebushigudu.xufangggg.features.modules.combat;

import dev.hanfeng.zhebushigudu.xufangggg.features.modules.Module;
import dev.hanfeng.zhebushigudu.xufangggg.features.setting.Setting;
import dev.hanfeng.zhebushigudu.xufangggg.util.*;
import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.EventRender2D;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.EventRender3D;
import dev.hanfeng.zhebushigudu.xufangggg.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Killaura extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
    public Setting<Boolean> delay = register(new Setting("HitDelay", Boolean.valueOf(true)));
    public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
    public Setting<Boolean> onlySharp = register(new Setting("SwordOnly", Boolean.valueOf(true)));
    public Setting<Float> raytrace = register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), "Wall Range."));
    public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
    public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
    public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
    public Setting<Boolean> vehicles = register(new Setting("Entities", Boolean.valueOf(false)));
    public Setting<Boolean> projectiles = register(new Setting("Projectiles", Boolean.valueOf(false)));
    public Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true)));
    public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));

    public Setting<Boolean> esp = register(new Setting("ESP",Boolean.valueOf(true)));

    public Killaura() {
        super("KillAura", "Kills aura.", Module.Category.COMBAT, true, false, false);
    }

    public void onTick() {
        if (!this.rotate.getValue().booleanValue())
            doKillaura();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue())
            doKillaura();
    }

    private void doKillaura() {
        if (this.onlySharp.getValue().booleanValue() && !EntityUtil.holdingWeapon(mc.player)) {
            target = null;
            return;
        }
        int wait = !this.delay.getValue().booleanValue() ? 0 : (int) (DamageUtil.getCooldownByWeapon(mc.player) * (this.tps.getValue().booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F));
        if (!this.timer.passedMs(wait))
            return;
        target = getTarget();
        if (target == null)
            return;
        if (this.rotate.getValue().booleanValue())
            OyVey.rotationManager.lookAtEntity(target);
        EntityUtil.attackEntity(target, this.packet.getValue().booleanValue(), true);
        this.timer.reset();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue().floatValue();
        double maxHealth = 36.0D;
        for (Entity entity : mc.world.playerEntities) {
            if (((!this.players.getValue().booleanValue() || !(entity instanceof EntityPlayer)) && (!this.animals.getValue().booleanValue() || !EntityUtil.isPassive(entity)) && (!this.mobs.getValue().booleanValue() || !EntityUtil.isMobAggressive(entity)) && (!this.vehicles.getValue().booleanValue() || !EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity))) || (entity instanceof net.minecraft.entity.EntityLivingBase &&
                    EntityUtil.isntValid(entity, distance)))
                continue;
            if (!mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.player.getDistanceSq(entity) > MathUtil.square(this.raytrace.getValue().floatValue()))
                continue;
            if (target == null) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer) entity, 18)) {
                target = entity;
                break;
            }
            if (mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (EntityUtil.getHealth(entity) < maxHealth) {
                target = entity;
                distance = mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
        }
        return target;
    }

    private float yPos = 0.5f;
    private boolean dir;
    public void onRendr3D(EventRender3D e) {
        if (target!= null &&esp.getValue()){
            drawShadow(target,e.getPartialTicks(),yPos,dir);
            drawCirele(target,e.getPartialTicks(),yPos);
        }
    }
    TimerUtil timerUtil = new TimerUtil();


    public void onRender2D(EventRender2D e) {
        if (timerUtil.delay(15)) {
            if (dir) {
                yPos += 0.05;
                if (2 - yPos < 0.05) {
                    dir = false;
                }
            } else {
                yPos -= 0.05;
                if (yPos < 0.05) {
                    dir = true;
                }
            }
            timerUtil.reset();
        }
    }
    //杀戮渲染！
    private void drawShadow(Entity e,float partialTicks, float pos,boolean dir) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7425);;
        double x =e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y =e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z =e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i=0;i<+180;i++) {
            double c = i* Math.PI * 2 / 180;
            double c1 = (i+1) * Math.PI * 2/180;
            GlStateManager.color(1,1,1,0.3f);
            GL11.glColor4f(1,1,1,0.3f);
            GL11.glVertex3d(x+0.5f*Math.cos(c),y,z+0.5*Math.sin(c));
            GL11.glVertex3d(x+0.5f*Math.cos(c1),y,z+0.5*Math.sin(c1));

            GL11.glColor4f(1,1,1,1);

            GL11.glVertex3d(x+0.5f*Math.cos(c),y + (dir?-0.2: 0.2),z+0.5*Math.sin(c));
            GL11.glVertex3d(x+0.5f*Math.cos(c1),y + (dir?-0.2: 0.2),z+0.5*Math.sin(c1));
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel((int) 7424);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

    }
    private void drawCirele(Entity e,float partialTicks, float pos) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel((int) 7425);
        GL11.glLineWidth(1);
        double x =e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) partialTicks - mc.getRenderManager().viewerPosX;
        double y =e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) partialTicks - mc.getRenderManager().viewerPosY + pos;
        double z =e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) partialTicks - mc.getRenderManager().viewerPosZ;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i=0;i<+180;i++) {
            double c = i* Math.PI * 2 / 180;
            GlStateManager.color(1,1,1,1);
            GL11.glColor4f(1,1,1,0.3f);
            GL11.glVertex3d(x+0.5f*Math.cos(c),y,z+0.5*Math.sin(c));
        }
        GL11.glEnd();
        GL11.glShadeModel((int) 7424);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

    }


    public String getDisplayInfo() {
        if (target instanceof EntityPlayer)
            return target.getName();
        return null;
    }
}
