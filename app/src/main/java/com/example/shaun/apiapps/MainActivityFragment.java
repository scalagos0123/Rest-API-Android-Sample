package com.example.shaun.apiapps;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<User> users;
    private UserAdapter adapter;
    private ProgressBar progressBar;

    public MainActivityFragment() {
        users = new ArrayList<>();




        try {
            JSONObject j = new JSONObject();
            j.put("first-name", "Shaun");

            System.out.println(j.toString());
        } catch (JSONException e) {

        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        ListView l = (ListView) v.findViewById(R.id.my_listview);
        UserAdapter adapter = new UserAdapter(getActivity(), R.layout.listadapter_layout, users);
        l.setAdapter(adapter);
        this.adapter = adapter;

        Button getUser = (Button) v.findViewById(R.id.get_user);
        final Button insertUser = (Button) v.findViewById(R.id.insert_user);

        insertUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUser();
            }
        });
        getUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsers();
            }
        });

        return v;
    }

    /**
     * Button 1 action
     */
    public void getUsers() {
        progressBar.setVisibility(View.VISIBLE);
        users.removeAll(users);
        GetUsers g = new GetUsers();
        g.execute();
    }

    /**
     * Button 2 action
     */
    public void insertUser() {
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.add_user_dialog, null);

//        getting values
        final TextInputEditText name = (TextInputEditText) dialogView.findViewById(R.id.user_name_input);
        final TextInputEditText age = (TextInputEditText) dialogView.findViewById(R.id.user_age_input);
        final TextInputEditText location = (TextInputEditText) dialogView.findViewById(R.id.user_location_input);

        db.setTitle("Add user");
        db.setView(dialogView);
        db.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                JSONObject user = new JSONObject();
//                build the json object by getting the values from the view
//                surrounding with try catch to cover JSONException (as you can't directly put strings in a JSON (for some reason)

                try {
                    user.put("user_name", name.getText().toString());
                    user.put("user_age", age.getText().toString());
                    user.put("user_location", location.getText().toString());
                    System.out.println(user.toString());
                } catch (JSONException j) {
                    j.printStackTrace();
                }

//                insert the users and pass the JSONObject as parameter
                InsertUser iu = new InsertUser();
                iu.execute(user);
            }
        });

        db.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        db.show();
    }

    public class GetUsers extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                URL url = new URL("http://frameworkwebapp25.azurewebsites.net/actions/users");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

//                get the returned json from inputstream
                BufferedReader receive = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

//                build the inputstream to a string 1st
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = receive.readLine()) != null) {
                    buffer.append(line);
                }

                JSONArray array = new JSONArray(buffer.toString());
                System.out.println(array);
                return array;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            progressBar.setVisibility(View.GONE);

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    users.add(new User(
                            (String) jsonObject.get("name"),
                            (String) jsonObject.get("age"),
                            (String) jsonObject.get("location"))
                    );
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class InsertUser extends AsyncTask<JSONObject, Void, Boolean> {
        String message;

        @Override
        protected Boolean doInBackground(JSONObject... jsonObjects) {
            message = "";
            JSONObject object = jsonObjects[0];
            String toSend = "userdata=" + object.toString();
            System.out.println(toSend);

            try {
                URL url = new URL("http://frameworkwebapp25.azurewebsites.net/actions/insert_user");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.getOutputStream().write(toSend.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                message = sb.toString();
                System.out.println(message);
                return Boolean.TRUE;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean booleanResult) {
            super.onPostExecute(booleanResult);
            if (booleanResult == Boolean.TRUE) {
                users.removeAll(users);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                GetUsers u = new GetUsers();
                u.execute();
            }
        }
    }
}
