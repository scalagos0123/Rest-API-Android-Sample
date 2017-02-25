package com.example.shaun.apiapps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shaun on 2/21/2017.
 */

public class UserAdapter extends ArrayAdapter<User> {
    LayoutInflater l;
    private int layout_resource;
    private ArrayList<User> users;

    public UserAdapter(Context context, int layout_resource, ArrayList<User> userArrayList) {
        super(context, layout_resource, userArrayList);
        l = LayoutInflater.from(context);
        this.layout_resource = layout_resource;
        users = userArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = l.inflate(layout_resource, null);
        TextView name = (TextView) v.findViewById(R.id.user_name);
        TextView age = (TextView) v.findViewById(R.id.user_age);
        TextView location = (TextView) v.findViewById(R.id.user_location);

        name.setText(users.get(position).getName());
        age.setText(users.get(position).getAge());
        location.setText(users.get(position).getLocation());

        return v;
    }
}
