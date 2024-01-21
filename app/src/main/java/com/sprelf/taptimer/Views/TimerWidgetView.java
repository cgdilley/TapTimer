package com.sprelf.taptimer.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import androidx.preference.PreferenceManager;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import com.sprelf.taptimer.Models.ActiveTimer;
import com.sprelf.taptimer.Models.Prefab_Timer;
import com.sprelf.taptimer.R;
import com.sprelf.taptimer.Widgets.TimerWidget;

import androidx.emoji2.text.EmojiCompat;

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
public class TimerWidgetView extends BaseWidgetView<ActiveTimer>
{
    private Typeface tf;
    private Paint indicatorPaint, circlePaint, gradientPaint, shadowPaint, fadePaint;
    private TextPaint iconPaint;
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

        int indicatorColor = getResources().getColor(R.color.TimerWidget_IndicatorDefault);
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

        iconPaint = new TextPaint();
        iconPaint.setStyle(Paint.Style.FILL);
        iconPaint.setAntiAlias(true);
        iconPaint.setTextAlign(Paint.Align.CENTER);

        boundingRect = new RectF();
    }


    /**
     * @inheritDoc For TimerWidgetView, draws all components of the view, including the circle,
     * the gradient, the indicator, the indicator shadow, the icon, and the fade filter.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
//        EmojiCompat.init(getContext());

        ActiveTimer info = _getWidgetInfo(getContext());
        Prefab_Timer prefab = info != null ? (Prefab_Timer) info.getPrefab() : null;


        int circleColor = prefab != null ? prefab.getColor()
                                       : Color.rgb(0, 0, 0);
        CharSequence icon = prefab != null ? prefab.getIcon() : "";
        Log.d("[TimerWidgetView]", "DRAWING: " + icon);
//        if (EmojiCompat.isConfigured())
//        {
//            icon = EmojiCompat.get().process(icon);
//            if (icon == null)
//                icon = "";
//        }
        boolean paused = info != null && info.isPaused();
        boolean ready = !icon.equals("");

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
                                                 Color.parseColor("#44FFFFFF"),
                                                 Color.parseColor("#44000000"),
                                                 Shader.TileMode.MIRROR));
        canvas.drawOval(boundingRect, gradientPaint);






        ///////////////////// ICON
        ////////////////////////////////







        // Draw icon
        iconPaint.setTextSize(height / 2);
        int xPos = (int)boundingRect.centerX();
        int yPos = (int)(boundingRect.centerY() - ((iconPaint.descent() + iconPaint.ascent()) / 2));
        canvas.drawText(icon.toString(), xPos, yPos, iconPaint);




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
        if (fadeIfInactive && (paused || !ready))
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

    protected ActiveTimer _getWidgetInfo(Context c)
    {
        if (this.widgetInfo != null)
            return this.widgetInfo;

        if (this.widgetId == null)
            return null;

        int id = Integer.parseInt(this.widgetId);

        Prefab_Timer prefab = Prefab_Timer.build(c, id);
        return ActiveTimer.build(c, id, prefab);
    }
}
