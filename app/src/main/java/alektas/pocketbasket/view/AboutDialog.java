package alektas.pocketbasket.view;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import alektas.pocketbasket.R;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AboutDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about_layout, null);

        TextView versionText = view.findViewById(R.id.version_text);
        try {
            String versionName = getActivity()
                    .getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0)
                    .versionName;
            versionText.setText(getResources().getString(R.string.version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setTitle(R.string.about_title)
                .setView(view)
                .setNeutralButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
        return builder.create();
    }
}