package fi.stipakov.heartproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Locale;

public class ListPlacesFragment extends ListFragment implements AdapterView.OnItemClickListener {
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

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Place p = (Place) getListAdapter().getItem(position);

        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", p.addr1);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}
