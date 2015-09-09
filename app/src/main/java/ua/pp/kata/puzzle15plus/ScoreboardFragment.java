package ua.pp.kata.puzzle15plus;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import ua.pp.kata.puzzle15plus.game.GameColor;


public class ScoreboardFragment extends Fragment {
    private float[][] mLevelsPalette;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout main = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_scoreboard, null);

        ArrayList<Highscores.Score> scoreboard = Highscores.getTable();
        if (scoreboard != null) {
            mLevelsPalette = Arrays.copyOfRange(GameColor.createPalette(scoreboard.size() + 1), 1, scoreboard.size() + 1) ;
            ScoreAdapter adapter = new ScoreAdapter(getActivity(), R.layout.item_scoreboard, scoreboard);

            ListView list = (ListView) main.findViewById(R.id.score_listview);
            list.setAdapter(adapter);
            list.setOnItemClickListener(adapter);
        }
        return main;
    }

    private class ScoreAdapter extends ArrayAdapter<Highscores.Score>
            implements AdapterView.OnItemClickListener, View.OnClickListener {
        ArrayList<Highscores.Score> scores;
        Context mContext;

        private ScoreAdapter(Context context, int ViewId, ArrayList<Highscores.Score> scores) {
            super(context, ViewId, scores);
            this.mContext = context;
            this.scores = scores;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // create view if not created yet
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.item_scoreboard, null);
            }
            // set view content based on model data
            Highscores.Score record = scores.get(position);
            DateFormat df = DateFormat.getDateInstance();
            ((TextView) convertView.findViewById(R.id.date)).setText(df.format(record.getDate()));
            ((TextView) convertView.findViewById(R.id.time)).setText(record.TimeToText());
            TextView level = (TextView) convertView.findViewById(R.id.level);
            level.setText(String.valueOf(record.getLevel()));
            level.setBackgroundColor(Color.HSVToColor(mLevelsPalette[position]));
            ((TextView) convertView.findViewById(R.id.steps)).setText(
                    String.valueOf(record.getSteps()) + " mv");
            convertView.setTag(record.getLevel());
            convertView.findViewById(R.id.share).setOnClickListener(this);
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int scoreLevel = (int) view.getTag();

            File imageFile = new File(mContext.getFilesDir(), "level" + scoreLevel + ".png");
            if (imageFile.exists()) {
                // FileProvider facilitates secure sharing of files
                Uri contentUri = FileProvider.getUriForFile(mContext, "ua.pp.kata.puzzle15plus.fileprovider", imageFile);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setType("image/png")
                            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            // intent.setData(contentUri) provide image to ACTION_VIEW
                            // intent.putExtra(Intent.EXTRA_STREAM, contentUri)provide image to ACTION_SEND
                            .setData(contentUri);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.view_gameboard)));
            }
        }

        @Override
        public void onClick(View v) {
            int scoreLevel = (int) ((View) v.getParent()).getTag();
            File imageFile = new File(mContext.getFilesDir(), "level" + scoreLevel + ".png");
            // FileProvider facilitates secure sharing of files
            Uri imageUri = FileProvider.getUriForFile(mContext, "ua.pp.kata.puzzle15plus.fileprovider", imageFile);

            // try to send via Facebook
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent
                    .setType("image/png")
                    .setPackage("com.facebook.katana");
            ArrayList<Uri> files = new ArrayList<>();
            files.add(imageUri);

            // add qr code
            File qrFile = new File(mContext.getFilesDir(), "qr.png");
            if (!qrFile.exists()) {
                // write from raw for the first time
                qrFile = writeQr();
            }
            Uri qrUri = FileProvider.getUriForFile(mContext, "ua.pp.kata.puzzle15plus.fileprovider", qrFile);
            files.add(qrUri);

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e1) {
                // try to send via Google+
                int min = Highscores.getHighscore(scoreLevel).getTime()[0] + 1;
                String text = getResources().getQuantityString(R.plurals.timeToSolvePuzzle, min, min);
                text += "\n\ngoo.gl/5DV998";
                Intent intent2 = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(text)
                        .setType("image/png")
                        .setStream(imageUri)
                        .getIntent().setPackage("com.google.android.apps.plus");
                try {
                    startActivity(intent2);
                } catch (ActivityNotFoundException e2) {
                    int duration = Toast.LENGTH_SHORT;
                    String notFound = getActivity().getResources().getString(R.string.g_not_found);
                    Toast toast = Toast.makeText(mContext, notFound, duration);
                    toast.show();
                }
            }
        }
    }

    private File writeQr() {
        File file = new File(getActivity().getFilesDir() + File.separator + "qr.png");
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = getActivity().getResources().openRawResource(R.raw.qr);
            fileOutputStream = new FileOutputStream(file);

            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0) {
                fileOutputStream.write(buf,0,len);
            }
        } catch (IOException e1) {}
        finally {
            try { fileOutputStream.close();
            } catch (IOException e) {}
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
        return file;
    }
}

