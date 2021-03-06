package com.sprelf.taptimer.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

import com.sprelf.taptimer.Emojicon.EmojiconHandler_Custom;
import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Widgets.TimerWidget;

/*
 * TapTimer - A Timer Widget App
 * Copyright (C) 2016 Dilley, Christopher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by Chris on 14.11.2016.
 * Class for rendering the current status of a widget based on settings stored under a particular
 * widget ID.
 */
public class TimerWidgetView extends BaseWidgetView
{
    private Typeface tf;
    private Paint indicatorPaint, circlePaint, gradientPaint, shadowPaint, fadePaint;
    private RectF boundingRect;

    public TimerWidgetView(Context c)
    {
        super(c);
        initialize();
    }


    public TimerWidgetView(Context c, AttributeSet as)
    {
        super(c, as);
        initialize();
    }

    /**
     * Initializes all members.
     */
    private void initialize()
    {
        // tf = Typeface.createFromAsset(getContext().getAssets(), "roboto_bold.ttf");

        int indicatorColor = getResources().getColor(R.color.TimerWidget_Indicator);
        int shadowColor = getResources().getColor(R.color.TimerWidget_Shadow);

        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStrokeCap(Paint.Cap.BUTT);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setColor(indicatorColor);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStrokeCap(Paint.Cap.BUTT);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setColor(shadowColor);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);
        gradientPaint.setAntiAlias(true);

        fadePaint = new Paint();
        fadePaint.setStyle(Paint.Style.FILL);
        fadePaint.setAntiAlias(true);
        fadePaint.setColor(getResources().getColor(R.color.fadeFilter));

        boundingRect = new RectF();
    }


    /**
     * @inheritDoc For TimerWidgetView, draws all components of the view, including the circle,
     * the gradient, the indicator, the indicator shadow, the icon, and the fade filter.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        int circleColor = prefs.getInt(TimerWidget.TIMER_COLOR + widgetId, 0);
        int icon = EmojiconHandler_Custom.getIcon(
                getContext(), prefs.getString(TimerWidget.TIMER_ICON + widgetId, ""));


        // Get the padding value of the view
        int padding = getResources().getDimensionPixelSize(R.dimen.TimerWidget_Padding);
        // Calculate the dimensions of the view
        int left = getLeft() + padding;
        int right = getRight() - padding;
        int top = getTop() + padding;
        int bottom = getBottom() - padding;
        float width = right - left;
        float height = bottom - top;
        // Calculate the scaling ratio of the app based on the smallest dimension
        float dimRatio = Math.min(width / getResources()
                                                  .getDimensionPixelSize(R.dimen.TimerWidget_DefaultWidth),
                                  height / getResources()
                                                   .getDimensionPixelSize(R.dimen.TimerWidget_DefaultHeight));
        float radius = Math.min(width, height) / 2;


        // Get the indicator thickness and the icon margin, and then scale them depending on the
        // scaling dimension ratio
        float indicatorThickness = getResources().getDimensionPixelSize(R.dimen.TimerWidget_IndicatorThickness)
                                   * dimRatio;
        float iconMargin = getResources().getDimensionPixelSize(R.dimen.TimerWidget_IconMargin)
                           * dimRatio;
        float shadowThickness = getResources().getDimensionPixelSize(R.dimen.TimerWidget_ShadowThickness)
                                * dimRatio;
        int radialGradientXOffset = getResources().getDimensionPixelSize(
                R.dimen.TimerWidget_RadialGradientXOffset);
        int radialGradientYOffset = getResources().getDimensionPixelSize(
                R.dimen.TimerWidget_RadialGradientYOffset);

        // Calculate the angle offset of the shadow based on shadow thickness
        float shadowRadius = radius - ((indicatorThickness + shadowThickness) / 2);
        float shadowCircumference = (float)(2 * shadowRadius * Math.PI);
        float shadowAngleOffset = shadowThickness / (shadowCircumference / 360);





        ///////////////////// CIRCLE
        ////////////////////////////////




        // Define the bounding rectangle for the circle
        boundingRect.set(left + (width / 2) - radius,
                         top + (height / 2) - radius,
                         right - (width / 2) + radius,
                         bottom - (height / 2) + radius);
        // Draw circle
        circlePaint.setColor(circleColor);
        canvas.drawOval(boundingRect, circlePaint);





        ///////////////////// GRADIENT
        ////////////////////////////////




        // Draw gradient, using same bounds as circle
        gradientPaint.setShader(new RadialGradient(radialGradientXOffset,
                                                 radialGradientYOffset,
                                                 radius * 4,
                                                 Color.parseColor("#66FFFFFF"),
                                                 Color.parseColor("#22000000"),
                                                 Shader.TileMode.MIRROR));
        canvas.drawOval(boundingRect, gradientPaint);






        ///////////////////// ICON
        ////////////////////////////////







        // Draw icon
        try
        {
            // Define the bounding rectangle for the icon
            boundingRect.set(left + (width / 2) - radius + iconMargin,
                             top + (height / 2) - radius + iconMargin,
                             right - (width / 2) + radius - iconMargin,
                             bottom - (height / 2) + radius - iconMargin);

            Bitmap bmp = ((BitmapDrawable) getContext().getResources()
                                                       .getDrawable(icon)).getBitmap();
            canvas.drawBitmap(bmp, null, boundingRect, null);
        } catch (Resources.NotFoundException e)
        {
            Log.d("[WidgetView]", "Icon not found.");
        }




        ///////////////////// INDICATOR
        ////////////////////////////////





        // Define the bounding rectangle for the arc
        boundingRect.set(left + (width / 2) - radius + (indicatorThickness / 2),
                         top + (height / 2) - radius + (indicatorThickness / 2),
                         right - (width / 2) + radius - (indicatorThickness / 2),
                         bottom - (height / 2) + radius - (indicatorThickness / 2));

        // Draw indicator arc
        indicatorPaint.setStrokeWidth(indicatorThickness);
        float sweepAngle = 360 * (percentage);
        canvas.drawArc(boundingRect, 270 - sweepAngle, sweepAngle, false, indicatorPaint);







        ///////////////////// SHADOW
        ////////////////////////////////






        // Define the bounding rectangle for the shadow arc
        boundingRect.set(
                left + (width / 2) - radius + ((indicatorThickness + shadowThickness) / 2),
                top + (height / 2) - radius + ((indicatorThickness + shadowThickness) / 2),
                right - (width / 2) + radius - ((indicatorThickness + shadowThickness) / 2),
                bottom - (height / 2) + radius - ((indicatorThickness + shadowThickness) / 2));

        // Draw shadow arc
        shadowPaint.setStrokeWidth(indicatorThickness + shadowThickness);
        canvas.drawArc(boundingRect, 270 - sweepAngle - shadowAngleOffset,
                       sweepAngle + (shadowAngleOffset*2),
                       false, shadowPaint);








        ///////////////////// FADE FILTER
        ////////////////////////////////






        // If the view should fade when inactive, and is inactive, draw a fade filter over it
        if (fadeIfInactive && prefs.contains(TimerWidget.TIMER_PAUSE + widgetId))
        {
            // Define the bounding rectangle for the fade filter circle
            boundingRect.set(left + (width / 2) - radius,
                             top + (height / 2) - radius,
                             right - (width / 2) + radius,
                             bottom - (height / 2) + radius);
            // Draw fade filter circle
            canvas.drawOval(boundingRect, fadePaint);
        }
    }
}
