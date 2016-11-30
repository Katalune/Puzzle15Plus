package ua.pp.kata.puzzle15plus;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Represent starting screen or game screen.
 */
public class GameFragment extends Fragment {
    public static final int START_STATE = 1;
    public static final int DEF_DIM = 2;
    public static final String STATE_INDEX = "index";
    private GameFragmentListener mListener;

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
            default:
        }
        return view;
    }

    public interface GameFragmentListener {
        void onStartGameButtonClick();
    }
}
