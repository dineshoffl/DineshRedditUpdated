package com.dineshredditsample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dineshredditsample.R;
import com.dineshredditsample.models.Spinner_Array;

import java.util.List;

/**
 * Created by $Dinesh on 12/20/2019.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<Spinner_Array> items;
    private final int mResource;

    public CustomArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        Spinner_Array data = items.get(position);

        TextView text_spinner = (TextView) view.findViewById(R.id.text_spinner);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        text_spinner.setText(data.getName());
        image.setImageResource(data.getImage());




        return view;
    }
}