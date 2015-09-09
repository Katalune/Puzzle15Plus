package ua.pp.kata.puzzle15plus.game;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.kata.puzzle15plus.Highscores;
import ua.pp.kata.puzzle15plus.MainActivity;
import ua.pp.kata.puzzle15plus.R;
import ua.pp.kata.puzzle15plus.RetainedFragment;

/**
 * Represent starting screen or game screen.
 */
public class GameFragment extends Fragment {
    public static final int START_STATE = 1;
    public static final int DEF_DIM = 2;
    public static final int GAME_STATE = DEF_DIM;
    public static final String STATE_INDEX = "index";
    private GameController mGameController;
    private int mBoardDimension;
    private GameFragmentListener mListener;

    public interface GameFragmentListener {
        void onStartGameButtonClick();
        void onPauseGameButtonClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GameFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement GameFragmentListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int index = getArguments().getInt(STATE_INDEX);

        // if we are going to start game
        if (index == GAME_STATE) {
            // get current board size
            SharedPreferences mSharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            mBoardDimension = mSharedPref.getInt(MainActivity.CUR_DIM, DEF_DIM);
            // get board size from last game
            int last_dim;
            try {
                last_dim = GameData.getGameModel().getBoard().length;
            } catch (NullPointerException e) {
                last_dim = DEF_DIM;
            }
            // if there was a game and saved data
            // and game dimension doesn't change since last time
            // and board isn't already won
            // OR there's greeting dialog
            // restore the controller
            if (GameData.exists && mBoardDimension == last_dim && !GameData.getGameModel().isWon() ||
                    (getActivity().getFragmentManager().findFragmentByTag(GameController.GREETING) != null)) {
                mGameController = new GameController(getActivity(), GameData.getGameModel(), GameData.getGameTime(),
                        GameData.getGamePalette());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int index = getArguments().getInt(STATE_INDEX);
        View view = null;

        switch (index) {
            case START_STATE:
                view = inflater.inflate(R.layout.fragment_startgame, container, false);
                View button = view.findViewById(R.id.StartButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onStartGameButtonClick();
                    }
                });

                break;
            case GAME_STATE:
                // if there wasn't saved data in retained DataFragment
                if (mGameController == null) {
                    mGameController = new GameController(getActivity(), mBoardDimension);
                }
                view = mGameController.getView();
                view.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPauseGameButtonClick();
                    }
                });
                break;
            default:
        }
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().getInt(STATE_INDEX) == GAME_STATE) {
            // hide actionbar
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }

        if (mGameController != null) {
            mGameController.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGameController != null) {
            mGameController.pause();

            // save game data
            GameData.setData(mGameController.getModel(), mGameController.getTime(),
                    mGameController.getPalette());
            RetainedFragment.writeObject(getActivity(), new GameData(), RetainedFragment.GAMEDATA_FILENAME);

            // save scoreboard if changes were made
            if (mGameController.hasNewHighscore()) {
                RetainedFragment.writeObject(getActivity(), new Highscores(), RetainedFragment.SCOREBOARD_FILENAME);
            }
        }

        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    public GameController getGameController() {
        return  mGameController;
    }
}
