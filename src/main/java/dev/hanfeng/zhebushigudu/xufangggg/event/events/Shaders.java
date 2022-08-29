package dev.hanfeng.zhebushigudu.xufangggg.event.events;

public class Shaders {
    public static String packNameNone;
    static IShaderPack shaderPack = null;
    public static String getShaderPackName()
    {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }
}
