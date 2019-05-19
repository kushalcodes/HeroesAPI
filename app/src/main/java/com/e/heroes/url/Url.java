package com.e.heroes.url;

import retrofit2.Retrofit;

public class Url {

    public static String BASE_URl = "http://10.0.2.2:3000/";

    public static Retrofit getRetrofitInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URl)
                .build();

        return retrofit;
    }

}
