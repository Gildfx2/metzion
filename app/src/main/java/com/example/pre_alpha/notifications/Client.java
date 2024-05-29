package com.example.pre_alpha.notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    static Retrofit retrofit = null;
    public static Retrofit getClient(String url){
        //takes a URL address and returns a Retrofit object. If there is no existing instance of Retrofit, the function builds and returns a new one using Retrofit.Builder.
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
