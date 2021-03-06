package alektas.pocketbasket.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import alektas.pocketbasket.R;

public class GuideAcceptDialog extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface GuideAcceptDialogListener {
        void onDialogAcceptHints();
        void onDialogRejectHints();
    }

    // Use this instance of the interface to deliver action events
    private GuideAcceptDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the ResetDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ResetDialogListener so we can send events to the host
            mListener = (GuideAcceptDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ResetDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.guide_accept_title)
                .setMessage(R.string.guide_accept_msg)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    mListener.onDialogAcceptHints();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    mListener.onDialogRejectHints();
                });
        return builder.create();
    }
}