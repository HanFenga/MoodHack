package dev.hanfeng.zhebushigudu.xufangggg.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
public class BlockRenderSmooth implements Util{
    private BlockPos lastPos;
    private BlockPos newPos;
    private final FadeUtils fade;
    public static  Timer timer = new Timer();

    public BlockRenderSmooth (BlockPos pos , long smoothLength){
        lastPos = pos;
        newPos = pos;
        fade = new FadeUtils(smoothLength);
    }

    public void setNewPos (BlockPos pos){
        if(!pos.equals(newPos)&&timer.passedMs(200)){
            lastPos = newPos;
            newPos = pos;
            fade.reset();
            timer.reset();
        }
    }

    public Vec3d getRenderPos(){
        return  Lerp(PosToVec(lastPos),PosToVec(newPos),(float) fade.easeOutQuad());
    }

    public static Vec3d Lerp (Vec3d frome , Vec3d to,float t){
        return new Vec3d(t*to.x+(1-t)*frome.x,t*to.y+(1-t)*frome.y,t*to.z+(1-t)*frome.z);
    }
    private static Vec3d PosToVec(BlockPos pos){
        return new Vec3d(pos.getX(),pos.getY(),pos.getZ());
    }

    public void begin() {
    }

    public void end() {
    }

    public Object getFullUpdate() {
        return null;
    }
}
