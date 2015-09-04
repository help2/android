package fi.stipakov.heartproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ListPlacesFragment extends ListFragment {
    private LayoutInflater _inf;

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
        List<Place> places = ((MainActivity)getActivity()).getPlaces();

        if (places != null) {
            setListAdapter(new PlacesAdapter(getActivity(),
                    places.toArray(new Place[places.size()])));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView lv = getListView();
        View header = _inf.inflate(R.layout.places_list_header, lv, false);
        lv.addHeaderView(header, null, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
