package com.gametic.sdk.gametic;

import android.app.ProgressDialog;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ali on 8/4/17.
 */

public class GameticDataStructures {

    public static class Segment{
        String SegmentName;
        String SegmentValue;

        public Segment(String name , String value){
            SegmentName = name;
            SegmentValue = value;
        }
        public String getSegmentName(){return SegmentName;}
        public String getSegmentValue(){return SegmentValue;}
        public JsonObject getAsJson(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",SegmentName);
            jsonObject.addProperty("propertyValue",SegmentName);
            return jsonObject;
        }
    }

    public static class Parameter{
        protected String ParamName;
        protected String ParamType;

        protected Parameter(String name , String type ){
            ParamName = name;
            ParamType = type;
        }
        public String getParamName(){return  ParamName;}
        public String getParamType(){return ParamType;}
        public JsonObject getAsJson(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",ParamName);
            jsonObject.addProperty("type",ParamType);
            return jsonObject;
        }
    }
    public static class StringParameter extends Parameter{

        String value;

        public StringParameter(String name, String mValue) {
            super(name, "string");
            value = mValue;
        }
        public String getValue(){return value;}
        public void setValue(String mValue){value = mValue;}
        public  JsonObject getAsJson(){
            JsonObject jsonObject = super.getAsJson();
            jsonObject.addProperty("value",value);
            return jsonObject;
        }
    }
    public static class NumberParameter extends Parameter{

        float Value;
        public NumberParameter(String name,float value) {
            super(name, "number");
            Value = value;
        }
        public void setValue(float value){Value = value;}
        public float getValue(){return Value;}
        public JsonObject getAsJson(){
            JsonObject jsonObject = super.getAsJson();
            jsonObject.addProperty("value",String.valueOf(Value));
            return jsonObject;
        }
    }
    public static class Event{
        String EventName;
        ArrayList<Parameter> EventParams;

        public Event(String name , ArrayList<Parameter> Params){
            EventName = name;
            EventParams = new ArrayList<>();
            if(!Params.isEmpty())
                EventParams.addAll(Params);
        }
        public String getEventName(){return EventName;}
        public ArrayList<Parameter> getEventParams(){return EventParams;}
        public void AddParameter(Parameter parameter){EventParams.add(parameter);}
        public void RemoveParam(Parameter parameter){EventParams.remove(parameter);}
        public JsonObject getAsJson(){
            JsonObject jsonObject = new JsonObject();
            JSONArray ParamsArray = new JSONArray();
            jsonObject.addProperty("name",EventName);
            if (!EventParams.isEmpty()){
                for (Parameter param : EventParams){
                    ParamsArray.put(param.getAsJson());
                }

            }
            jsonObject.addProperty("parameters",ParamsArray.toString());
            return jsonObject;
        }
    }

    public static class GameticException extends Exception{
        public GameticException(){super();}
        public GameticException(String message){super(message);}
        public GameticException(Throwable cause){super(cause);}
        public GameticException(String message, Throwable cause){super(message,cause);}

    }

    public static class Project{
        public String project_name;
        public String developer_id;
    }
}
