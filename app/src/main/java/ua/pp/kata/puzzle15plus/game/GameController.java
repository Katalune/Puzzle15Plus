package ua.pp.kata.puzzle15plus.game;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import ua.pp.kata.puzzle15plus.Highscores;
import ua.pp.kata.puzzle15plus.MainActivity;
import ua.pp.kata.puzzle15plus.R;
import ua.pp.kata.puzzle15plus.StorageUtils;

/**
 * Create game view and game model. Set OnTouchListeners on board tiles and timer.
 */
class GameController implements GreetingDialogFragment.GreetingDialogListener,
        View.OnTouchListener {
    static final String GREETING = "greeting";
    private GameModel mBoard;
    private GameGUI mGui;
    private int mDimension;
    private long mStartTime; // timer start time
    private long mSpentTime = 0;
    private android.os.Handler handler;
    private Runnable updateTimer;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private boolean mIsHighscore = false;

    /**
     * Recreate game controller.
     * @param context game context.
     * @param model game model.
     * @param time time spent in game previously.
     * @param palette game palette (hsb).
     */
    GameController(Context context, GameModel model, long time, float[][] palette) {
        this.mContext = context;
        mBoard = model;
        mDimension = model.getBoard().length;
        mGui = new GameGUI(context, mBoard.getBoard());
        mGui.colorBoard(palette);
        mSpentTime = time;

        init();
    }

    /**
     * Create new game controller.
     * @param context game context.
     * @param dim game board dimension.
     */
    GameController(Context context, int dim) {
        this.mContext = context;
        mDimension = dim;
        mBoard = new GameModel(mDimension);
        mGui = new GameGUI(context, mBoard.getBoard());
        mSpentTime = 0;

        init();
    }

    private void init() {
        mGui.setSteps(mBoard.getStepsNumber());
        mSharedPref = StorageUtils.getPrefs(mContext);
        mStartTime = SystemClock.uptimeMillis() - mSpentTime; // add to the whole period previous time
        RelativeLayout boardLayout = (RelativeLayout) mGui.getMainLayout().findViewById(R.id.boardLayout);
        for (int i = 0, size = boardLayout.getChildCount(); i < size; i++) {
            View v = boardLayout.getChildAt(i);
            v.setOnTouchListener(this);
        }
        mGui.getMainLayout().findViewById(R.id.restart).setOnTouchListener(this);

        updateTimer = new Runnable() {
            @Override
            public void run() {
                mSpentTime = SystemClock.uptimeMillis() - mStartTime;
                setGuiTime();
                handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        boolean isGreeting = (((Activity) mContext).getFragmentManager().
                findFragmentByTag(GREETING) != null);
        if (isGreeting) {
            setGuiTime();
        } else {
            handler.postDelayed(updateTimer, 0);
        }
    }

    private void setGuiTime() {
        int[] minSec = converMillisToMinsec(mSpentTime);
        if (minSec[0] >= 100) {
            reset();
        } else {
            int[] time = {minSec[0] / 10, minSec[0] % 10, 0, minSec[1] / 10, minSec[1] % 10};
            mGui.setTime(time);
        }
    }

    /**
     * @param millis time in milliseconds
     * @return time[0] - minutes, time[1] - seconds.
     */
    private int[] converMillisToMinsec(long millis) {
        int secs = Math.round(millis / 1000);
        int mins = Math.round(secs / 60);
        secs %= 60;
        return new int[] {mins, secs};
    }

    /**
     * Take a screenshot of the main layout with the medal added.
     * @return The screenshot.
     */
    private Bitmap takeScreenshot() {
        LinearLayout v1 = mGui.getMainLayout();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmapParent = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        // add medal
        Bitmap medal = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.goodjob);
        Canvas canvas = new Canvas(bitmapParent);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        // find the height of the timer
        View v = mGui.getMainLayout().findViewById(R.id.boardLayout);
        float left = v.getX();
        float top = v.getY();
        canvas.drawBitmap(medal, left, top, paint);

        return bitmapParent;
    }

    /**
     * Greets player with dialog.
     */
    void greet() {
        GreetingDialogFragment greeting = new GreetingDialogFragment();
        FragmentManager fm = ((Activity) mContext).getFragmentManager();
        greeting.show(fm, GREETING);
    }

    /**
     * Reset current game with the board of same dimension (doesn't redraw timer and buttons).
     */
    private void reset() {
        mBoard = new GameModel(mDimension);
        mGui.placeBoard(mBoard.getBoard());
        mGui.colorBoard();
        mStartTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        mGui.setSteps(mBoard.getStepsNumber());
    }

    GameModel getModel() {
        return mBoard;
    }

    float[][] getPalette() {
        return mGui.getBoardPalette();
    }

    /**
     * @return - time spent in game in millis.
     */
    long getTime() {
        return mSpentTime;
    }

    LinearLayout getView() {
        return mGui.getMainLayout();
    }

    boolean hasNewHighscore() {
        return mIsHighscore;
    }

    void pause() {
        boolean isGreeting = (((Activity) mContext).getFragmentManager().
                findFragmentByTag(GREETING) != null);
        if (!isGreeting) {
            mSpentTime = SystemClock.uptimeMillis() - mStartTime;
            handler.removeCallbacks(updateTimer);
        }
    }

    void resume() {
        boolean isGreeting = (((Activity) mContext).getFragmentManager().
                findFragmentByTag(GREETING) != null);
        if (!isGreeting) {
            mStartTime = SystemClock.uptimeMillis() - mSpentTime;
            handler.postDelayed(updateTimer, 0);
        }
    }

    // methods of the interface GreetingDialogFragment.GreetingDialogListener
    // update an information in the dialog view
    @Override
    public void onCreate(TextView title, TextView score, ImageView reward) {
        Resources res = mContext.getResources();
        String title_text = res.getString(R.string.greet_level) + mDimension + "";
        String score_text = "";
        if (mIsHighscore) {
            reward.setImageResource(R.drawable.goodjob);
            title_text += res.getString(R.string.greet_highscore);
        } else {
            reward.setImageResource(R.drawable.goodjob2);
            // if there was another highscore
            Highscores.Score highscore = Highscores.getHighscore(mDimension);
            if (highscore != null) {
                score_text += res.getString(R.string.best_result) + highscore.TimeToText() + ", " +
                        highscore.getSteps() + "mv" +
                        res.getString(R.string.your_result);
            }
        }
        int[] time = converMillisToMinsec(mSpentTime);
        score_text += Highscores.Score.TimeToText(time) + ", " + mBoard.getStepsNumber() + "mv";

        title.setText(title_text);
        score.setText(score_text);
    }

    // new game, same level
    @Override
    public void onPositiveButtonClick() {
        reset();
    }

    // new game, next level
    @Override
    public void onNegativeButtonClick() {
        mDimension++;
        mBoard = new GameModel(mDimension);
        mGui.updateLevel(mContext, mBoard.getBoard());
        mGui.setSteps(mBoard.getStepsNumber());

        RelativeLayout boardLayout = (RelativeLayout) mGui.getMainLayout().findViewById(R.id.boardLayout);
        for (int i = 0, size = boardLayout.getChildCount(); i < size; i++) {
            View v = boardLayout.getChildAt(i);
            v.setOnTouchListener(this);
        }
        // update current game level
        mSharedPref.edit().putInt(
                MainActivity.CUR_DIM, mDimension). apply();

        mStartTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
    }

    @Override
    public void onCancel() {
//        reset();
//        ((Activity) mContext).getFragmentManager().popBackStack();

        onNegativeButtonClick();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onClick(v);
                return true;
        }
        return false;
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                v.animate().rotationBy(-360);
                reset();
                break;
            default:
                mGui.moveTile((Button) v, mBoard.moveTile(v.getId()));
                mGui.setSteps(mBoard.getStepsNumber());
                if (mBoard.isWon()) {
                    // stop timer
                    handler.removeCallbacks(updateTimer);

                    // delay greetings to show tile animation
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            greet();
                        }
                    }, 0);

                    // update scoreboard.
                    Calendar c = Calendar.getInstance();
                    mIsHighscore = Highscores.addScore(c.getTime(),
                            converMillisToMinsec(mSpentTime), mDimension, mBoard.getStepsNumber());

                    // update maximum game level if it's not higher than current
                    int maxLvl = mSharedPref.getInt(MainActivity.MAX_DIM, 2);
                    if (maxLvl == mDimension) {
                        mSharedPref.edit().putInt(MainActivity.MAX_DIM, mDimension + 1).apply();
                    }

                    // save screenshot of the highscore result
                    if (mIsHighscore) {
                        // delay screenshot to show tile animation
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap screenshot = takeScreenshot();
                                if (screenshot != null && !screenshot.isRecycled()) {
                                    StorageUtils.writeImage(mContext, screenshot, "level" + mDimension + ".png");
                                }
                            }
                        }, GameGUI.ANIM_MILLIS);
                    }
                }
        }
    }
}
