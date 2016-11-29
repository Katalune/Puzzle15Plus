package ua.pp.kata.puzzle15plus.game;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import ua.pp.kata.puzzle15plus.Highscores;
import ua.pp.kata.puzzle15plus.MainActivity;
import ua.pp.kata.puzzle15plus.R;
import ua.pp.kata.puzzle15plus.StorageUtils;

import static ua.pp.kata.puzzle15plus.game.GameFragment.DEF_DIM;

public class GameActivity extends Activity {

    private GameController gameController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current board size

        StorageUtils.loadData(this);

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
        int boardDimension = StorageUtils.getPrefs(this).getInt(MainActivity.CUR_DIM, DEF_DIM);
        if (GameData.exists && boardDimension == last_dim && !GameData.getGameModel().isWon() ||
                (getFragmentManager().findFragmentByTag(GameController.GREETING) != null)) {
            gameController = new GameController(this, GameData.getGameModel(), GameData.getGameTime(),
                    GameData.getGamePalette());
        }


        // if there wasn't saved data in retained DataFragment
        if (gameController == null) {
            gameController = new GameController(this, boardDimension);
        }
        View view = gameController.getView();
        view.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameController != null) {
            gameController.resume();
        }
    }

    @Override
    protected void onPause() {
        if (gameController != null) {
            gameController.pause();

            // save game data
            GameData.setData(gameController.getModel(), gameController.getTime(),
                    gameController.getPalette());
            StorageUtils.writeObject(this, new GameData(), StorageUtils.GAMEDATA_FILENAME);

            // save scoreboard if changes were made
            if (gameController.hasNewHighscore()) {
                StorageUtils.writeObject(this, new Highscores(), StorageUtils.SCOREBOARD_FILENAME);
            }
        }
        super.onPause();
    }

    public GameController getGameController() {
        return gameController;
    }
}
