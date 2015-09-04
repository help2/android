package fi.stipakov.heartproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by stipa on 3.9.15.
 */
public class PlacesAdapter extends ArrayAdapter<Place> {
    private LayoutInflater _inf;

    static class ViewHolder {
        private TextView name;
    }

    public PlacesAdapter(Context context, Place[] values) {
        super(context, -1, values);
        _inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = _inf.inflate(R.layout.row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Place p = getItem(position);

        holder.name.setText(p.name);

        return convertView;
    }
}
