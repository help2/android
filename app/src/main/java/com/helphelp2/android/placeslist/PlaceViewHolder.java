package com.helphelp2.android.placeslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.helphelp2.android.R;
import com.helphelp2.android.models.Address;
import com.helphelp2.android.models.Place;
import com.helphelp2.android.utils.IntentHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.distance)
    TextView _distanceTextView;

    @Bind(R.id.name)
    TextView _nameTextView;

    @Bind(R.id.list_address1)
    TextView _address1TextView;

    @Bind(R.id.list_address2)
    TextView _address2TextView;

    @Bind(R.id.list_helpers)
    TextView _helpersTextView;

    @Bind(R.id.list_items)
    TextView _itemsTextView;

    private Context _context;

    public PlaceViewHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        _context = context;
    }

    public void updateContent(@NonNull Place place) {
        String distanceText = Place.getDistanceStr(place.distance);
        if (!TextUtils.isEmpty(distanceText)) {
            distanceText += " - ";
            _distanceTextView.setText(distanceText);
        }
        _nameTextView.setText(place.name);

        Address address = place.addr;
        String addr1 = address.getAddr1();
        if (!addr1.isEmpty()) {
            _address1TextView.setVisibility(View.VISIBLE);
            _address1TextView.setText(addr1);
        } else {
            _address1TextView.setVisibility(View.GONE);
        }

        String addr2 = place.getAddr2();
        if (!addr2.isEmpty()) {
            _address2TextView.setVisibility(View.VISIBLE);
            _address2TextView.setText(Html.fromHtml(addr2));
            _address2TextView.setFocusable(false);
        } else {
            _address2TextView.setVisibility(View.GONE);
        }

        if (!place.items.isEmpty()) {
            _itemsTextView.setVisibility(View.VISIBLE);
            _itemsTextView.setText(TextUtils.join(", ", place.items));
        } else {
            _itemsTextView.setVisibility(View.GONE);
        }

        if (place.helpers) {
            _helpersTextView.setVisibility(View.VISIBLE);
            _helpersTextView.setText(R.string.need_helpers);
        } else {
            _helpersTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.list_address1)
    void onAddressClick(TextView addressTextView) {
        CharSequence address = addressTextView.getText();
        if (!TextUtils.isEmpty(address)) {
            lookUpAddress(address.toString());
        }
    }

    private void lookUpAddress(@NonNull String address) {
        _context.startActivity(IntentHelper.getLookUpAddressIntent(address));
    }

}
