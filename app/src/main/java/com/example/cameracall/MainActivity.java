package com.example.cameracall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button b;
    ImageView i;
    Bitmap bitmap;
    String name = "image";
    public String TAG = "MainActivity";
    public String httpRequest = "httpRequest";

    final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = (Button) findViewById(R.id.button);

        i = (ImageView) findViewById(R.id.imageView);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");

        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        String loginPostUrl = "http://eatsmartai.com:5000/predict";
        OkHttpLoginHandler loginBgTask = new OkHttpLoginHandler();
       // loginBgTask.execute(loginPostUrl);



         class OkHttpLoginHandler extends AsyncTask<String, Void, Response> {

            OkHttpClient client = new OkHttpClient();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //String nameField = "image";
            /*emailTxt = findViewById(R.id.emailEditText);
            passwordTxt = findViewById(R.id.pwdEditText);
            this.emailId = emailTxt.getText().toString();
            this.password = passwordTxt.getText().toString();
            Log.v(TAG, "pre execution success! userid "+this.emailId+" password "+password);
            // TODO create a progress bar for logging in*/
            }

            @Override
            protected Response doInBackground(String... loginPostUrl) {
                // TODO handle email or pwd null field

                // add signIn request body to JSON object from user fields
                JSONObject loginJsonBody = new JSONObject();
                try {
                    // loginJsonBody.put("image", this.emailId);
                    loginJsonBody.put(name, ConvertImage);
                } catch (JSONException e) {
                    // TODO handle json object creation error
                }

                // create a request body with the json object created above
                RequestBody body = RequestBody.create(JSON, loginJsonBody.toString());

                // create a request by building a request with url and request body
                Request request = new Request.Builder()
                        .url(loginPostUrl[0])
                        .post(body)
                        .build();

                try {
                    // try to post with a new call
                    Response response = client.newCall(request).execute();
                    return response;
                } catch (Exception e) {
                    // TODO handle network error Failed to connect to eatsmart
                    // exception run when no internet or reaching server error
                    // Unable to resolve host "eatsmartai.com": No address associated with hostname
                    e.printStackTrace();
                    Log.v(TAG, String.valueOf(e));
                    // go back to login activity

                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Response loginRespString) {
                super.onPostExecute(loginRespString);

                try {
                    Log.v(TAG, loginRespString.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // response can take 3 states 1. login success 2. wrong id or pwd 3. no internet connection
                // handle 3 states
                Response test = loginRespString.networkResponse();
                if (test.code() == 200) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
                if (test.code() == 500) {
                    // go back to Login activity

                }
                // TODO create failed login logic
                // TODO update progress bar after successful login

            }


        }
    }

    private class OkHttpLoginHandler {
    }
}