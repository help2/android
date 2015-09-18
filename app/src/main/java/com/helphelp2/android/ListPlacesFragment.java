package com.helphelp2.android;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;
import java.util.Locale;

public class ListPlacesFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private LayoutInflater _inf;

    PlacesAdapter _adapter;
    Place[] _places;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _inf = inflater;
        View v = inflater.inflate(R.layout.list_fragment, container, false);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _adapter = new PlacesAdapter(getActivity());
        setListAdapter(_adapter);

        if (_places != null) {
            _adapter.clear();
            _adapter.addAll(_places);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Place p = (Place) getListAdapter().getItem(position);
        Address address = p.addr;
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", address.getAddr1());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void setPlaces(List<Place> places) {
        _places = places.toArray(new Place[places.size()]);
        if (_adapter != null && places != null) {
            _adapter.clear();
            _adapter.addAll(_places);
        }
    }
}
