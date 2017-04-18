package nl.tue.vagariapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by s149211 on 18-4-2017.
 */
public class Popup extends Activity {

    Bitmap picture;
    LatLng latlong;
    String dateTimeText;
    String albumText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.popup);

        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.drawable.cast_abc_scrubber_control_off_mtrl_alpha);
        EditText album = (EditText) findViewById(R.id.editText);
        EditText dateTime = (EditText) findViewById(R.id.editText2);
        EditText location = (EditText) findViewById(R.id.editText3);
        Button close = (Button) findViewById(R.id.button5);
        Button map = (Button) findViewById(R.id.button6);


        //this if checks for the neccesary extra data with the intent.
        //put all the date in a Bundle and put the bundle as an extra to the intent.
        if(getIntent().getExtras().containsKey("PicNData")){
            Bundle bundle = getIntent().getParcelableExtra("PicNData");
            albumText = bundle.getParcelable("album");
            dateTimeText = bundle.getParcelable("dateTime");
            latlong = bundle.getParcelable("location");
            picture = bundle.getParcelable("picture");

            //img.setImageResource(set image resource here!);
            album.setText(albumText);
            dateTime.setText(dateTimeText);
            location.setText(latlong.toString());

            //this onclicklistener ensures that pressing the location moves you directly to map screen
            //it does so by putting a boolean as an extra to the intent, and a latlong to get to
            //the correct position.
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity2 = new Intent (Popup.this, Tabs.class);
                    activity2.putExtra("mapStatus", true);
                    activity2.putExtra("latLong", latlong);
                    startActivity (activity2);
                }
            });

        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Popup.this, Tabs.class));
            }
        });
    }
}
