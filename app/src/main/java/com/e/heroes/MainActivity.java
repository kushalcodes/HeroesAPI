package com.e.heroes;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.heroes.api.HeroesApi;
import com.e.heroes.model.ImageResponse;
import com.e.heroes.url.Url;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageView theImage;
    Button btnSave;
    EditText heroName, heroDescription;
    String imageName;
    String imagePath;

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

        theImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
            }
        });

    }

    private void BrowseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK ){
            if (data == null){
                alert("Please select an image!");
            }
        }

        Uri uri = data.getData();
        imagePath = getRealPathFromUri(uri);
        previewImage(imagePath);

    }

    private void previewImage(String imagePath) {

        File imgFile = new File(imagePath);
        if (imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            theImage.setImageBitmap(bitmap);
        }

    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;
    }

    private void StrictMode(){
        StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void Save(){
        SaveImageOnly();
        String name = heroName.getText().toString();
        String desc = heroDescription.getText().toString();
//
//        Map<String,String> map = new HashMap<>();
//        map.put("name",name);
//        map.put("desc",desc);
//        map.put("image",imageName);

        HeroesApi heroesApi = Url.getRetrofitInstance().create(HeroesApi.class);
        Call<Void> heroCall = heroesApi.addHero(name,desc,imageName);

        heroCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if ( !response.isSuccessful() ){
                    alert("Code: "+response.code());
                    return;
                }
                alert("Added Okay :)");
                clearField();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                alert("Herocall failure");
            }
        });

    }

    private void SaveImageOnly(){
        File file = new File(imagePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile",file.getName(),requestBody);

        HeroesApi heroesApi = Url.getRetrofitInstance().create(HeroesApi.class);
        Call<ImageResponse> imageResponseCall = heroesApi.uploadImage(body);

        StrictMode();

        try{

            Response<ImageResponse> imageResponseResponse = imageResponseCall.execute();
            imageName = imageResponseResponse.body().getFileName();

        }catch (Exception e){
            alert("Error Save Image Only");
            e.printStackTrace();
        }

    }

    private void alert(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
        return;
    }

    private void clearField(){
        heroName.setText("");
        heroDescription.setText("");
        theImage.setBackgroundResource(R.drawable.choose);
    }

}
