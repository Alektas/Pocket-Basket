/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alektas.pocketbasket.ui.utils;

import android.view.animation.Interpolator;

/**
 * Uses a lookup table for the Bezier curve from (0,0) to (1,1) with control points:
 * P0 (0, 0)
 * P1 (0.4, 0.5)
 * P2 (0.4, 1.0)
 * P3 (1.0, 1.0)
 */
public class SmoothDecelerateInterpolator implements Interpolator {

    /**
     * Lookup table values sampled with x at regular intervals between 0 and 1 for a total of
     * 201 points.
     */
    private static final float[] mValues = new float[] {
            0.0000f, 	 0.0074f, 	 0.0149f, 	 0.0224f, 	 0.0299f, 	 0.0374f, 	 0.0449f,
            0.0524f, 	 0.0599f, 	 0.0674f, 	 0.0749f, 	 0.0824f, 	 0.0898f, 	 0.0973f,
            0.1048f, 	 0.1122f, 	 0.1197f, 	 0.1271f, 	 0.1346f, 	 0.1420f, 	 0.1495f,
            0.1569f, 	 0.1643f, 	 0.1717f, 	 0.1791f, 	 0.1865f, 	 0.1939f, 	 0.2012f,
            0.2086f, 	 0.2159f, 	 0.2233f, 	 0.2306f, 	 0.2379f, 	 0.2452f, 	 0.2525f,
            0.2598f, 	 0.2670f, 	 0.2743f, 	 0.2815f, 	 0.2887f, 	 0.296f, 	 0.3031f,
            0.3103f, 	 0.3175f, 	 0.3246f, 	 0.3318f, 	 0.3389f, 	 0.3460f, 	 0.3530f,
            0.3601f, 	 0.3671f, 	 0.3742f, 	 0.3812f, 	 0.3881f, 	 0.3951f, 	 0.4021f,
            0.4090f, 	 0.4159f, 	 0.4228f, 	 0.4296f, 	 0.4365f, 	 0.4433f, 	 0.4501f,
            0.4568f, 	 0.4636f, 	 0.4703f, 	 0.4770f, 	 0.4837f, 	 0.4903f, 	 0.4969f,
            0.5035f, 	 0.5101f, 	 0.5166f, 	 0.5231f, 	 0.5296f, 	 0.5361f, 	 0.5425f,
            0.5489f, 	 0.5553f, 	 0.5616f, 	 0.568f, 	 0.5742f, 	 0.5805f, 	 0.5867f,
            0.5929f, 	 0.5991f, 	 0.6052f, 	 0.6113f, 	 0.6174f, 	 0.6234f, 	 0.6294f,
            0.6354f, 	 0.6413f, 	 0.6472f, 	 0.6530f, 	 0.6589f, 	 0.6647f, 	 0.6704f,
            0.6761f, 	 0.6818f, 	 0.6875f, 	 0.6931f, 	 0.6986f, 	 0.7042f, 	 0.7096f,
            0.7151f, 	 0.7205f, 	 0.7259f, 	 0.7312f, 	 0.7365f, 	 0.7418f, 	 0.7470f,
            0.7521f, 	 0.7573f, 	 0.7624f, 	 0.7674f, 	 0.7724f, 	 0.7773f, 	 0.7823f,
            0.7871f, 	 0.792f, 	 0.7967f, 	 0.8015f, 	 0.8061f, 	 0.8108f, 	 0.8154f,
            0.8199f, 	 0.8244f, 	 0.8289f, 	 0.8333f, 	 0.8376f, 	 0.8419f, 	 0.8462f,
            0.8504f, 	 0.8546f, 	 0.8587f, 	 0.8627f, 	 0.8667f, 	 0.8707f, 	 0.8746f,
            0.8785f, 	 0.8822f, 	 0.8860f, 	 0.8897f, 	 0.8933f, 	 0.8969f, 	 0.9004f,
            0.9039f, 	 0.9073f, 	 0.9107f, 	 0.9140f, 	 0.9173f, 	 0.9205f, 	 0.9236f,
            0.9267f, 	 0.9297f, 	 0.9327f, 	 0.9356f, 	 0.9384f, 	 0.9412f, 	 0.944f,
            0.9466f, 	 0.9492f, 	 0.9518f, 	 0.9543f, 	 0.9567f, 	 0.9591f, 	 0.9614f,
            0.9636f, 	 0.9658f, 	 0.9679f, 	 0.9699f, 	 0.9719f, 	 0.9738f, 	 0.9757f,
            0.9775f, 	 0.9792f, 	 0.9809f, 	 0.9825f, 	 0.9840f, 	 0.9855f, 	 0.9868f,
            0.9882f, 	 0.9894f, 	 0.9906f, 	 0.9917f, 	 0.9928f, 	 0.9937f, 	 0.9947f,
            0.9955f, 	 0.9963f, 	 0.9970f, 	 0.9976f, 	 0.9981f, 	 0.9986f, 	 0.9990f,
            0.9994f, 	 0.9996f, 	 0.9998f, 	 0.9999f, 	 1.0000f

    };
    private final float mStepSize;

    public SmoothDecelerateInterpolator() {
        mStepSize = 1f / (mValues.length - 1);
    }

    @Override
    public float getInterpolation(float input) {
        if (input >= 1.0f) {
            return 1.0f;
        }
        if (input <= 0f) {
            return 0f;
        }

        // Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when
        // we lerp (linearly interpolate) in the return statement
        int position = Math.min((int) (input * (mValues.length - 1)), mValues.length - 2);

        // Calculate values to account for small offsets as the lookup table has discrete values
        float quantized = position * mStepSize;
        float diff = input - quantized;
        float weight = diff / mStepSize;

        // Linearly interpolate between the table values
        return mValues[position] + weight * (mValues[position + 1] - mValues[position]);
    }
}
