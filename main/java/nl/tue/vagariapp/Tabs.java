package nl.tue.vagariapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Stijn Albert  on 19-3-2017.
 */

public class Tabs extends Activity implements OnMapReadyCallback{

    public TabHost th;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);

        th = (TabHost) findViewById(R.id.tabHost);
        th.setup();
        TabHost.TabSpec specs = th.newTabSpec("tag 1");
        specs.setContent(R.id.tab1);
        specs.setIndicator("UPLOAD");
        th.addTab(specs);

        specs = th.newTabSpec("tag 2");
        specs.setContent(R.id.tab2);
        specs.setIndicator("GALLERY");
        th.addTab(specs);

        specs = th.newTabSpec("tag 3");
        specs.setContent(R.id.tab3);
        specs.setIndicator("MAP");
        setContentView(R.layout.tabs);
        th.addTab(specs);

        th.setCurrentTab(1);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        for (int i = 0; i < th.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) th.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#e3d2e3"));
        }

        TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < th.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) th.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#e3d2e3"));
                }

                TextView tv = (TextView) th.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#ffffff"));


            }
        };
        th.setOnTabChangedListener(listener);

        th.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tabs.this, Upload.class);
                startActivity(intent);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }
    @Override
    public void onMapReady(GoogleMap map) {
        /*use following to add markers
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
                */
    }
}
