package com.major.potholerectifier.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.major.potholerectifier.R;
import com.major.potholerectifier.model.PotHole;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PotHoleListAdapter extends ArrayAdapter<PotHole> {

    Activity context;
    List<PotHole> data;
    public PotHoleListAdapter(@NonNull Activity  context, List<PotHole> data) {
        super(context, 0,data);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public PotHole getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable PotHole item) {
        return super.getPosition(item);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.pot_hole_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.pot_hole_image);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        PotHole ph = getItem(position);
        titleText.setText(ph.getStatus()+"");
        Log.e("Tsasdas :",ph.getImage());
        // Base 64 -> Byte Array -> Bitmap -> Image view
        byte[] decodedString = Base64.decode(ph.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
        subtitleText.setText(ph.getLocality() + "("+ph.getLandmark()+")");
        return rowView;
    };

    public void updateAdapter(List<PotHole> newPotHoles) {
        this.data = newPotHoles;
        notifyDataSetChanged();
    }




}
