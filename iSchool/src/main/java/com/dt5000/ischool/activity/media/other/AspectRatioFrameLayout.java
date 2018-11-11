package com.dt5000.ischool.activity.media.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dt5000.ischool.R;


/**
 * 固定比例的FrameLayout
 * 1:1宽度主宰，设置aspectRatioEnabled=true就ok了
 * Created by gujiajia on 2017/3/24.
 */

public class AspectRatioFrameLayout extends FrameLayout {

    public static final int MEASUREMENT_WIDTH = 0;
    public static final int MEASUREMENT_HEIGHT = 1;

    private static final float DEFAULT_ASPECT_RATIO = 1f;
    private static final boolean DEFAULT_ASPECT_RATIO_ENABLED = false;
    private static final int DEFAULT_DOMINANT_MEASUREMENT = MEASUREMENT_WIDTH;

    private float aspectRatio;
    private boolean aspectRatioEnabled;
    private int dominantMeasurement;

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public AspectRatioFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public AspectRatioFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    public AspectRatioFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioComponent);
        aspectRatio = a.getFloat(R.styleable.AspectRatioComponent_aspectRatio, DEFAULT_ASPECT_RATIO);
        aspectRatioEnabled = a.getBoolean(R.styleable.AspectRatioComponent_aspectRatioEnabled,
                DEFAULT_ASPECT_RATIO_ENABLED);
        dominantMeasurement = a.getInt(R.styleable.AspectRatioComponent_dominantMeasurement,
                DEFAULT_DOMINANT_MEASUREMENT);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!aspectRatioEnabled) return;

        int newWidth;
        int newHeight;
        switch (dominantMeasurement) {
            case MEASUREMENT_WIDTH:
                newWidth = getMeasuredWidth();
                newHeight = (int) (newWidth * aspectRatio);
                break;

            case MEASUREMENT_HEIGHT:
                newHeight = getMeasuredHeight();
                newWidth = (int) (newHeight * aspectRatio);
                break;

            default:
                throw new IllegalStateException("Unknown measurement with ID " + dominantMeasurement);
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
    }

    /** Get the aspect ratio for this image view. */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /** Set the aspect ratio for this image view. This will update the view instantly. */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        if (aspectRatioEnabled) {
            requestLayout();
        }
    }

    /** Get whether or not forcing the aspect ratio is enabled. */
    public boolean getAspectRatioEnabled() {
        return aspectRatioEnabled;
    }

    /** set whether or not forcing the aspect ratio is enabled. This will re-layout the view. */
    public void setAspectRatioEnabled(boolean aspectRatioEnabled) {
        this.aspectRatioEnabled = aspectRatioEnabled;
        requestLayout();
    }

    /** Get the dominant measurement for the aspect ratio. */
    public int getDominantMeasurement() {
        return dominantMeasurement;
    }

    /**
     * Set the dominant measurement for the aspect ratio.
     *
     * @see #MEASUREMENT_WIDTH
     * @see #MEASUREMENT_HEIGHT
     */
    public void setDominantMeasurement(int dominantMeasurement) {
        if (dominantMeasurement != MEASUREMENT_HEIGHT && dominantMeasurement != MEASUREMENT_WIDTH) {
            throw new IllegalArgumentException("Invalid measurement type.");
        }
        this.dominantMeasurement = dominantMeasurement;
        requestLayout();
    }
}
