package com.gametic.sdk.gametic;

//Gametic SDK manager
//Singleton

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GameticSDKManager {


    //SDK urls
    String Signup_URL;
    String Event_URL;
    String Default_Event_URL;
    String Segment_URL;




    private Context appContext;
    public String ProjectName;
    public String DeveloperID;
    public static GameticSDKManager instance;
    private  GameticRequestManager SDK_ReqeustManager;
    private String UserID;

    //private constructor:
    private GameticSDKManager(Activity activity, String projectName, String developerID) throws GameticDataStructures.GameticException{


        appContext = activity.getApplicationContext();
        ProjectName = projectName;
        if (ProjectName==null||projectName.isEmpty()){
            throw new GameticDataStructures.GameticException("Null Project Name");
        }
        DeveloperID = developerID;
        if(DeveloperID==null || developerID.isEmpty()){
            throw new GameticDataStructures.GameticException("Null DeveloperID");
        }
        SDK_ReqeustManager = new GameticRequestManager();
        Signup_URL = "analytic/signup";
        Event_URL = "analytic/events";
        Default_Event_URL= "analytic/default_events";
        Segment_URL = "analytic/segments";
        SharedPreferences sharedPreferences = activity.getSharedPreferences("GameticSharedPrefrences", Context.MODE_PRIVATE);
        if(UserID==null){
            UserID= sharedPreferences.getString("UserID",null);
        }

        if (UserID==null) {
            NewUser();
            sharedPreferences.edit().putString("UserID",UserID).apply();

        }
        else{
            NewSession();
        }

    }

    public static GameticSDKManager getInstance(Activity activity, String projectName, String developerID){
        if(instance == null){
            try {
                instance = new GameticSDKManager(activity,projectName,developerID);
            } catch (GameticDataStructures.GameticException e) {
                Log.e("Gametic",e.getMessage());
                e.printStackTrace();
            }

        }
        return instance;
    }

    //get functions
    public String getProjectName(){return ProjectName;}
    public String getDeveloperID(){return DeveloperID;}
    public String getUserID(){return UserID;}


    public  void setUserID(String mUserID){
        UserID = mUserID;
    }

/**************************************************************************************************/
    private JsonObject getProjectBodyAsJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("project_name",ProjectName);
        jsonObject.addProperty("developer_id",DeveloperID);
        jsonObject.addProperty("id",UserID);
        return jsonObject;
    }

    //Send Request functions:

    private void NewUser() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("developer_id", DeveloperID);
        jsonObject.addProperty("project_name", ProjectName);
        GameticDataStructures.Project project = new GameticDataStructures.Project();
        project.project_name = ProjectName;
        project.developer_id = DeveloperID;
        SDK_ReqeustManager.SignUp(project);
        final JsonObject responseJson = SDK_ReqeustManager.responseJson;

    }
    public void NewUserResponse(JsonObject responseJson){
        UserID = responseJson.get("content").getAsJsonObject().get("app_user_id").getAsString();
        SharedPreferences sharedPreferences =  appContext.getSharedPreferences("GameticSharedPrefrences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserID",UserID);
        editor.commit();
    }

    private void SendEventReq(String Url,String eventName,  ArrayList<GameticDataStructures.Parameter> parameters){
        JsonObject jsonObject = getProjectBodyAsJson();
        jsonObject.addProperty(Url,(new GameticDataStructures.Event(eventName, parameters)).getAsJson().toString());
        SDK_ReqeustManager.SendRequest(Event_URL,jsonObject);
    }

    private void NewSession(){
        SendEventReq("analytic/default_events","#NewSession",new ArrayList<GameticDataStructures.Parameter>(0));
    }
    public void SendEvent(String eventName, ArrayList<GameticDataStructures.Parameter> parameters, boolean tryAgainInCaseOfError){
        SendEventReq("events",eventName,parameters);
    }
    public void SendSegment(String SegmentName, String SegmentValue){
        JsonObject jsonObject = getProjectBodyAsJson();
        jsonObject.addProperty("segment",(new GameticDataStructures.Segment(SegmentName,SegmentValue)).getAsJson().toString());
        SDK_ReqeustManager.SendRequest(Segment_URL,jsonObject);
    }
    public void SendPurchase(String Market, int Value, boolean tryAgainInCaseOfError){
        ArrayList<GameticDataStructures.Parameter> parameters = new ArrayList<>();
        parameters.add(new GameticDataStructures.StringParameter("Market",Market));
        parameters.add(new GameticDataStructures.NumberParameter("Count",Value));
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        parameters.add(new GameticDataStructures.StringParameter("Day",dayFormat.format(Calendar.getInstance().getTime())));
        dayFormat = new SimpleDateFormat("HH");
        parameters.add(new GameticDataStructures.StringParameter("Hour",dayFormat.format(Calendar.getInstance().getTime())));
        SendEventReq("analytic/default_events","#Purchase",parameters);
        SendSegment("#Purchase","Purchased");
    }

}
