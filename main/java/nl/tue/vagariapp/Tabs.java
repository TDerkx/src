package nl.tue.vagariapp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stijn Albert  on 19-3-2017.
 */

public class Tabs extends FragmentActivity  implements OnMapReadyCallback {

    public TabHost th;

    public static ArrayList<Picture> myPhotos = new ArrayList<>();
    public static ArrayList<String> keys = new ArrayList<>();

    public static Activity main;

    private GoogleMap map;

    int selectedPic;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthList;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    public List<String> listDataHeader = new ArrayList<>();
    final public static HashMap<String, ArrayList<Picture>> listDataChild = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        main = this;

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);


        // preparing list data
        prepareListData();

        // setting list adapter

        myPhotos.clear();
        keys.clear();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onResume();

        final String currentUser = Login.getUID();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users/" + currentUser + "/albums/");
        ref.keepSynced(true);


        setUpTabs();

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



        ImageButton bSettings = (ImageButton)findViewById(R.id.bSettings);
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tabs.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });

        /*final ImageButton bTrash = (ImageButton)findViewById(R.id.bTrash);
        bTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Deleting: "+ selectedPic + " -- " +listDataHeader.get(selectedPic));
                String key = listDataHeader.get(selectedPic);
                ref.child(key).removeValue();
                bTrash.setVisibility(View.INVISIBLE);
                bTrash.setClickable(false);
                finish();
            }
        });
        bTrash.setVisibility(View.INVISIBLE);
        bTrash.setClickable(false);

        GridView gv = (GridView)findViewById(R.id.lblListItem);

        // Delete function, doesn't work yet
        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPic = position;
                System.out.println(selectedPic + " -- " +keys.get(selectedPic));
                bTrash.setVisibility(View.VISIBLE);
                bTrash.setClickable(true);
                return true;
            }
        });*/


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        /* use the followimg part in a for each statement
         * or something for all pictures in a database.
         * sort colors by album
         */
        map = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        LatLng standard = new LatLng(0, 0);
        map.moveCamera(CameraUpdateFactory.newLatLng(standard));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);

    }

    /*this adds a marker */
    private void addMarker(GoogleMap map, LatLng latlong, String title, String snippet, Float hue) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(hue);
        map.addMarker(new MarkerOptions()
                .position(latlong)
                .title(title)
                .snippet(snippet))
                .setIcon(bitmapDescriptor);
    }

    public void setUpTabs() {
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

        //Make 'GALLERY' the default tab and set colors of tabs
        th.setCurrentTab(1);
        TextView upload = (TextView)th.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        TextView gallery = (TextView)th.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        TextView maps = (TextView)th.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        upload.setTextColor(Color.parseColor("#e3d2e3"));
        gallery.setTextColor(Color.parseColor("#ffffff"));
        maps.setTextColor(Color.parseColor("#e3d2e3"));


        //change tabs' textcolors onTabChanged
        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < th.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) th.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#e3d2e3"));
                }
                TextView tv = (TextView) th.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        //when user clicks on UPLOAD tab, go to upload activity
        th.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tabs.this, Upload.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void createMarkers() {
        map.clear();
        ArrayList<LatLng> gglLocations = new ArrayList<>();
        gglLocations.clear();
        for(int i = 0; i < myPhotos.size(); i++ ) {
            com.google.android.gms.maps.model.LatLng mapsLatLng =
                    new com.google.android.gms.maps.model.LatLng(myPhotos.get(i).location.getLat(),
                            myPhotos.get(i).location.getLng());
            gglLocations.add(mapsLatLng);
        }

        for(int i = 0; i < myPhotos.size(); i++) {
            if (myPhotos.get(i).location == null) {

            } else {
                addMarker(map, gglLocations.get(i), "title", myPhotos.get(i).date, (float) 150);
            }
        }
    }

    private void prepareListData() {

        final String currentUser = Login.getUID();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference albumRef = database.getReference("users/" + currentUser + "/albums/");

        albumRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String album = dataSnapshot.getKey();
                listDataHeader.add(album);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < listDataHeader.size(); i++) {
                    retrieveAlbum(listDataHeader.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void retrieveAlbum(final String album) {
        final String currentUser = Login.getUID();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference picRef = database.getReference("users/" + currentUser + "/albums/" + album);
        final ArrayList<Picture>list = new ArrayList<>();
        list.clear();


        picRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Picture picture = dataSnapshot.getValue(Picture.class);
                list.add(picture);
                myPhotos.add(picture);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        picRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDataChild.put(album, list);
                if (listDataChild.size() == listDataHeader.size()) {
                    listAdapter = new ExpandableListAdapter(Tabs.this, listDataHeader, listDataChild);
                    expListView.setAdapter(listAdapter);
                    for (int i = 0; i < listAdapter.getGroupCount(); i ++) {
                        expListView.expandGroup(i);
                    }
                createMarkers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}