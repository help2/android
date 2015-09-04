package fi.stipakov.heartproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by stipa on 2.9.15.
 */
public class PlaceDialogFragment extends DialogFragment {
    public static PlaceDialogFragment newInstance(String title, String addr, String addr2,
                                                  String items) {
        PlaceDialogFragment frag = new PlaceDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("addr", addr);
        args.putString("addr2", addr2);
        args.putString("items", items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        String addr = getArguments().getString("addr");
        String addr2 = getArguments().getString("addr2");
        String items = getArguments().getString("items");

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

        tv = (TextView)layout.findViewById(R.id.items);
        tv.setText(items);

        builder.setView(layout);
        builder.setTitle(title);

        return builder.create();
    }
}