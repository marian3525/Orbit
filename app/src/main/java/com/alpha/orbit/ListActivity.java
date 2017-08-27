package com.alpha.orbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<String> listItems = new ArrayList<>();
        ArrayAdapter<String> adapter;

        listItems.add(getString(R.string.mercury_data));
        listItems.add(getString(R.string.venus_data));
        listItems.add(getString(R.string.earth_data));
        listItems.add(getString(R.string.mars_data));
        listItems.add(getString(R.string.jupiter_data));
        listItems.add(getString(R.string.saturn_data));
        listItems.add(getString(R.string.uranus_data));
        listItems.add(getString(R.string.neptune_data));
        listItems.add(getString(R.string.pluto_data));
        listItems.add(getString(R.string.moon_data));
        listItems.add(getString(R.string.titan_data));
        listItems.add(getString(R.string.enceladus_data));
        listItems.add(getString(R.string.mimas_data));
        listItems.add(getString(R.string.iapteus_data));
        listItems.add(getString(R.string.deimos_data));
        listItems.add(getString(R.string.phobos_data));
        listItems.add(getString(R.string.europa_data));
        listItems.add(getString(R.string.io_data));
        listItems.add(getString(R.string.callisto_data));
        listItems.add(getString(R.string.sun_data));

        ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, R.layout.simple_row, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Object entry = adapterView.getAdapter().getItem(position);
                        Log.e("!!!", "onItemClick: " + entry.toString());
                        Intent intent = new Intent();
                        intent.putExtra("stringData", entry.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
        listView.setAdapter(adapter);
    }
}