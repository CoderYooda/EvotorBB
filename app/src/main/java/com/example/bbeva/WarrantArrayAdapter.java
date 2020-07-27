package com.example.bbeva;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WarrantArrayAdapter extends ArrayAdapter<Warrant> {
    private static final String TAG = "warrantArrayAdapter";
    private List<Warrant> warrantList = new ArrayList<Warrant>();

    static class warrantViewHolder {
        ImageView warrantImg;
        TextView warrantName;
        TextView calories;
        TextView date;
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        warrantViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.warrant_list_item, parent, false);
            viewHolder = new warrantViewHolder();
            viewHolder.warrantImg = (ImageView) row.findViewById(R.id.warrantImg);
            viewHolder.warrantName = (TextView) row.findViewById(R.id.warrantName);
            viewHolder.calories = (TextView) row.findViewById(R.id.warrant_sum);
            viewHolder.date = (TextView) row.findViewById(R.id.warrantDate);
            row.setTag(viewHolder);
        } else {
            viewHolder = (warrantViewHolder)row.getTag();
        }
        Warrant warrant = getItem(position);
        String image_name = warrant.getWarrantImg();
        int icon = warrant.getWarrantIncoming() ? R.drawable.ic_down : R.drawable.ic_up;
        viewHolder.warrantImg.setImageResource(icon);//warrant.getWarrantImg());
        viewHolder.warrantName.setText(warrant.getWarrantName());
        viewHolder.date.setText(warrant.getWarrantDate());

        viewHolder.calories.setText( warrant.getWarrantSum());

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
