package dev.hanfeng.zhebushigudu.xufangggg.util;

public class FadeUtils {

    protected long start;
    protected long length;

    public FadeUtils(long ms) {
        length = ms;
        reset();
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public boolean isEnd() {
        return getTime() >= length;
    }

    protected long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void setLength(long length) {
        this.length = length;
    }

    private double getFadeOne() {
        return isEnd() ? 1.0 : (double)getTime() / length;
    }

    public double getFadeInDefault() {
        return Math.tanh((double)getTime() / (double)length * 3.0);
    }

    public double getFadeOutDefault() {
        return 1.0 - Math.tanh((double)getTime() / (double)length * 3.0);
    }

    public double getEpsEzFadeIn() {
        return 1.0 - Math.sin(0.5 * Math.PI * getFadeOne()) * Math.sin(0.8 * Math.PI * getFadeOne());
    }

    public double getEpsEzFadeOut() {
        return Math.sin(0.5 * Math.PI * getFadeOne()) * Math.sin(0.8 * Math.PI * getFadeOne());
    }

    public double easeOutQuad() {
        return 1 - (1 - getFadeOne()) * (1 - getFadeOne());
    }

    public double easeInQuad() {
        return getFadeOne() * getFadeOne();
    }

    public double def(){
        return isEnd() ? 1.0 : getFadeOne();
    }
}
