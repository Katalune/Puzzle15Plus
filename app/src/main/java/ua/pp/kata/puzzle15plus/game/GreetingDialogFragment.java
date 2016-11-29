package ua.pp.kata.puzzle15plus.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ua.pp.kata.puzzle15plus.R;

/**
 * Show greet, when user won
 */
public class GreetingDialogFragment extends DialogFragment {
    private GreetingDialogListener mListener;

    private static GameController findHost(Activity activity) {
        return ((GameActivity) activity).getGameController();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // As this dialog is called from the GameController class, callback goes there
            mListener = findHost(activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("GameController in " + activity.toString() +
                    " must implement GreetingDialogListener");
        }

        if (mListener == null) {
            Log.i("wtf", "controller is null in greeting on attach!");
        } else {
            Log.i("wtf", "controller NOT null in greeting on attach!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // View with greeting image and TextView with score information
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_greeting, null);

        // mListener could be null if on config changes dialog had been already attached
        // but host class wasn't recreated yet.
        if (mListener == null) {
            mListener = findHost(getActivity());
        }
        if (mListener != null){
            // Set score information from the Dialog's Host
            mListener.onCreate((TextView) v.findViewById(R.id.title_txt),
                    (TextView) v.findViewById(R.id.score_txt),
                    (ImageView) v.findViewById(R.id.reward_img));
            builder.setView(v)
                    .setPositiveButton(getActivity().getResources().getString(R.string.greet_button_positive),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // handles click from the Dialog's Host
                            mListener.onPositiveButtonClick();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.greet_button_negative),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.onNegativeButtonClick();
                        }
                    });
        }

        if (mListener == null) {
            Log.i("wtf", "controller is null in greeting on create!");
        } else {
            Log.i("wtf", "controller NOT null in greeting on create!");
        }

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    public interface GreetingDialogListener {
        void onCreate(TextView title, TextView score, ImageView reward);

        void onPositiveButtonClick();

        void onNegativeButtonClick();

        void onCancel();
    }
}
