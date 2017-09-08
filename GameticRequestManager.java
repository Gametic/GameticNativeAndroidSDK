package com.gametic.sdk.gametic;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by ali on 8/4/17.
 */

public class GameticRequestManager {
    String API_BaseURL;
    int API_Port ;
    public static  JsonObject responseJson;
    public GameticRequestManager(){
        API_BaseURL = "http://api.gametic.ir/";
        API_Port = 80;
        responseJson = new JsonObject();
    }

    public void SignUp(GameticDataStructures.Project project){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);  // <-- this is the important line!
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BaseURL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        GameticAPI gameticAPI = retrofit.create(GameticAPI.class);

         Call<JsonObject> call = gameticAPI.SignUp(project);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("Signup",String.valueOf(response.code()));
                    Log.d("signUp",call.request().toString());
                    if (response.code() == 200) {
                        Log.d("Signup", "User ID:" + response.body());
                        GameticSDKManager.instance.NewUserResponse(response.body());
                    } else {
                        Log.e("SignUp", "Error code : " + String.valueOf(response.code()));
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("SignUp", "Failed with error: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        };
    public JsonObject SendRequest(String Url, JsonObject UserJson ) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BaseURL).addConverterFactory(GsonConverterFactory.create()).build();
        GameticAPI gameticAPI = retrofit.create(GameticAPI.class);
        final JsonObject[] responseBody = new JsonObject[1];
        final String[] resBody = new String[1];

        //sending request
//        do {
        gameticAPI.Post(Url, UserJson).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Post",String.valueOf(response.code()));
                if (response.code() == 200) {
                    responseBody[0] = response.body();
//                    resBody[0] = response.body().getAsString();
                    resBody[0] = response.toString();
                } else {
                    Log.e("POST", "Error code : " + String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SignUp", "Failed with error: " + t.getMessage());
                t.printStackTrace();
            }
        });
//        Log.e("SignUp", "Failed with error: " + resBody[0]);

//    }while (tryAgain[0]);
        return  responseBody[0];
    }


    public interface GameticAPI{

        @POST
        Call<JsonObject> Post(@Url String url, @Body JsonObject jsonObject);
        @POST("analytic/signup")
        Call<JsonObject> SignUp(@Body GameticDataStructures.Project project);
    }
}
