package ua.pp.kata.puzzle15plus.game;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.pp.kata.puzzle15plus.R;

/**
 * Handle the drawing of the views
 */
class GameGUI {
    static final int ANIM_MILLIS = 400;
    private final View mMainLayout; // Main container
    private final LinearLayout mTimerLayout;
    private final RelativeLayout mBoardLayout;
    private final TextView mStepsView;
    private int mBoardSize;
    private Constants mConstants;
    private View[] mTiles; // Tiles (mTiles[i] represent tile with number i
    private float[][] mBoardPalette; // defines color in HSB format (3 columns) for all tiles.


    /**
     * Create GUI of the game
     * @param context - view's context
     * @param board - array of board numbers.
     */
    GameGUI(Context context, final int[][] board) {
        mBoardSize = board.length;

        mMainLayout = LayoutInflater.from(context).inflate(R.layout.fragment_game, null);
        mTimerLayout = (LinearLayout) mMainLayout.findViewById(R.id.timerLayout);
        mBoardLayout = (RelativeLayout) mMainLayout.findViewById(R.id.boardLayout);
        mStepsView = (TextView) mMainLayout.findViewById(R.id.steps);

        mConstants = new Constants(context);
        mMainLayout.setBackgroundColor(Color.parseColor(Constants.bgColor));


        // create board
        mBoardLayout.setLayoutParams(mConstants.boardParams);
        mBoardLayout.setMinimumHeight((int) (mConstants.boardWidth_DP * mConstants.dpTOpx));
        mBoardLayout.setMinimumWidth((int) (mConstants.boardWidth_DP * mConstants.dpTOpx));

        createBoard(context, board); // create board tiles
        placeBoard(board);
        colorBoard();

        // style steps view
        mStepsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mConstants.timerTextSize_DP);
        mStepsView.setTextColor(Color.parseColor(Constants.stepsColor));

        // style labels
        TextView label = (TextView) mMainLayout.findViewById(R.id.steps_label);
        label.setTextColor(Color.parseColor(Constants.labelColor));
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mConstants.labelTextSize_DP);

        label = (TextView) mMainLayout.findViewById(R.id.time_label);
        label.setTextColor(Color.parseColor(Constants.labelColor));
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mConstants.labelTextSize_DP);

        // create and style timer
        for (int i = 0; i < 5; i++) {
            TextView temp = new TextView(context);
            temp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mConstants.timerTextSize_DP);
            temp.setLayoutParams(mConstants.timerParams);
            if (i == 2) {
                temp.setTextColor(Color.parseColor(Constants.labelColor));
            } else {
                temp.setPadding(mConstants.timerPadding_PX, 0, mConstants.timerPadding_PX, 0);
                temp.setBackgroundColor(Color.parseColor(Constants.timerColor));
                temp.setTextColor(Color.parseColor(Constants.bgColor));
            }
            mTimerLayout.addView(temp);
        }
        setTime(new int[]{0, 0, 0, 0, 0});
    }

    void updateLevel(Context context, int[][] board) {
        mBoardSize = board.length;
        mConstants = new Constants(context);
        createBoard(context, board);
        placeBoard(board);
        colorBoard();
    }

    private void createBoard(Context context, int[][] BOARD) {
        mBoardSize = BOARD.length;
        mBoardLayout.removeAllViews();
        mTiles = new Button[mBoardSize * mBoardSize];

        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                int tileNumber = BOARD[i][j];
                Button temp = new Button(context);
                temp.setLayoutParams(mConstants.tileParams); // set size of the tile

                temp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mConstants.tileTextSize_DP);
                temp.setTextColor(Color.parseColor(Constants.bgColor));
                temp.setPadding(0, 0, 0, 0);
                temp.setText(Integer.toString(tileNumber));
                temp.setId(tileNumber);

                mTiles[tileNumber] = temp;
                mBoardLayout.addView(temp);
            }
        }
    }

    /**
     * Create new board Palette
     */
    private void setBoardPalette() {
        final int LEN = mTiles.length;
//        float[][] newPalette = createPalette(LEN);
        float[][] newPalette = GameColor.createPalette(LEN);
        setBoardPalette(newPalette);
    }

    /**
     * @return hsb of board tiles.
     */
    float[][] getBoardPalette() {
        return mBoardPalette;
    }

    /**
     * Set custom board palette
     * @param palette represent HSB values of the board
     */
    private void setBoardPalette(float[][] palette) {
        mBoardPalette = palette;
    }

    /**
     * Add new parameter to distinguish from method with empty parentheses. Color board with
     * existed palette.
     * @param inner no meaning
     */
    private void colorBoard(boolean inner) {
        final int LEN = mTiles.length;
        for (int i = 1; i < LEN; i++) {
            mTiles[i].setBackgroundColor(Color.HSVToColor(mBoardPalette[i]));
        }
        mTiles[0].setVisibility(View.GONE);
    }

    void colorBoard(float[][] palette) {
        setBoardPalette(palette);
        colorBoard(true);
    }

    void colorBoard() {
        setBoardPalette();
        colorBoard(true);
    }

    void placeBoard(int[][] board)  {
        for (int i = 0, dim = board.length; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int tileNumber = board[i][j];

                // set coordinates relatively to the parent RelativeLayout
                float[] xy =  convertTileRC_ToXY(i, j);
                mTiles[tileNumber].setY(xy[1]);
                mTiles[tileNumber].setX(xy[0]);
                mTiles[tileNumber].animate().rotationY(0);
            }
        }
    }

    /**
     * Get the XY coordinates of the tile in parent Relative Layout
     * @param row row of the tile in the board
     * @param col column of the tile in the board
     */
    private float[] convertTileRC_ToXY(int row, int col) {
        float y = mConstants.tileMargin_DP * (1 + (mConstants.proportion + 1) * row) * mConstants.dpTOpx;
        float x = mConstants.tileMargin_DP * (1 + (mConstants.proportion + 1) * col) * mConstants.dpTOpx;
        return new float[] {x, y};
    }

    View getMainLayout() {
        return mMainLayout;
    }

    /**
     * Set game time in the TimerLayout.
     * @param time array with 5 elements representing time in MM:SS format
     */
    void setTime(int[] time) {
        if (time.length == 5) {
            for (int i = 0, size = mTimerLayout.getChildCount(); i < size; i++) {
                TextView temp = (TextView) mTimerLayout.getChildAt(i);
                if (i == 2) {
                    temp.setText(":");
                } else {
                    temp.setText(Integer.toString(time[i]));
                }
            }
        }
    }

    void setSteps(int steps) {
        mStepsView.setText(String.valueOf(steps));
    }

    void moveTile(Button tile, int[] coordRC) {
        int index = tile.getId();
        tile.animate().setDuration(ANIM_MILLIS);
        tile.animate().setInterpolator(new  DecelerateInterpolator());
        if (coordRC[0] < 0) { // stay
            tile.animate().rotationY(((int)(mTiles[index].getRotationY() / 180) + 1) * 180);
        } else {
            float[] xy = convertTileRC_ToXY(coordRC[0], coordRC[1]);
            tile.animate().x(xy[0]).y(xy[1]);
            tile.animate().rotationY(0);
        }
    }

    private class Constants {
        // colors
        static final String bgColor = "#FCFBF7";
        static final String timerColor = "#0AA68C";
        static final String labelColor = "#FF5722";
        static final String stepsColor = "#99D160";

        // screen const
        final float dpTOpx;
        // board const
        final float board_scale;
        final float boardWidth_DP;
        final int topBoardMargin_PX;
        final int leftBoardMargin_PX;
        // tile const
        final int proportion;
        final float tileMargin_DP;
        final int tileSize_PX;
        final float tileTextSize_DP;
        // timer const
        final float timerTextSize_DP;
        final int timerMargin_PX;
        final int timerPadding_PX;
        // label const
        final float labelTextSize_DP;
        // gui layout parameters
        private final LinearLayout.LayoutParams boardParams;
        private final LinearLayout.LayoutParams timerParams;
        private final RelativeLayout.LayoutParams tileParams;
        int screenWidth_DP;
        int screenHeight_DP;

        Constants(Context context) {

            // screen const
            dpTOpx = context.getResources().getDisplayMetrics().density;
            screenWidth_DP = (int) ((Math.min(context.getResources().getDisplayMetrics().widthPixels,
                    context.getResources().getDisplayMetrics().heightPixels) + 0.5) / dpTOpx);
            screenHeight_DP = (int) ((Math.max(context.getResources().getDisplayMetrics().widthPixels,
                    context.getResources().getDisplayMetrics().heightPixels) + 0.5) / dpTOpx);

            // correct by the size of the action bar
//            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                screenHeight_DP -= actionBarHeight;
//            } else {
//                screenWidth_DP -= actionBarHeight;
//            }

            // timer const
            timerTextSize_DP = screenWidth_DP / 8;
            timerMargin_PX = (int) (timerTextSize_DP * dpTOpx / 14);
            timerPadding_PX = (int) (timerTextSize_DP * dpTOpx / 5);

            // board and tile const
            board_scale = 0.9f; // scale the board
            boardWidth_DP = screenWidth_DP * board_scale;
            // tile const
            proportion = 10; // width of one tile equals proportion * margin
            tileMargin_DP = boardWidth_DP / (float)(mBoardSize * (proportion + 1) + 1);
            tileSize_PX = (int) (proportion * tileMargin_DP * dpTOpx);
            tileTextSize_DP = proportion * tileMargin_DP / 2;

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // center board
                leftBoardMargin_PX = (int) ((screenWidth_DP - boardWidth_DP) * dpTOpx / 2);
                // add more space in portrait orientation
                topBoardMargin_PX = (int) (Math.max(0, (screenHeight_DP - screenWidth_DP * 1.3 - 108)) * dpTOpx);

            } else {
                float k = screenWidth_DP - boardWidth_DP - 6 * timerTextSize_DP;
                leftBoardMargin_PX = (int) context.getResources().getDimension(R.dimen.game_horizontal_margin);
                topBoardMargin_PX = (int) ((screenWidth_DP - boardWidth_DP) * dpTOpx / 2 - tileMargin_DP * dpTOpx);

            }

            // label const
            labelTextSize_DP = timerTextSize_DP / 2;

            // layout parameters
            boardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            boardParams.setMargins(leftBoardMargin_PX, topBoardMargin_PX, 0, 0);

            tileParams = new RelativeLayout.LayoutParams(tileSize_PX, tileSize_PX);

            timerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            timerParams.setMargins(timerMargin_PX, 0, timerMargin_PX, 0);
        }
    }
}
