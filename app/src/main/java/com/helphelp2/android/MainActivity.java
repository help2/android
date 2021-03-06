package com.helphelp2.android;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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
import com.helphelp2.android.models.Place;
import com.helphelp2.android.models.PlacesResponse;
import com.helphelp2.android.placeslist.PlacesListFragment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private RequestQueue _queue;
    private GoogleApiClient _googleApi;
    Toast _toast;
    private Location _loc;
    private List<Place> _places;

    private PlacesStorageFragment _dataFrag;

    @Bind(R.id.heart_image)
    ImageView _heartImageView;

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
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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
    PlacesListFragment _placesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        _placesListFragment = new PlacesListFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, _mapFragment, "map");
        fragmentTransaction.commit();

        _viewSwitcher = new ViewSwitcher(this, _heartImageView,
                new ViewSwitcher.IViewSwicherListener() {

                    @Override
                    public void onViewSwitched() {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        if (_mapFragment.isVisible()) {
                            transaction.replace(R.id.fragment_container, _placesListFragment);
                        } else {
                            transaction.replace(R.id.fragment_container, _mapFragment);
                        }
                        transaction.commitAllowingStateLoss();
                    }

                    @Override
                    public void onLongPress() {
                        fetchPlaces();
                    }

                    @Override
                    public void onShortPress() {
                        InfoBoxDialogFragment dialog = new InfoBoxDialogFragment();
                        dialog.show(getSupportFragmentManager(), "infobox");
                    }
                });

        if (!_dataFrag.isPlacesFetched()) {
            if (_toast != null) _toast.cancel();
            _toast = Toast.makeText(this, getString(R.string.acquiring_location), Toast.LENGTH_SHORT);
            _toast.show();
            buildGoogleApiClient();
        } else {
            _mapFragment.placePins(_places, false);
            _placesListFragment.setPlaces(_places);
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

        String backendUrl = getBackendUrl();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, backendUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parsePlaces(response);
                        updateDistanceString(_loc == null);
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

                        _placesListFragment.setPlaces(_places);

                        _viewSwitcher.playInitialAnimation(_heartImageView);
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

    @NonNull
    private String getBackendUrl() {
        Uri uri = Uri.parse(BuildConfig.API_END_POINT);
        if (_loc == null) {
            uri = uri.buildUpon()
                    .appendPath(BuildConfig.API_PLACES_READ_PATH)
                    .build();
        } else {
            uri = uri.buildUpon()
                    .appendPath(BuildConfig.API_PLACES_READ_PATH)
                    .appendQueryParameter(BuildConfig.API_QUERY_PARAMETER_LATITUDE,
                            "" + _loc.getLatitude())
                    .appendQueryParameter(BuildConfig.API_QUERY_PARAMETER_LONGITUDE,
                            "" + _loc.getLongitude())
                    .build();
        }
        return uri.toString();
    }

    private void parsePlaces(JSONObject placesJsonString) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<PlacesResponse> jsonAdapter =
                moshi.adapter(PlacesResponse.class);
        PlacesResponse placesResponse = null;
        try {
            placesResponse = jsonAdapter.fromJson(placesJsonString.toString());
            _places = placesResponse.places;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDistanceString(boolean locationIsUnknown) {
        List<Place> places = _places;
        for (int i = 0, size = places.size(); i < size; ++i) {
            Place place = places.get(i);
            if (locationIsUnknown) {
                place.distance = 0;
            }
            place.distStr = Place.getDistanceStr(place.distance);
        }
    }

    CameraPos _cameraPos;

    public void saveCameraPosition(CameraPos cameraPos) {
        _cameraPos = cameraPos;
    }

    public CameraPos getCameraPosition() {
        return _cameraPos;
    }
}
