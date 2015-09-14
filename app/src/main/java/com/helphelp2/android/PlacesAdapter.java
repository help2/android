package com.helphelp2.android;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by stipa on 3.9.15.
 */
public class PlacesAdapter extends ArrayAdapter<Place> {
    private LayoutInflater _inf;

    static class ViewHolder {
        private TextView name;
        private TextView distance;
        private TextView addr1;
        private TextView addr2;
        private TextView items;
        private TextView helpers;
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
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.addr1 = (TextView) convertView.findViewById(R.id.list_address1);
            holder.addr2 = (TextView) convertView.findViewById(R.id.list_address2);
            holder.items = (TextView) convertView.findViewById(R.id.list_items);
            holder.helpers = (TextView) convertView.findViewById(R.id.list_helpers);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place p = getItem(position);

        holder.name.setText(p.name);
        holder.distance.setText(Place.getDistanceStr(p.dist) + " - ");

        if (!p.addr1.isEmpty()) {
            holder.addr1.setVisibility(View.VISIBLE);
            holder.addr1.setText(p.addr1);
        } else {
            holder.addr1.setVisibility(View.GONE);
        }

        if (!p.addr2.isEmpty()) {
            holder.addr2.setVisibility(View.VISIBLE);
            holder.addr2.setText(Html.fromHtml(p.addr2));
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

        if (!p.helpers.isEmpty()) {
            holder.helpers.setVisibility(View.VISIBLE);
            holder.helpers.setText(p.helpers);
        } else {
            holder.helpers.setVisibility(View.GONE);
        }

        return convertView;
    }
}
