package com.grusoft.hitrack;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SendToServer.AsyncResponse {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int a = 0;
        setContentView(R.layout.activity_main);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.people_1);

        try {
            JSONObject postData = new JSONObject();
            postData.put("name", "cys");
            new SendToServer(this).execute(bmp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    protected void showPic(Bitmap bmp){
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        //imageView.setImageURI(imageUri);
        imageView.setImageBitmap(bmp);
        //imageView.setImageResource(R.drawable.people_1);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    @Override
    public void processFinish(Bitmap output) {
        showPic(output);
    }
}
