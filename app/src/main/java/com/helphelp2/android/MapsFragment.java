package com.helphelp2.android;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by stipa on 2.9.15.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap _map;
    private HashMap<Marker, Place> _markerToPlace;
    private List<Place> _places;
    private boolean _moveCamera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _markerToPlace = new HashMap<>();

        View v = inflater.inflate(R.layout.maps_fragment, container, false);

        SupportMapFragment frag = (SupportMapFragment) (getChildFragmentManager().findFragmentById(R.id.map));
        if (frag == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            frag = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, frag).commit();
        }

        frag.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;

        googleMap.setMyLocationEnabled(true);
        _map.setOnMarkerClickListener(this);

        if (_places != null) {
            placePins(_places, _moveCamera);
        }
    }

    public void onPause() {
        super.onPause();

        if (_map != null) {
            CameraPos pos = new CameraPos(_map.getCameraPosition().target.latitude,
                            _map.getCameraPosition().target.longitude,
                            _map.getCameraPosition().zoom);

            ((MainActivity)getActivity()).saveCameraPosition(pos);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Place p = _markerToPlace.get(marker);

        String name = p.name;
        if (!p.distStr.isEmpty()) {
            name += " (" + Place.getDistanceStr(p.dist) + ")";
        }

        DialogFragment dialog = PlaceDialogFragment.newInstance(name, p.addr1, p.addr2,
                TextUtils.join(", ", p.items));
        dialog.show(getFragmentManager(), "place");

        return true;
    }

    public void placePins(List<Place> places, boolean moveCamera) {
        _places = places;
        _moveCamera = moveCamera;
        if (_map == null) {
            return;
        }

        double dist = 10000000;
        Marker closestMarket = null;

        for (Place p : places){
            LatLng pos = new LatLng(p.lat, p.lon);

            Marker m = _map.addMarker(new MarkerOptions().position(pos).title(p.name));
            if (p.dist < dist) {
                closestMarket = m;
                dist = p.dist;
            }

            _markerToPlace.put(m, p);
        }

        CameraPosition pos;
        if (moveCamera && closestMarket != null) {
            pos = new CameraPosition.Builder().target(closestMarket.getPosition()).zoom(10).build();
            closestMarket.showInfoWindow();
            _map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
        } else {
            CameraPos savedPos = ((MainActivity)getActivity()).getCameraPosition();
            LatLng ll = new LatLng(savedPos.lat, savedPos.lon);
            pos = new CameraPosition.Builder().target(ll).zoom(savedPos.zoom).build();
            _map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }

        _moveCamera = false;
    }
}
