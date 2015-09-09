package ua.pp.kata.puzzle15plus.game;

import java.io.Serializable;

import ua.pp.kata.puzzle15plus.RetainedFragment;

/**
 * Store game data for GameFragment on configuration changes.
 */
public class GameData implements Serializable, RetainedFragment.Loadable {
    private static GameModel gameModel;
    private static float[][] gamePalette;
    private static long gameTime;
    public static boolean exists = false;

    private GameModel mGameModel;
    private float[][] mGamePalette;
    private long mGameTime;
    public boolean mExists;

    public GameData() {
        mGameModel = gameModel;
        mGamePalette = gamePalette;
        mGameTime = gameTime;
        mExists = exists;
    }

    public void load() {
        gameModel = mGameModel;
        gamePalette = mGamePalette;
        gameTime = mGameTime;
        exists = mExists;
    }

    /**
     * Store game state
     * @param model - game model (board, number of steps)
     * @param palette - board palette from GameGUI class
     * @param time - time already spent solving the puzzle
     */
    static void setData(GameModel model, long time , float[][] palette) {
        gameModel = model;
        gamePalette = palette;
        gameTime = time;
        exists = true;
    }

    static GameModel getGameModel() {
        return gameModel;
    }

    static float[][] getGamePalette() {
        return gamePalette;
    }

    static long getGameTime() {
        return gameTime;
    }
}
