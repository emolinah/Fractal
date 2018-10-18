package com.example.pc.fractal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText txt_usuario;
    EditText txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.btn_login);
        txt_usuario   = (EditText) findViewById(R.id.txt_usuario);
        txt_password   = (EditText) findViewById(R.id.txt_password);

        login.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {

            if(txt_usuario.getText().toString() != "" &&
                    txt_password.getText().toString() != "") {

                RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());

                String url = "http://45.7.228.17:3000";
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        //setting json response
                        home.putExtra("json", response);
                        startActivity(home);
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Log.d("error", error.toString());
                        Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_LONG).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("device", "android");
                        MyData.put("nombre", txt_usuario.getText().toString());
                        MyData.put("password", txt_password.getText().toString());
                        return MyData;
                    }
                };

                MyRequestQueue.add(MyStringRequest);

            }
            else{
                Toast.makeText(LoginActivity.this, "Ingrese las credenciales", Toast.LENGTH_LONG).show();

            }
            }

        });
    }

    private boolean createFile(Context context, String fileName, String jsonString) {
        String FILENAME = "storage.json";
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }
}
