/*
 *     TapTimer - A Timer Widget App
 *     Copyright (C) 2016 Dilley, Christopher
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sprelf.taptimer.Utils;

import android.graphics.Color;

/**
 * Created by Chris on 21.11.2016.
 */

public abstract class ColorUtils
{

    /**
     * Calculates the color that contrasts the given color (either black or white).
     *
     * @param color Color to find the contrast of.
     * @return Returns black if the given color's luminance is greater than 0.6, white otherwise.
     */
    public static int getContrastColor(int color)
    {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
        return hsv[2] > 0.60f ? Color.BLACK : Color.WHITE;
    }


    /**
     * Calculates the average of the two given colors, with the second value weighted by the given
     * weight between 0 and 1.
     *
     * @param color1 First color to average (is multiplied by 1 minus the weight)
     * @param color2 Second color to average (is multiplied by weight)
     * @param weight The weight of the second color
     * @return The weighted average of the two colors
     */
    public static int averageColor(int color1, int color2, float weight)
    {
        int red = (int) ((Color.red(color1) * (1 - weight)) + (Color.red(color2) * weight));
        int green = (int) ((Color.green(color1) * (1 - weight)) + (Color.green(color2) * weight));
        int blue = (int) ((Color.blue(color1) * (1 - weight)) + (Color.blue(color2) * weight));
        int alpha = (int) ((Color.alpha(color1) * (1 - weight)) + (Color.alpha(color2) * weight));
        return Color.argb(alpha, red, green, blue);
    }

}
