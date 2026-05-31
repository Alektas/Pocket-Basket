package alektas.pocketbasket.ui.dialogs;

import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

public abstract class SafeDialogFragment extends DialogFragment {
    private static final String TAG = "SafeDialogFragment";

    @Override
    public void onStart() {
        try {
            super.onStart();
        } catch (WindowManager.BadTokenException | IllegalStateException e) {
            Log.w(TAG, "Dialog window is not available", e);
            dismissAllowingStateLoss();
        }
    }
}
