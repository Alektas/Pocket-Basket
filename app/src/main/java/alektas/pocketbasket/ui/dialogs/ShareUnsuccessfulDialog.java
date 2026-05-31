package alektas.pocketbasket.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import alektas.pocketbasket.R;

public class ShareUnsuccessfulDialog extends SafeDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.share_unsuccessful_title)
                .setMessage(R.string.msg_share_unsuccessful_basket_empty)
                .setNegativeButton(R.string.cancel, (dialog, btnId) -> { })
                .create();
    }
}
