package com.helphelp2.android;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.helphelp2.android.models.Address;
import com.helphelp2.android.models.Place;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by stipa on 3.9.15.
 */
public class PlacesAdapter extends ArrayAdapter<Place> {
    private LayoutInflater _inf;

    static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.distance)
        TextView distance;
        @Bind(R.id.list_address1)
        TextView addr1;
        @Bind(R.id.list_address2)
        TextView addr2;
        @Bind(R.id.list_items)
        TextView items;
        @Bind(R.id.list_helpers)
        TextView helpers;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public PlacesAdapter(Context context) {
        super(context, -1);
        _inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = _inf.inflate(R.layout.row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place p = getItem(position);

        holder.name.setText(p.name);
        holder.distance.setText(Place.getDistanceStr(p.distance) + " - ");

        Address address = p.addr;
        String addr1 = address.getAddr1();
        if (!addr1.isEmpty()) {
            holder.addr1.setVisibility(View.VISIBLE);
            holder.addr1.setText(addr1);
        } else {
            holder.addr1.setVisibility(View.GONE);
        }

        String addr2 = p.getAddr2();
        if (!addr2.isEmpty()) {
            holder.addr2.setVisibility(View.VISIBLE);
            holder.addr2.setText(Html.fromHtml(addr2));
            holder.addr2.setFocusable(false);
        } else {
            holder.addr2.setVisibility(View.GONE);
        }

        if (!p.items.isEmpty()) {
            holder.items.setVisibility(View.VISIBLE);
            holder.items.setText(TextUtils.join(", ", p.items));
        } else {
            holder.items.setVisibility(View.GONE);
        }

        if (p.helpers) {
            holder.helpers.setVisibility(View.VISIBLE);
            holder.helpers.setText(R.string.need_helpers);
        } else {
            holder.helpers.setVisibility(View.GONE);
        }

        return convertView;
    }
}
