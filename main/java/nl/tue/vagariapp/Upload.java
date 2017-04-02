package nl.tue.vagariapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import java.util.ArrayList;

/**
 * Created by Stijn Albert  on 26-3-2017.
 */
public class Upload extends Activity {
    public static ArrayList<Bitmap> myPhotos = new ArrayList<Bitmap>();
    public ArrayList<Bitmap> getList() {
        return myPhotos;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.upload);

        ImageButton closeButton = (ImageButton) findViewById(R.id.closeButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        Button btn_choose_photo = (Button)findViewById(R.id.photo_choose_button);
        btn_choose_photo.setOnClickListener(btnChoosePhotoPressed);
    }
    
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int check = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (check != PackageManager.PERMISSION_GRANTED) {
            
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                
            } else {
                // No explanation needed, let's request the permission.
                ActivityCompat.requestPermissions((Activity) context, 
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
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
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
    
    public View.OnClickListener btnChoosePhotoPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            final int ACTIVITY_SELECT_IMAGE = 1234;
            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
        }
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            switch (requestcode) {
                case 1234:
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        
                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        myPhotos.add(yourSelectedImage);
                        
                        if (yourSelectedImage != null) {
                            Intent intent = new Intent(Upload.this, Tabs.class);
                            startActivty(intent);
                            finish();
                        }
                    }
            }
        }
    }
}
