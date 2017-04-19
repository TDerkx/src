package nl.tue.vagariapp;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Picture> mPictures;

    public ImageAdapter(Context c, ArrayList<Picture> pictures) {
        mPictures = pictures;
        mContext = c;
    }

    public int getCount() {
        return mPictures.size();
    }

    public Object getItem(int position) {
        return mPictures.get(position);
    }

    public long getItemId(int position) {return 0;}

    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            gridView = new View(mContext);

            gridView = inflater.inflate(R.layout.grid_item, null);

            ImageView imageView = (ImageView) gridView.findViewById(R.id.picture);
            //imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);

            String encodedImage = mPictures.get(position).getImage();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imageView.setImageBitmap(decodedByte);



        } else {
            gridView = (View) convertView;

        }
        return gridView;

    }
}
