package com.example.rafael.concapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    // Alert Dialog Manager
  //  AlertDialogManager alert = new AlertDialogManager();

    // Construct the data source
    ArrayList<Team> arrayOfTeams;
    TeamAdapter adapter;
    // ListView
    ListView listView;
    SearchView searchView;

    Button btnTeamAdd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----------------Session class instance
        session = new SessionManager(getApplicationContext());
        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);


        //----Add Team Button
        // Login button
        btnTeamAdd = (Button) findViewById(R.id.btnTeamAdd);


        // Login button click event
        btnTeamAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //alert.showAlertDialog(MainActivity.this, "Add New", "Please Insert codes haha", false);
                try {
                    Intent myIntent = new Intent(MainActivity.this, TeamInsert.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    startActivity(myIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Get a reference to the ListView, and attach this adapter to it.
        arrayOfTeams = new ArrayList<Team>();
        // Attach the adapter to a ListView
        adapter = new TeamAdapter(this, arrayOfTeams);
        new TeamAsync().execute();
        searchView = (SearchView) findViewById(R.id.teamsSearchView);
        listView = (ListView)findViewById(R.id.teamsListView);
        listView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
               // Log.d("FILTER", "Receiving: " + text);
                adapter.getFilter().filter(text);
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuId_settings:
                // newGame();
                return true;
            case R.id.menuId_help:
                // showHelp();
                return true;
            case R.id.menuId_logout:
                session.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class TeamAsync extends AsyncTask<String, String, JSONArray> {

        private static final String URL = "http://10.0.2.2/ConcApp/getTeams.php";


        JSONParser jsonParser = new JSONParser();



        @Override
        protected JSONArray doInBackground(String... params) {

            try {
                // PREPARING PARAMETERS..
                HashMap<String, String> args = new HashMap<>();
                args.put("Label_Param1", "param1");
                args.put("Label_Param2", "param2");

                Log.d("request", "starting");

                JSONArray json = jsonParser.makeHttpRequest(
                        URL, "POST", args);

                if (json != null) {
                    Log.d("JSON REQUEST", params.toString());
                    Log.d("JSON result", json.toString());
                    // RETURNING THE ANSWER FROM THE SERVER TO
                    // onPostExecute IN THE MAIN THREAD
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (jsonArray != null) {
                ArrayList<Team> arrayOfTeams = Team.fromJson(jsonArray);
                adapter.clear();
                adapter.addAll(arrayOfTeams);
            }
        }
    }

}
