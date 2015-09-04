package fi.stipakov.heartproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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
}