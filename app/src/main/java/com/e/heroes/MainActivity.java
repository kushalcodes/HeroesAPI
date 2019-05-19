package com.e.heroes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.heroes.api.HeroesApi;
import com.e.heroes.url.Url;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ImageView theImage;
    Button btnSave;
    EditText heroName, heroDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        theImage = findViewById(R.id.theImage);
        btnSave = findViewById(R.id.btnSave);
        heroName = findViewById(R.id.heroName);
        heroDescription = findViewById(R.id.heroDesc);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });

    }


    private void Save(){
        String name = heroName.getText().toString();
        String desc = heroDescription.getText().toString();

        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("desc",desc);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HeroesApi heroesApi = retrofit.create(HeroesApi.class);

        Call<Void> heroCall = heroesApi.addHero(map);

        heroCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if ( !response.isSuccessful() ){
                    alert("Code: "+response.code());
                }
                alert("Added Okay :)");

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                alert("Error");
            }
        });

    }

    private void alert(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
        return;
    }
}