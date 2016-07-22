package com.example.rafael.concapp;
/*
* taken from https://gist.github.com/webile-android/1068348
* PErmission taken from https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
* https://www.youtube.com/watch?v=TMnQJKtmOd4
* */
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class TeamInsert extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    ImageView teamImage;
    EditText teamName;
    Button teamSendRegister;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_insert);
        teamImage = (ImageView) findViewById(R.id.teamImage);
        teamName  = (EditText) findViewById(R.id.teamName);
        teamSendRegister = (Button) findViewById(R.id.teamSendRegister);

        teamSendRegister.setOnClickListener(this);
        teamImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.teamImage:
                try {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }catch(Exception e){

                    Log.d("ImageParser:", e.toString());
                    e.printStackTrace();
                }

                break;
            case R.id.teamSendRegister:
                try {
                    Log.d("ImageParser:", "Call 1 ");
                    Bitmap image = ((BitmapDrawable) teamImage.getDrawable()).getBitmap();
                    new TeamInsertAsync(image, teamName.getText().toString()).execute();

                }catch(Exception e){
                    Log.d("ImageParser:", e.toString());
                }

                //Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ImageParser:", "Return 1 ");
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
             //   Bundle extras = data.getExtras();
            //    Bitmap imageBitmap = (Bitmap) extras.get("data");
                try{
                    Uri selectedImage = data.getData();
                    teamImage.setImageURI(selectedImage);
                }catch(Exception e){
                    Log.d("ImageParser:", e.toString());
                }
               // teamImage.setImageBitmap(imageBitmap);
               // Toast.makeText(getApplicationContext(), "Kiff!", Toast.LENGTH_LONG).show();
                Log.d("ImageParser:", "Return 2");
            }
        }else{
            Toast.makeText(getApplicationContext(), "Oopss!", Toast.LENGTH_LONG).show();
        }
    }
    private class TeamInsertAsync extends AsyncTask<Void, Void, JSONArray> {

        Bitmap image;
        String name;
        // Alert Dialog Manager
        AlertDialogManager alert = new AlertDialogManager();

        private static final String URL = "http://10.0.2.2/ConcApp/insertTeams.php";
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        public TeamInsertAsync(Bitmap image, String name){
            this.image=image;
            this.name=name;
        }

        @Override
        protected void onPreExecute() {
            Log.d("JSonInsTeam", "Start");
            pDialog = new ProgressDialog(TeamInsert.this);
            pDialog.setMessage("Attempting register...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(JSONArray json) {
            Log.d("JSonInsTeam", "Finish");
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (true) {
                int success = 0;
                String message = "";

                if (json != null) {
                    Toast.makeText(TeamInsert.this, json.toString(),
                            Toast.LENGTH_LONG).show();

                    try {
                        success = json.getJSONObject(0).getInt(TAG_SUCCESS);
                        message = json.getJSONObject(0).getString(TAG_MESSAGE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (success == 1) {
                    Log.d("Success!", message);

                    finish();
                } else {
                    // username / password doesn't match
                    alert.showAlertDialog(TeamInsert.this, "Login failed..", "Username/Password is incorrect", false);
                    Log.d("Failure", message);
                    finish();
                }
            } else {
                // user didn't entered username or password
                // Show alert asking him to enter the details
                alert.showAlertDialog(TeamInsert.this, "Login failed..", "Please enter username and password", false);
            }
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            Log.d("JSonInsTeam", "Background");
            try {
            //    Log.d("JSON REQUEST", "Start ...");
                //Converting image to String
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                // PREPARING PARAMETERS..
                Log.d("JSON REQUEST", "Preparing Params ...");
                HashMap<String, String> args = new HashMap<>();
                args.put("Team_Name", name);
                args.put("Team_Logo", encodedImage);
             //   Log.d("JSON REQUEST", args.toString());
                Log.d("JSON REQUEST", "Firing Json ...");
                JSONArray json = jsonParser.makeHttpRequest(
                        URL, "POST", args);

                if (json != null) {
                    Log.d("JSON REQUEST", params.toString());
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }




}
