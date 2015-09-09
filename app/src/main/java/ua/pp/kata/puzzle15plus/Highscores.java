package ua.pp.kata.puzzle15plus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Stores score result
 */
public class Highscores implements Serializable, RetainedFragment.Loadable{
    private static ArrayList<Score> table;
    private ArrayList<Score> mTable;

    public Highscores() {
        mTable = table;
    }

    public void load() {
       table = mTable;
    }

    /**
     * Add new game record to the Highscores
     * @param date date of the game.
     * @param time time spent in the game play, [0] - minutes, [1] - seconds.
     * @param level the level of the game play.
     * @param steps the number of steps in the game.
     * @return if the score higher, than presented in table, and was added.
     */
    public static boolean addScore(Date date, int[] time, int level, int steps) {
        Score score = new Score(date, time, level, steps);

        if (table == null) {
            table = new ArrayList<>();
            table.add(score);
            return true;
        }

        boolean exist = false;

        for (int i = 0, size = table.size(); i < size; i++) {
            if (score.getLevel() == table.get(i).getLevel()) {
                exist = true;
                if (score.compareTo(table.get(i)) > 0) {
                    table.add(score);
                    table.remove(i); // break iterating after element removing!!!
                    Collections.sort(table);
                    return true;
                }
            }
        }
        // there's no scores of this level
        if (!exist) {
            table.add(score);
            Collections.sort(table);
            return true;
        }
        return false;
    }

    /**
     * Return highscore of given level.
     * @param level game level.
     * @return highscore of given level if exist. Null otherwise.
     */
    public static Score getHighscore(int level) {
        if (table != null) {
            for (Score record : table) {
                if (record.getLevel() == level) {
                    return record;
                }
            }
        }
        return null;
    }

    static ArrayList<Score> getTable() {
        return table;
    }

    public static class Score implements Comparable<Score>, Serializable {
        private Date mDate;
        private int[] mTime; // time[0] - minutes, time[1] - seconds spent in game
        private int mLevel;
        private int mSteps;

        /**
         * Construct new game record
         * @param date the date of the game.
         * @param time time spent in the game play, [0] - minutes, [1] - seconds.
         * @param level the level of the game play.
         * @param steps the number of steps in the game.
         */
        Score(Date date, int[] time, int level, int steps) {
            this.mDate = date;
            this.mTime = time;
            this.mLevel = level;
            this.mSteps = steps;
        }

        /**
         * @return date of the game.
         */
        Date getDate() {
            return mDate;
        }

        /**
         * @return time spent in the game, [0] - minutes, [1] - seconds.
         */
        int[] getTime() {
            return mTime;
        }

        public String TimeToText() {
            return TimeToText(mTime);
        }

        /**
         * Convert time to  string.
         * @param time_array [0] - minutes, [1] - seconds.
         * @return string "*minutes*m *seconds*s".
         */
        public static String TimeToText(int[] time_array) {
            String time = "";
            if (time_array[0] != 0) {
                time += String.valueOf(time_array[0]) + "m ";
            }
            if (time_array[1] != 0) {
                time += String.valueOf(time_array[1]) + "s";
            }
            return time;
        }

        /**
         * @return level of the game.
         */
        int getLevel() {
            return mLevel;
        }

        /**
         * @return number of steps done in the game.
         */
        public int getSteps() {
            return mSteps;
        }

        @Override
        public int compareTo(Score another) {
            int anotherTime = another.mTime[0] * 60 + another.mTime[1];
            int thisTime = mTime[0] * 60 + mTime[1];
            if (another.mLevel == mLevel) {
                if (anotherTime == thisTime) {
                    return another.mSteps - this.mSteps;
                } else {
                    return anotherTime - thisTime;
                }
            } else {
                return mLevel - another.mLevel;
            }
        }
    }
}
