package ua.pp.kata.puzzle15plus;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;

import ua.pp.kata.puzzle15plus.game.GameColor;
import ua.pp.kata.puzzle15plus.game.GameFragment;


public class LevelFragment extends Fragment {
    private LevelFragmentListener mListener;
    private int mCurLevel;
    private SharedPreferences mSharedPref;
    private float[][] mLevelsPalette;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LevelFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LevelFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout main = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_levels, null);
        mSharedPref = StorageUtils.getPrefs(getActivity());
        int maxLevel = mSharedPref.getInt(MainActivity.MAX_DIM, GameFragment.DEF_DIM);
        mCurLevel = mSharedPref.getInt(MainActivity.CUR_DIM, GameFragment.DEF_DIM);

        ArrayList<Integer> levels = makeSequence(2, maxLevel);
        mLevelsPalette = Arrays.copyOfRange(GameColor.createPalette(levels.size() + 1), 1, levels.size() + 1) ;
        LevelAdapter adapter = new LevelAdapter(getActivity(), R.layout.item_level, levels);

        GridView grid = (GridView) main.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(adapter);

        return main;
    }

    private ArrayList<Integer> makeSequence(int begin, int end) {
        ArrayList<Integer> list = new ArrayList<>(end - begin + 1);

        for(int i = begin; i <= end; list.add(i++));

        return list;
    }

    public interface LevelFragmentListener {
        void onLevelButtonClick();
    }

    private class LevelAdapter extends ArrayAdapter<Integer> implements AdapterView.OnItemClickListener {
        ArrayList<Integer> levels;
        Context context;

        private LevelAdapter(Context context, int ViewId, ArrayList<Integer> levels) {
            super(context, ViewId, levels);
            this.context = context;
            this.levels = levels;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // create view if not created yet
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_level, null);
            }
            // set view content based on model data
            int level = levels.get(position);
            Button b = (Button) convertView.findViewById(R.id.button);
            b.setText(String.valueOf(level));
            b.setBackgroundColor(Color.HSVToColor(mLevelsPalette[position]));

            // select current level
            if (level == mCurLevel) {
                convertView.setBackgroundResource(R.drawable.frame);
            }
            convertView.setTag(level); // tag equals level associated with current view
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // tag associated with the view is it's level
            int checkedLevel = (int) view.getTag();
            if (checkedLevel != mCurLevel) {
                LinearLayout previous = (LinearLayout) parent.findViewWithTag(mCurLevel);
                previous.setBackgroundResource(0);
                view.setBackgroundResource(R.drawable.frame);
                mCurLevel = checkedLevel;
                mSharedPref.edit().putInt(MainActivity.CUR_DIM, mCurLevel).commit();

                mListener.onLevelButtonClick();
            }
        }
    }

}
