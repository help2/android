package com.helphelp2.android;

import android.location.Location;
import android.support.v4.app.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private RequestQueue _queue;
    private GoogleApiClient _googleApi;
    Toast _toast;
    private Location _loc;
    private List<Place> _places;

    private PlacesStorageFragment _dataFrag;

    @Override
    public void onConnected(Bundle bundle) {
        _toast.cancel();

        _loc = LocationServices.FusedLocationApi.getLastLocation(_googleApi);

        if (!_dataFrag.isPlacesFetched()) {
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

    protected void onDestroy() {
        super.onDestroy();

        _viewSwitcher.onActivityDestroyed();

        _dataFrag.setData(_places);
        _dataFrag.setCameraPosition(_cameraPos);
        _dataFrag.setLocation(_loc);
    }

    ViewSwitcher _viewSwitcher;
    MapsFragment _mapFragment;
    ListPlacesFragment _listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _queue = Volley.newRequestQueue(this);

        FragmentManager fm = getSupportFragmentManager();
        _dataFrag = (PlacesStorageFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (_dataFrag == null) {
            // add the fragment
            _dataFrag = new PlacesStorageFragment();
            fm.beginTransaction().add(_dataFrag, "data").commit();
            _places = new LinkedList<>();
        } else {
            _places = _dataFrag.getPlaces();
            _cameraPos = _dataFrag.getCameraPosition();
            _loc = _dataFrag.getLocation();
        }

        _mapFragment = new MapsFragment();
        _listFragment = new ListPlacesFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, _mapFragment, "map");
        fragmentTransaction.commit();

        _viewSwitcher = new ViewSwitcher(this, findViewById(R.id.heart_image), new ViewSwitcher.IViewSwicherListener() {
            @Override
            public void onViewSwitched() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                if (_mapFragment.isVisible()) {
                    transaction.replace(R.id.fragment_container, _listFragment);
                } else {
                    transaction.replace(R.id.fragment_container, _mapFragment);
                }
                transaction.commitAllowingStateLoss();
            }

            @Override
            public void onLongPress() {
                fetchPlaces();
            }
        });

        if (!_dataFrag.isPlacesFetched()) {
            if (_toast != null) _toast.cancel();
            _toast = Toast.makeText(this, getString(R.string.acquiring_location), Toast.LENGTH_SHORT);
            _toast.show();
            buildGoogleApiClient();
        } else {
            _mapFragment.placePins(_places, false);
            _listFragment.setPlaces(_places);
        }

        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                InfoBoxDialogFragment dialog = new InfoBoxDialogFragment();
                dialog.show(getSupportFragmentManager(), "infobox");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void parsePlaces(JSONObject obj) {
        _places.clear();

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

                place.helpers = p.getBoolean("helpers") ? getString(R.string.need_helpers) : "";

                if (_loc == null) {
                    place.dist = 0;
                }

                place.distStr = Place.getDistanceStr(place.dist);

                List<String> addr1 = new LinkedList<>();
                addr1.add(place.street);
                if (!place.zipcode.isEmpty()) {
                    addr1.add(place.zipcode);
                }
                addr1.add(place.city);
                place.addr1 = TextUtils.join(", ", addr1);

                List<String> addr2 = new LinkedList<>();
                if (!place.person.isEmpty()) {
                    addr2.add(place.person);
                }
                if (!place.phone.isEmpty()) {
                    addr2.add(String.format("<a href=\"tel:%s\">%s</a>", place.phone, place.phone));
                }
                if (!place.hours.isEmpty()) {
                    addr2.add(place.hours);
                }
                if (!place.website.isEmpty()) {
                    addr2.add(String.format("<a href=\"%s\">%s</a>", place.website, place.website));
                }

                place.addr2 = TextUtils.join(", ", addr2);

                _places.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        _toast = Toast.makeText(this, getString(R.string.fetching_places), Toast.LENGTH_SHORT);
        _toast.show();

        String url = getString(R.string.backend_url) + "/heart/places/";
        if (_loc != null) {
            url += String.format("?lat=%f&lon=%f", _loc.getLatitude(), _loc.getLongitude());
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parsePlaces(response);
                        _dataFrag.setPlacesFetched();
                        if (_toast != null) {
                            _toast.cancel();
                        }
                        _toast = Toast.makeText(MainActivity.this,
                                _loc != null ? getString(R.string.ready) :
                                        getString(R.string.location_not_available), Toast.LENGTH_SHORT);
                        _toast.show();

                        if (_mapFragment.isVisible()) {
                            _mapFragment.placePins(_places, true);
                        }

                        _listFragment.setPlaces(_places);

                        _viewSwitcher.playInitialAnimation(findViewById(R.id.heart_image));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (_toast != null) {
                            _toast.cancel();
                        }
                        _toast = Toast.makeText(MainActivity.this,
                                getString(R.string.fetching_places_error), Toast.LENGTH_LONG);
                        _toast.show();
                    }
                });
        _queue.add(jsObjRequest);
    }

    CameraPos _cameraPos;

    public void saveCameraPosition(CameraPos cameraPos) {
        _cameraPos = cameraPos;
    }

    public CameraPos getCameraPosition() {
        return _cameraPos;
    }
}
