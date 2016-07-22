package com.example.rafael.concapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rafael on 02/07/2016.
 * taken from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#creating-the-view-template
 */
public class Team {
    public int Code_Team;
    public String Team_Name;
    public String Team_Logo;

    public String getTeam_Logo(){
        return this.Team_Logo;
    }
    public String getTeam_Name(){
        return this.Team_Name;
    }
    public int getCode_Team(){
        return this.Code_Team;
    }
    // Constructor to convert JSON object into a Java class instance
    public Team(JSONObject object){
        try {
            this.Team_Name = object.getString("Team_Name");
            this.Code_Team = Integer.valueOf(object.getString("Code_Team"));
            this.Team_Logo = object.getString("Team_Logo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<Team> fromJson(JSONArray jsonObjects) {
        ArrayList<Team> teams = new ArrayList<Team>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                teams.add(new Team(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return teams;
    }
}
