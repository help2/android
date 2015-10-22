package com.helphelp2.android.placeslist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.helphelp2.android.R;
import com.helphelp2.android.models.Place;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlacesListFragment extends Fragment {

    @Bind(R.id.places_list)
    RecyclerView _placesView;

    @Bind(R.id.places_list_empty)
    TextView _emptyView;

    private List<Place> _places;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_list_fragment, container, false);
        ButterKnife.bind(this, rootView);
        _placesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _placesView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setPlaces(@Nullable List<Place> places) {
        _places = places;
        if (_placesView != null && _emptyView != null) {
            updateView();
        }
    }

    protected void updateView() {
        if (_places == null || _places.isEmpty()) {
            _emptyView.setVisibility(View.VISIBLE);
            _placesView.setVisibility(View.GONE);
        } else {
            _placesView.setVisibility(View.VISIBLE);
            _emptyView.setVisibility(View.GONE);
            renderList();
        }
    }

    private void renderList() {
        PlacesListAdapter placesListAdapter = new PlacesListAdapter(_places);
        _placesView.setAdapter(placesListAdapter);
    }

}
