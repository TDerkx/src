package nl.tue.vagariapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Stijn Albert  on 19-3-2017.
 */

public class Tabs extends FragmentActivity  implements OnMapReadyCallback {

    public TabHost th;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);

        mAuth = FirebaseAuth.getInstance();
        mAuthList = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed in
                    startActivity(new Intent(Tabs.this, Login.class));

                }
            }
        };

        Button logout;
        View toolbar = findViewById(R.id.tool_bar);

        logout = (Button) toolbar.findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignout();
            }
        });

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
        th.addTab(specs);

        th.setCurrentTab(1);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onResume();

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

    private void startSignout() {
        mAuth.signOut();
        startActivity(new Intent(Tabs.this, Login.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /* use the followimg part in a for each statement
         * or something for all pictures in a database.
         * sort colors by album
         */
        LatLng standard = new LatLng(0, 0);
        addMarker(googleMap, standard, "this is a title", "this is a snippet", (float)150);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(standard));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

    }

            /*this adds a marker */
    private void addMarker(GoogleMap map, LatLng latlong, String title, String snippet, Float hue) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(hue);
        map.addMarker(new MarkerOptions()
                .position(latlong)
                .title(title)
                .snippet(snippet))
                .setIcon(bitmapDescriptor);
        ;
    }
}