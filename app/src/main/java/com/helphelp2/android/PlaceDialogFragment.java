package com.helphelp2.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by stipa on 2.9.15.
 */
public class PlaceDialogFragment extends DialogFragment {

    protected static final String BUNDLE_KEY_ADDR = "addr";
    protected static final String BUNDLE_KEY_ADDR2 = "addr2";
    protected static final String BUNDLE_KEY_HELPERS = "helpers";
    protected static final String BUNDLE_KEY_ITEMS = "items";
    protected static final String BUNDLE_KEY_TITLE = "title";

    public static PlaceDialogFragment newInstance(String title, String addr, String addr2,
                                                  String items, boolean helpers) {
        PlaceDialogFragment frag = new PlaceDialogFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_TITLE, title);
        args.putString(BUNDLE_KEY_ADDR, addr);
        args.putString(BUNDLE_KEY_ADDR2, addr2);
        args.putBoolean(BUNDLE_KEY_HELPERS, helpers);
        args.putString(BUNDLE_KEY_ITEMS, items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(BUNDLE_KEY_TITLE);

        final String addr = getArguments().getString(BUNDLE_KEY_ADDR);
        String addr2 = getArguments().getString(BUNDLE_KEY_ADDR2);
        String items = getArguments().getString(BUNDLE_KEY_ITEMS);
        boolean helpers = getArguments().getBoolean(BUNDLE_KEY_HELPERS);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View layout = inflater.inflate(R.layout.dialog_place, null);

        TextView tv = (TextView)layout.findViewById(R.id.address);
        tv.setText(addr);

        tv = (TextView)layout.findViewById(R.id.address2);
        if (!addr2.isEmpty()) {
            tv.setText(Html.fromHtml(addr2));
        } else {
            tv.setVisibility(View.GONE);
        }

        tv = (TextView)layout.findViewById(R.id.helpers);
        if (helpers) {
            tv.setText(R.string.need_helpers);
        } else {
            tv.setVisibility(View.GONE);
        }

        tv = (TextView)layout.findViewById(R.id.items);
        tv.setText(items);

        builder.setView(layout);
        builder.setTitle(title);

        View maps = layout.findViewById(R.id.dialog_maps_icon);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", addr);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        return builder.create();
    }
}