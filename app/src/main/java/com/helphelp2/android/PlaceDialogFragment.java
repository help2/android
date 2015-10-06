package com.helphelp2.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by stipa on 2.9.15.
 */
public class PlaceDialogFragment extends DialogFragment {

    protected static final String BUNDLE_KEY_ADDR = "addr";
    protected static final String BUNDLE_KEY_ADDR2 = "addr2";
    protected static final String BUNDLE_KEY_HELPERS = "helpers";
    protected static final String BUNDLE_KEY_ITEMS = "items";
    protected static final String BUNDLE_KEY_TITLE = "title";

    protected static final String GEO_INTENT_PATTERN = "geo:0,0?q=%s";

    @Bind(R.id.address)
    TextView _addressTextView;

    @Bind(R.id.address2)
    TextView _address2TextView;

    @Bind(R.id.helpers)
    TextView _helpersTextView;

    @Bind(R.id.items)
    TextView _itemsTextView;

    protected String addr;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String title = arguments.getString(BUNDLE_KEY_TITLE);
        addr = arguments.getString(BUNDLE_KEY_ADDR);
        String addr2 = arguments.getString(BUNDLE_KEY_ADDR2);
        String items = arguments.getString(BUNDLE_KEY_ITEMS);
        boolean helpers = arguments.getBoolean(BUNDLE_KEY_HELPERS);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = inflater.inflate(R.layout.dialog_place, null);
        ButterKnife.bind(this, layout);

        _addressTextView.setText(addr);
        _itemsTextView.setText(items);

        if (!TextUtils.isEmpty(addr2)) {
            _address2TextView.setText(Html.fromHtml(addr2));
        } else {
            _address2TextView.setVisibility(View.GONE);
        }

        if (helpers) {
            _helpersTextView.setText(R.string.need_helpers);
        } else {
            _helpersTextView.setVisibility(View.GONE);
        }

        builder.setView(layout);
        builder.setTitle(title);

        return builder.create();
    }

    @OnClick(R.id.dialog_maps_icon)
    public void onMapsIconClick() {
        if (TextUtils.isEmpty(addr)) {
            return;
        }
        String uri = String.format(Locale.ENGLISH, GEO_INTENT_PATTERN, addr);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
