package com.helphelp2.android;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.helphelp2.android.models.Place;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stipa on 3.9.15.
 */
public class PlacesStorageFragment extends Fragment {
    // data object we want to retain
    private boolean _placesFetched;
    private List<Place> _places;
    private CameraPos _cameraPos;
    private Location _loc;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

        _places = new LinkedList<>();
    }

    public void setData(List<Place> places) {
        _places = places;
    }

    public List<Place> getPlaces() {
        return _places;
    }

    public boolean isPlacesFetched() {
        return _placesFetched;
    }

    public void setPlacesFetched() {
        _placesFetched = true;
    }

    public void setCameraPosition(CameraPos cameraPos) {
        _cameraPos = cameraPos;
    }

    public CameraPos getCameraPosition() {
        return _cameraPos;
    }

    public Location getLocation() {
        return _loc;
    }

    public void setLocation(Location loc) {
        _loc = loc;
    }
}