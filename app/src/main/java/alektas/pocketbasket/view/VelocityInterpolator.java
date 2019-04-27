package alektas.pocketbasket.view;

import android.view.animation.Interpolator;

public class VelocityInterpolator implements Interpolator {
    private float mVelocity = 0f;

    public VelocityInterpolator(float velocity) {
        mVelocity = velocity;
    }

    @Override
    public float getInterpolation(float input) {
        float factor = (int) (Math.abs(mVelocity)/2000);
        if (factor == 0) return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        return (float)(1.0f - Math.pow((1.0f - input), 1 + factor));
    }

    public void setVelocity(float velocity) {
        mVelocity = velocity;
    }
}
