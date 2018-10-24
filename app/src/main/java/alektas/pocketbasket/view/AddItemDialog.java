package alektas.pocketbasket.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import alektas.pocketbasket.R;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddItemDialog extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddItemDialogListener {
        void onDialogAddItem(String name, int tagRes);
    }

    // Use this instance of the interface to deliver action events
    private AddItemDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the ResetDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ResetDialogListener so we can send events to the host
            mListener = (AddItemDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ResetDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_item_layout, null);
        Spinner spinner = view.findViewById(R.id.categories_spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.category_view);
        spinner.setAdapter(adapter);
        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    EditText nameField = view.findViewById(R.id.add_item_field);
                    String name = nameField.getText().toString();
                    int tagRes = (int) spinner.getSelectedItem();
                    mListener.onDialogAddItem(name, tagRes);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
        return builder.create();
    }

}