package fi.stipakov.heartproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap _map;
    private RequestQueue _queue;
    private GoogleApiClient _googleApi;
    Toast _toast;
    private Location _loc;
    private HashMap<Marker, Place> _markerToPlace;
    private List<Place> _places;

    private RetainedFragment _dataFrag;

    public static class RetainedFragment extends Fragment {
        // data object we want to retain
        private boolean _placesFetched;
        private List<Place> _places;

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
    }

    @Override
    public void onConnected(Bundle bundle) {
        _toast.cancel();

        _loc = LocationServices.FusedLocationApi.getLastLocation(_googleApi);

        if (!_dataFrag.isPlacesFetched()) {
            _toast = Toast.makeText(this, getString(R.string.fetching_places), Toast.LENGTH_SHORT);
            _toast.show();
            fetchPlaces();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        int a;
        a = 0;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int a;
        a = 0;
    }

    public static class PlaceDialogFragment extends DialogFragment {
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

    protected void onDestroy() {
        super.onDestroy();

        _dataFrag.setData(_places);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        _queue = Volley.newRequestQueue(this);

        _markerToPlace = new HashMap<>();

        FragmentManager fm = getSupportFragmentManager();
        _dataFrag = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (_dataFrag == null) {
            // add the fragment
            _dataFrag = new RetainedFragment();
            fm.beginTransaction().add(_dataFrag, "data").commit();
            _places = new LinkedList<>();

            _toast = Toast.makeText(this, getString(R.string.preparing_map), Toast.LENGTH_SHORT);
            _toast.show();
        } else {
            _places = _dataFrag.getPlaces();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void parsePlaces(JSONObject obj) {
        try {
            for (int i = 0; i < obj.getJSONArray("places").length(); ++ i){
                Place place = new Place();
                place.items = new LinkedList<>();

                JSONObject p = obj.getJSONArray("places").getJSONObject(i);
                for (int j = 0; j < p.getJSONArray("items").length(); ++ j) {
                    place.items.add(p.getJSONArray("items").getString(j));
                }

                JSONObject addr = p.getJSONObject("addr");

                place.name = p.getString("name");
                place.lat = addr.getDouble("lat");
                place.lon = addr.getDouble("lon");
                place.street = addr.getString("street");
                place.city = addr.getString("city");
                place.zipcode = addr.getString("zip");

                place.person = p.getString("person");
                place.hours = p.getString("hours");
                place.phone = p.getString("phone");
                place.website = p.getString("website");
                place.dist = p.getDouble("distance");

                _places.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void placePins(boolean moveCamera) {
        double dist = 10000000;
        Marker closestMarket = null;

        for (Place p : _places){
            LatLng pos = new LatLng(p.lat, p.lon);

            Marker m = _map.addMarker(new MarkerOptions().position(pos).title(p.name));
            if (p.dist < dist) {
                closestMarket = m;
                dist = p.dist;
            }

            _markerToPlace.put(m, p);
        }

        if ((closestMarket != null) && moveCamera){
            CameraPosition pos = new CameraPosition.Builder()
                    .target(closestMarket.getPosition()).zoom(10).build();
            _map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
            closestMarket.showInfoWindow();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;

        googleMap.setMyLocationEnabled(true);
        _map.setOnMarkerClickListener(this);

        if (!_dataFrag.isPlacesFetched()) {
            if (_toast != null) _toast.cancel();
            _toast = Toast.makeText(this, getString(R.string.acquiring_location), Toast.LENGTH_SHORT);
            _toast.show();
            buildGoogleApiClient();
        } else {
            placePins(false);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        _googleApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        _googleApi.connect();
    }

    private void fetchPlaces() {
        String url = getString(R.string.backend_url) + "/heart/places/";
        if (_loc != null) {
            url += String.format("?lat=%f&lon=%f", _loc.getLatitude(), _loc.getLongitude());
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (_map != null) {
                            _toast.cancel();
                            _dataFrag.setPlacesFetched();
                            parsePlaces(response);
                            placePins(true);

                            _toast.cancel();
                            _toast = Toast.makeText(MapsActivity.this,
                                    getString(R.string.ready), Toast.LENGTH_SHORT);
                            _toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _toast = Toast.makeText(MapsActivity.this,
                                getString(R.string.fetching_places_error), Toast.LENGTH_LONG);
                        _toast.show();
                    }
                });
        _queue.add(jsObjRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Place p = _markerToPlace.get(marker);

        List<String> addr = new LinkedList<>();
        addr.add(p.street);
        if (!p.zipcode.isEmpty()) {
            addr.add(p.zipcode);
        }
        addr.add(p.city);

        List<String> addr2 = new LinkedList<>();
        if (!p.person.isEmpty()) {
            addr2.add(p.person);
        }
        if (!p.phone.isEmpty()) {
            addr2.add(String.format("<a href=\"tel:%s\">%s</a>", p.phone, p.phone));
        }
        if (!p.hours.isEmpty()) {
            addr2.add(p.hours);
        }
        if (!p.website.isEmpty()) {
            addr2.add(String.format("<a href=\"%s\">%s</a>", p.website, p.website));
        }

        String name = p.name;
        if (p.dist < 100) {
            name += " (" + p.dist + "m)";
        } else if (p.dist >= 100 && p.dist <= 1000) {
            name += " (" + (int)(p.dist / 100) + "00m)";
        } else {
            name += String.format(" (%.1f km)", p.dist / 1000.);
        }

        DialogFragment dialog = PlaceDialogFragment.newInstance(name, TextUtils.join(", ", addr),
                TextUtils.join(", ", addr2), TextUtils.join(", ", p.items));
        dialog.show(getSupportFragmentManager(), "place");

        return false;
    }
}
