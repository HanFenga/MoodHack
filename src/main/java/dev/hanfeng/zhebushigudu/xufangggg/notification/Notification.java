package dev.hanfeng.zhebushigudu.xufangggg.notification;

import dev.hanfeng.zhebushigudu.xufangggg.OyVey;
import dev.hanfeng.zhebushigudu.xufangggg.util.AnimationUtils;
import dev.hanfeng.zhebushigudu.xufangggg.util.RenderUtil;
import dev.hanfeng.zhebushigudu.xufangggg.util.RenderUtils;
import dev.hanfeng.zhebushigudu.xufangggg.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

public class Notification {
    public String text;
    public double width, height = 30;
    public float x;
    Type type;
    public float y, position;
    public boolean in = true;
    AnimationUtils animationUtils = new AnimationUtils();
    AnimationUtils yAnimationUtils = new AnimationUtils();

    public Notification(String text, Type type) {
        this.text = text;
        this.type = type;
        width =  Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 25;
        x = (float) width;
    }

    public void onRender() {
        int i = 0;
        for (Notification notification : OyVey.notificationsManager.notifications) {
            if (notification == this) {
                break;
            }
            i++;
        }
        y = yAnimationUtils.animate((float) ((float) i * (height + 5)), y, 0.1f);
        ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
        RenderUtil.drawRectS(sr.getScaledWidth() + x - width,
                sr.getScaledHeight() - 50 - y - height,
                sr.getScaledWidth() + x,
                sr.getScaledHeight() - 50 - y,
                new Color(0, 0, 0, 165).getRGB());
        /*RenderUtil.drawShadow((float) (sr.getScaledWidth() + x - width), (float) (sr.getScaledHeight() - 50 - y - height), sr.getScaledWidth() + x, sr.getScaledHeight() - 50 - y, 5);
         */
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, ((float) (sr.getScaledWidth() + x - width + 10)), ((float) (sr.getScaledHeight() - 50f - y - 18)), new Color(204, 204, 204, 232).getRGB());
        try{
            switch(type){
                case Success:{
                    RenderUtils.bindTexture(new DynamicTexture(ImageIO.read(Objects.requireNonNull(OyVey.class.getResource("/notifi/success.png")))).getGlTextureId());
                    RenderUtils.drawTexture((int)(sr.getScaledWidth()+x-width+2),(int)(sr.getScaledHeight()-50-y-height+(height/2)-2),8,8);
                    break;
                }
                case Info:{
                    RenderUtils.bindTexture(new DynamicTexture(ImageIO.read(Objects.requireNonNull(OyVey.class.getResource("/notifi/info.png")))).getGlTextureId());
                    RenderUtils.drawTexture((int)(sr.getScaledWidth()+x-width+2),(int)(sr.getScaledHeight()-50-y-height+(height/2)-2),8,8);
                    break;
                }
                case Error:{
                    RenderUtils.bindTexture(new DynamicTexture(ImageIO.read(Objects.requireNonNull(OyVey.class.getResource("/notifi/error.png")))).getGlTextureId());
                    RenderUtils.drawTexture((int)(sr.getScaledWidth()+x-width+2),(int)(sr.getScaledHeight()-50-y-height+(height/2)-2),8,8);
                    break;
                }
            }
        }catch (Exception ignored){

        }
    }

    //
    public enum Type {
        Success,
        Error,
        Info
    }
}
