package com.opsc.monumental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FavouriteAdapter extends ArrayAdapter<FavouriteList> {

    public FavouriteAdapter(@NonNull Context context, ArrayList<FavouriteList> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item2, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        FavouriteList currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        TextView locationName = (TextView) currentItemView.findViewById(R.id.field_NAME);
        assert currentNumberPosition != null;
        locationName.setText(currentNumberPosition.getLocationName());

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView locationAddress = (TextView) currentItemView.findViewById(R.id.field_ADDRESS);
        locationAddress.setText(currentNumberPosition.locationAddress);

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView phoneNumber = (TextView) currentItemView.findViewById(R.id.field_PHONE_NUMBER);
        phoneNumber.setText(currentNumberPosition.getPhoneNumber());

        TextView url = (TextView) currentItemView.findViewById(R.id.field_WEBSITE_URI);
        url.setText(currentNumberPosition.getUrl());

        // then return the recyclable view
        return currentItemView;
    }
}

