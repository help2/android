package com.helphelp2.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * Created by stipa on 14.9.15.
 */
public class InfoBoxDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final SpannableString s = new SpannableString(getString(R.string.infobox_text));
        Linkify.addLinks(s, Linkify.ALL);
        builder.setTitle(R.string.app_name);
        builder.setMessage(s);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView tv = (TextView) getDialog().findViewById(android.R.id.message);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
