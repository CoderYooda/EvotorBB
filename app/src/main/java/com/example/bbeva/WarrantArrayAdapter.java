package com.example.bbeva;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WarrantArrayAdapter extends ArrayAdapter<Warrant> {
    private static final String TAG = "FruitArrayAdapter";
    private List<Warrant> warrantList = new ArrayList<Warrant>();

    static class FruitViewHolder {
        ImageView fruitImg;
        TextView fruitName;
        TextView calories;
    }

    public WarrantArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Warrant object) {
        warrantList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.warrantList.size();
    }

    @Override
    public Warrant getItem(int index) {
        return this.warrantList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FruitViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.warrant_list_item, parent, false);
            viewHolder = new FruitViewHolder();
            viewHolder.fruitImg = (ImageView) row.findViewById(R.id.warrantImg);
            viewHolder.fruitName = (TextView) row.findViewById(R.id.warrantName);
            viewHolder.calories = (TextView) row.findViewById(R.id.warrant_sum);
            row.setTag(viewHolder);
        } else {
            viewHolder = (FruitViewHolder)row.getTag();
        }
        Warrant warrant = getItem(position);
        viewHolder.fruitImg.setImageResource(warrant.getWarrantImg());
        viewHolder.fruitName.setText(warrant.getWarrantName());
        viewHolder.calories.setText(warrant.getWarrantSum());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
