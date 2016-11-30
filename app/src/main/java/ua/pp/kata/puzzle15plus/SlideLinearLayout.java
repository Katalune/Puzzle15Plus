package ua.pp.kata.puzzle15plus;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SlideLinearLayout extends LinearLayout {
    public static final int ERROR = -9999;

    public SlideLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        final int width = getWidth();
        return (width > 0) ? (getX() / width) : ERROR;
    }

    public void setXFraction(float xFraction) {
        // TODO: cache width
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : ERROR);
    }

    public float getYFraction() {
        final int height = getHeight();
        return (height > 0) ? (getY() / height) : ERROR;
    }

    public void setYFraction(float yFraction) {
        final int height = getHeight();
        setY((height > 0) ? (yFraction * height) : ERROR);
    }
}
