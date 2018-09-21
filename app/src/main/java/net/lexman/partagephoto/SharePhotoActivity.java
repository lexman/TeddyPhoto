package net.lexman.partagephoto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class SharePhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_photo);
        RecyclerView rv = (RecyclerView)findViewById(R.id.rvUploader);
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        ArrayList<Uri> imageUris;
        if (Intent.ACTION_SEND.equals(action) && type.startsWith("image/")) {
            imageUris = new ArrayList<>();
            imageUris.add((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
            handleImages(imageUris);
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type.startsWith("image/")) {
            imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            handleImages(imageUris);
        }
    }

    void handleImages(ArrayList<Uri> imageUris) {
        Log.d("handlerReceiveImage", imageUris.toString());
    }


}

