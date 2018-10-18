package com.example.pc.fractal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by FelipeCruz on 5/18/2018.
 */

public class FractalView{


    public void fractal(Context context, JSONObject structure, Object parent)
    {

        try{

            Iterator<?> keys = structure.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();

                if ( structure.get(key) instanceof JSONObject ) {

                    JSONObject jsonProperties = (JSONObject) structure.get(key);
                    //checking posibles controls values
                    switch(key){
                        case "NestedScrollView":

                            NestedScrollView nestedScroll = CustomNestedScrollView(context, jsonProperties);

                            //if has many childs views
                            if(jsonProperties.has("childs")){

                                JSONArray childs = jsonProperties.getJSONArray("childs");
                                //iterating the childs
                                for(int c=0 ; c < childs.length() ; c++){
                                    fractal(context, childs.getJSONObject(c), nestedScroll);
                                }
                            }
                            //if has just one child view
                            else if(jsonProperties.has("child"))
                                fractal(context, jsonProperties.getJSONObject("child"), nestedScroll);


                            addToParent(parent, nestedScroll);

                            break;
                        case "LinearLayout":

                            LinearLayout layoutView = new LinearLayout(context);
                            if(jsonProperties.has("type")){
                                if(jsonProperties.getString("type").equals("linear")){
                                    layoutView = CustomLinearLayout(context, jsonProperties);
                                }
                            }
                            if(jsonProperties.has("childs")){

                                JSONArray childs = jsonProperties.getJSONArray("childs");
                                //iterating the childs
                                for(int c=0 ; c < childs.length() ; c++){
                                    fractal(context, childs.getJSONObject(c), layoutView);
                                }
                            }else if(jsonProperties.has("child"))
                                fractal(context, jsonProperties.getJSONObject("child"), layoutView);


                            addToParent(parent, layoutView);

                            break;
                        case "label":
                            //creating and setting the label
                            addToParent(parent, CustomLabel(context, (JSONObject)structure.get(key)));
                            break;
                        case "text":
                            //creating and setting the text
                            addToParent(parent, CustomText(context, (JSONObject)structure.get(key)));
                            break;
                        case "image":
                            //creating and setting the text
                            addToParent(parent, CustomImage(context, (JSONObject)structure.get(key)));
                            break;
                    }
                }
            }
        }
        catch(JSONException ex){
            Toast.makeText(context, "Error 11", Toast.LENGTH_LONG).show();
        }
    }
    public void addToParent(Object parent, Object children){

        if(parent instanceof NestedScrollView){
            ((NestedScrollView) parent).addView((View) children);
        }
        else if(parent instanceof LinearLayout){
            ((LinearLayout) parent).addView((View) children);
        }
    }

    public LinearLayout CustomLinearLayout(Context context, JSONObject control){

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        );

        try{
            if(control.has("layout")) {
                LinearLayout.LayoutParams params = getLayout(control.getString("layout"));
                layout.setLayoutParams(params);
            }

            if(control.has("padding")){
                    int size = Integer.parseInt(control.getString("padding"));
                    layout.setPadding(size,size,size,size);
            }

        }catch(JSONException ex){

        }

        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    public NestedScrollView CustomNestedScrollView(Context context, JSONObject control){

        NestedScrollView layout = new NestedScrollView(context);
        layout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        );

        try{
            if(control.has("layout")) {
                LinearLayout.LayoutParams params = getLayout(control.getString("layout"));
                layout.setLayoutParams(params);
            }

            if(control.has("padding")){
                int size = Integer.parseInt(control.getString("padding"));
                layout.setPadding(size,size,size,size);
            }

        }catch(JSONException ex){

        }
        return layout;
    }

    public TextView CustomLabel(Context context, JSONObject control){

        TextView textView = new TextView(context);
        if(control.has("value")){
            try{
                textView.setText(control.getString("value"));
            }catch(JSONException ex){

            }
        }
        else{
            textView.setText("label");
        }
        if(control.has("font-size")){
            try{
                int size = Integer.parseInt(control.getString("font-size"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
            }catch(JSONException ex){

            }
        }
        if(control.has("padding")){
            try{
                int size = Integer.parseInt(control.getString("padding"));
                textView.setPadding(size,size,size,size);
            }catch(JSONException ex){

            }
        }


        return textView;
    }

    public EditText CustomText(Context context, JSONObject control){

        EditText myText = new EditText(context); // Pass it an Activity or Context

        try
        {
            if(control.has("layout")) {
                    LinearLayout.LayoutParams params = getLayout(control.getString("layout"));
                    myText.setLayoutParams(params);
            }
            if(control.has("value")) {
                myText.setText(control.getString("value"));
            }

        }catch(JSONException ex){

        }
        return myText;
    }

    public ImageView CustomImage(Context context, JSONObject control){

        ImageView imageView = new ImageView(context);

        try
        {
            LinearLayout.LayoutParams params = getLayout(control.getString("layout"));
            imageView.setLayoutParams(params);

            if(control.has("src")){
                imageView.setImageDrawable(getDrawable(context, control.getString("src")));
            }

        }catch(JSONException ex){

        }
        return imageView;
    }

    //get image src
    public static final Drawable getDrawable(Context context, String resource) {
        Resources res = context.getResources();
        int resID = res.getIdentifier(resource , "png", "drawable");
        Drawable drawable = res.getDrawable(resID);
        return drawable;
    }

    public LinearLayout.LayoutParams getLayout(String layout){

        switch (layout) {
            case "xy": return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            case "Xy": return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            case "xY": return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

            case "XY": return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            default: return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
}
