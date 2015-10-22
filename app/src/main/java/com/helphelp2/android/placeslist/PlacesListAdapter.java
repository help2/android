package com.helphelp2.android.placeslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helphelp2.android.R;
import com.helphelp2.android.models.Place;

import java.util.List;

public class PlacesListAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private final List<Place> _places;

    public PlacesListAdapter(@NonNull List<Place> places) {
        _places = places;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.places_list_row, parent, false);
        return new PlaceViewHolder(context, itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        if (!_places.isEmpty()) {
            Place place = _places.get(position);
            holder.updateContent(place);
        }
    }

    @Override
    public int getItemCount() {
        return _places.size();
    }

}
