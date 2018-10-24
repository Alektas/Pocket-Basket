package alektas.pocketbasket.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import alektas.pocketbasket.R;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ResetDialog extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ResetDialogListener {
        void onDialogAcceptReset();
    }

    // Use this instance of the interface to deliver action events
    private ResetDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the ResetDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ResetDialogListener so we can send events to the host
            mListener = (ResetDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ResetDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.reset_msg)
                .setPositiveButton(R.string.accept, (dialog, id) -> mListener.onDialogAcceptReset())
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
        });
        return builder.create();
    }
}