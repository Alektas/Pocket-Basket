package alektas.pocketbasket.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import alektas.pocketbasket.R;
import alektas.pocketbasket.utils.ResourcesUtils;

public class AboutDialog extends DialogFragment {
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.layout_about, null);

        initAboutMenuLinks();

        TextView versionText = mView.findViewById(R.id.version_text);
        String versionName = ResourcesUtils.getVersionName();
        versionText.setText(getResources().getString(R.string.about_version, versionName));

        builder.setTitle(R.string.about_title)
                .setView(mView)
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });
        return builder.create();
    }

    private void initAboutMenuLinks() {
        setupLink(R.id.jeff_link, R.string.jeff_link);
        setupLink(R.id.bom_link, R.string.bom_link);
        setupLink(R.id.google_link, R.string.google_material_link);
        setupLink(R.id.apache_link, R.string.apache_license_link);
        setupLink(R.id.apache2_link, R.string.apache_license_link);
        setupLink(R.id.cc_link, R.string.cc_license_link);
        setupLink(R.id.privacy_policy_link, R.string.privacy_policy_link);
    }

    private void setupLink(@IdRes int viewId, @StringRes int linkId) {
        TextView tv = mView.findViewById(viewId);
        String link = getString(linkId);
        makeLinkableDecoration(tv, link);
        tv.setOnClickListener(v -> browseLink(link));
    }

    private void makeLinkableDecoration(TextView tv, String link) {
        SpannableString ss = new SpannableString(tv.getText());
        ss.setSpan(new URLSpan(link), 0, tv.getText().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
    }

    private void browseLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

}