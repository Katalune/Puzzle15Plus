package ua.pp.kata.puzzle15plus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ua.pp.kata.puzzle15plus.game.GameData;

/**
 * Retained fragment to upload data only once during its creation.
 * Also includes methods to save information to internal storage.
 */
public class StorageUtils {

    public static final String SCOREBOARD_FILENAME = "scoreboard";
    public static final String  GAMEDATA_FILENAME = "gamedata";
    private static final String PREFS_KEY = "ua.pp.kata.puzzle15plus";

    public static void loadData(Context context) {
        readObject(context, null, StorageUtils.SCOREBOARD_FILENAME);
        readObject(context, null, StorageUtils.GAMEDATA_FILENAME);
    }

    public static void saveData(Context context, Highscores highscores, GameData gameData) {

    }

    private static void readObject(Context context, Loadable object, String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(filename);
            ois = new ObjectInputStream(fis);
            object = (Loadable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeInputStream(fis);
            closeInputStream(ois);
        }
        if (object != null) {
            object.load();
        }
    }

    /**
     * Write object to a file.
     * @param activity Activity to open FileOutputStream.
     * @param object Serializable object to save.
     * @param filename Name of the file in the internal storage.
     */
    public static void writeObject(Activity activity, Object object, String filename) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = activity.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeOutputStream(fos);
            closeOutputStream(oos);
        }
    }

    /**
     * Write image to a file.
     * @param context context to open FileOutputStream.
     * @param bitmap bitmap to save.
     * @param filename name of the file in the internal storage.
     */
    public static void writeImage(Context context, Bitmap bitmap, String filename){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(fos);
        }
    }

    private static void closeOutputStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(StorageUtils.PREFS_KEY, Context.MODE_PRIVATE);
    }

    public interface Loadable {
        void load();
    }

}
