package com.example.pc.fractal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

      LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //controls
        setContentView(R.layout.activity_home);
        main = (LinearLayout) findViewById(R.id.layout_main);


        //params
        Intent myIntent = getIntent(); // gets the previously created intent
        String serverJSON = myIntent.getStringExtra("json");

        //JSON
        writeJSON(serverJSON, getApplicationContext());

        //DATA
        init();

        //UI
        ////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Buscar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void init(){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://45.7.228.17:3000/execute?clase=Instruccion&controllerAction=index";
        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ReloadView(response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("error", error.toString());
            }
        });

        MyRequestQueue.add(MyStringRequest);
    }

    private void ReloadView(String response){
        try
        {
            //JSON
            JSONArray resultJson = new JSONArray(response);

            //GETTING THE VIEWS
            JSONArray views = getViews(getApplicationContext(), "views.json");

            //CONFIG
            String configStr = getJson(getApplicationContext(), "config.json");
            JSONObject config = new JSONObject(configStr);

            FractalView Fractal = new FractalView();


            //getting the view
            JSONObject viewStructure = new JSONObject();
            for(int i = 0 ; i < views.length(); i++) {

                if (views.getJSONObject(i).has("access")) {
                    viewStructure = views.getJSONObject(i);
                }
            }

            //json control
            JSONObject accessView = viewStructure.getJSONObject("access");
            JSONArray childsControls = accessView
                    .getJSONObject("LinearLayout")
                    .getJSONArray("childs");

              List<String> controllers = new ArrayList<String>();



            //iterate the json results
            for(int o = 0 ; o < resultJson.length(); o++){

                  JSONObject access = resultJson.getJSONObject(o);
                  controllers.add(access.getString("controlador"));
                  for(int c = 0; c < childsControls.length(); c++){
                    JSONObject actualControl = childsControls.getJSONObject(c);

                    Iterator<?> keys = actualControl.keys();
                    while( keys.hasNext() ) {
                        String key = (String) keys.next();

                        //set the image
                        if (key.equals("image")){
                            String controlador = access.getString("controlador");
                            if(config.has(controlador) && config.getJSONObject(controlador).has("_image")){
                                  String src = config.getJSONObject(controlador).getString("_image");
                                  actualControl.getJSONObject(key).put("src", src);
                            }
                        }
                        if (key.equals("label")){
                            String titulo = access.getString("titulo");
                            actualControl.getJSONObject(key).put("value", titulo);
                        }
                    }
                  }
                  //drawing the access
                  Fractal.fractal(getApplicationContext(), accessView, main);
            }

//              for(int count = 0; count < main.getChildCount(); count ++) {
//                    LinearLayout layout = (LinearLayout) main.getChildAt(count);
//                    layout.setOnClickListener(new View.OnClickListener() {
//
//                          //controllers[count]
//                    });
//              }


        }
        catch(JSONException ex){
            Toast.makeText(HomeActivity.this, "Error al crear los accesos", Toast.LENGTH_LONG).show();
        }
    }

    private void writeJSON(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public JSONArray getViews(Context context, String file) {
        JSONArray result = null;
        try {
            InputStream is = context.getAssets().open(file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String export = new String(buffer, "UTF-8");

            result = new JSONArray(export);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException jsonEx){
            jsonEx.printStackTrace();
            return null;
        }
        return result;

    }

    private String getJson(Context context, String file) {

        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
