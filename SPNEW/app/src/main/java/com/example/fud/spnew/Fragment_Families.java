package com.example.fud.spnew;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class Fragment_Families extends Fragment {
    String[] families;

    public Fragment_Families() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper_Database helperDatabase = new Helper_Database(getContext());
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        families = getResources().getStringArray(R.array.species_array);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_families, container, false);
        final ArrayList<String> holder =  new ArrayList<>(Arrays.asList(families));
        ListView listView = (ListView) view.findViewById(R.id.listview);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.families_row,holder);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(families[position]);
            }
        });

        return view;
    }

    private void showDetails(String species){
        Fragment_Details details = Fragment_Details.newInstance(species);
        details.show(getFragmentManager(), "dialog");
    }

}
