package ua.pp.kata.puzzle15plus.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import ua.pp.kata.puzzle15plus.R;

public class GameTile extends CardView {
    private TextView content;

    public GameTile(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        content = new TextView(context);
        content.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.setGravity(Gravity.CENTER);
        setPreventCornerOverlap(false);
        setRadius(getResources().getDimensionPixelSize(R.dimen.tile_radius));
        setCardElevation(getResources().getDimensionPixelSize(R.dimen.tile_elevation));
        addView(content);
    }

    public void setTextSize(float tileTextSize_dp) {
        content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tileTextSize_dp);
    }

    public void setTextColor(@ColorInt int color) {
        content.setTextColor(color);
    }

    public void setText(String content) {
        this.content.setText(content);
    }

    public void setTileBackgroundColor(@ColorInt int color) {
        setCardBackgroundColor(color);
    }
}
