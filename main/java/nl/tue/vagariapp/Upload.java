package nl.tue.vagariapp;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.camera2.*;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * Created by Stijn Albert  on 26-3-2017.
 */
public class Upload extends Activity  implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemSelectedListener {

    public Bitmap toBeUploaded;
    public String encoded;
    private EditText mAlbumText;
    private EditText mLocationText;
    private EditText mDateText;

    ArrayList<String> spinnerArray = new ArrayList<>();


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    static double mLatitude;
    static double mLongitude;
    String album;


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    boolean gps_enabled;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final String currentUser = Login.getUID();
    final DatabaseReference ref1 = database.getReference("users/" + currentUser + "/albums/");
    final DatabaseReference ref = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.upload2);

        buildGoogleApiClient();

        mGoogleApiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        gps_enabled = true;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Upload.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });



        // Close Button
        ImageButton closeButton = (ImageButton) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btn_choose_photo = (Button)findViewById(R.id.bFiles); // Replace with id of your button.
        btn_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });

        Button makePhoto = (Button)findViewById(R.id.bCamera);
        makePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        final String today =  day+"/"+month+"/"+year;

        RelativeLayout bUpload = (RelativeLayout) findViewById(R.id.bUpload);
        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLatLng latLng = new MyLatLng(mLatitude, mLongitude);

                mLocationText = (EditText)findViewById(R.id.LocationInput);
                mDateText = (EditText)findViewById(R.id.DateInput);


                final String location = mLocationText.getText().toString();
                final String date = mDateText.getText().toString();

                updateLocation();
                final String currentUser = Login.getUID();

                if (album.equals("Choose Album")) {
                    Toast.makeText(Upload.this, "Please choose an album",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (toBeUploaded == null) {
                    Toast.makeText(Upload.this, "No image selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!gps_enabled && location.trim().length()==0) {
                    new AlertDialog.Builder(Upload.this)
                            .setTitle("Location services disabled and no location provided")
                            .setMessage("Your image will be uploaded without a location.")
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue without location
                                    if (date.trim().length() == 0) {
                                        writeNewPicture(currentUser, encoded, album, null, today);

                                    } else {
                                        writeNewPicture(currentUser, encoded, album, null, date);
                                    }

                                    Intent i = new Intent(Upload.this, Tabs.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //cancel upload
                                    return;
                                }
                            })
                            .show();

                } else {
                    if (date.trim().length() == 0) {
                        if (location.trim().length() == 0) {
                            writeNewPicture(currentUser, encoded, album, latLng, today);

                        } else {
                            MyLatLng customLocation = getLocationFromAddress(location);
                            writeNewPicture(currentUser, encoded, album, customLocation, today);
                        }
                    } else {
                        if (location.trim().length() == 0) {
                            writeNewPicture(currentUser, encoded, album, latLng, date);

                        } else {
                            MyLatLng customLocation = getLocationFromAddress(location);
                            writeNewPicture(currentUser, encoded, album, customLocation, date);
                        }
                    }
                    Intent i = new Intent(Upload.this, Tabs.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        spinnerArray.clear();
        spinnerArray.add("Choose Album");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

        final ArrayList<String> tempAlbums = new ArrayList<>();

        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String album = dataSnapshot.getKey();
                tempAlbums.add(album);
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
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                java.util.Collections.sort(tempAlbums);
                for (int i = 0; i < tempAlbums.size(); i++) {
                    spinnerArray.add(tempAlbums.get(i));
                }
                spinnerArray.add("Create new");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void writeNewPicture(String userId, String image, String album, MyLatLng location, String date) {
        Picture picture = new Picture(image, location, date);
        ref.child("users").child(userId).child("albums").child(album).push().setValue(picture);
        Log.d("test", "writeNewPicture: exec ");
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int check = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView preview = (ImageView)findViewById(R.id.preview);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            gps_enabled = true;
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            gps_enabled = false;
                            break;
                        default:
                            break;
                    }
                    break;

                case 1234:
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;

                        toBeUploaded = BitmapFactory.decodeFile(filePath, options);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        toBeUploaded.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        preview.setImageBitmap(toBeUploaded);

            /* Now you have choosen image in Bitmap format in object "yourSelectedImage". You can use it in way you want! */
                    }
                case 33:
                    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();

                        toBeUploaded = (Bitmap) extras.get("data");
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        toBeUploaded.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        preview.setImageBitmap(toBeUploaded);
                    }
            }
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 33;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            gps_enabled = true;
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        } else {
            gps_enabled = false;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        updateLocation();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    public MyLatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(Upload.this, Locale.getDefault());
        List<Address> address;
        MyLatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new MyLatLng(location.getLatitude(), location.getLongitude());

            return p1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String userSpinnerChoice = parent.getItemAtPosition(pos).toString();
        if (userSpinnerChoice.equals("Create new")) {
            final EditText input = new EditText(Upload.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            new AlertDialog.Builder(Upload.this)
                    .setView(input)
                    .setTitle("Create new album")
                    .setMessage("Provide a name for your new album.")
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ref1.child(input.getText().toString()).setValue(1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            album = userSpinnerChoice;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {    }
}
