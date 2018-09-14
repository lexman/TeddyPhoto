package net.lexman.partagephoto;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PicsListActivity extends AppCompatActivity {


    // private Handler handler = new Handler();

    private Runnable downloader = new Runnable() {

        private final OkHttpClient client = new OkHttpClient();
        private String DOWNLOAD_URL = "http://partagephoto.local/albums/album1/";

        protected ArrayList<Pic> json2Pics(Response response) throws Exception {
            ArrayList<Pic> result = new ArrayList<>();
            JSONArray values = new JSONArray(response.body().string());
            Log.d("json2Pics", "" + response.body().string());
            for (int i = 0; i < values.length(); i++) {

                JSONObject picJson = values.getJSONObject(i);

                Pic pic = new Pic(
                        picJson.getString("url"),
                        picJson.getString("thumb_url"),
                        picJson.getDouble("ts")
                );
                result.add(pic);
            }
            Log.d("json2Pics", "" + result);

            return result;
        }

        protected void loadAlbum() {
            Request request = new Request.Builder()
                    .url(DOWNLOAD_URL)
                    .header("Authorization", "Basic " + "XXXX")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }
                json2Pics(response);
                JSONArray reader = new JSONArray(response.body().string());
            } catch (Exception e) {
                Log.d("loadAlbum", "Failed to load album :");
                Log.d("loadAlbum", e.toString());
            }
        }

        @Override
        public void run() {
            loadAlbum();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv_pics);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(new PicsAdapter());
        downloader.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pics_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class Pic {

    private String name;
    private String url;
    private String thumb_url;
    private Double ts;


    public Pic(String name) {
        this.name = name;
    }

    public Pic(String url, String thumb_url, Double ts) {
        this.url = url;
        this.thumb_url = thumb_url;
        this.ts = ts;
    }

    public String getName() {
        return name;
    }
}

class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicViewHolder> {

    // private LayoutInflater inflater;

    private final List<Pic> pics = Arrays.asList(
            new Pic("tst"),
            new Pic("tst1"),
            new Pic("tst2"),
            new Pic("tst3"),
            new Pic("tst4"),
            new Pic("tst5"),
            new Pic("tst6"),
            new Pic("tst7"),
            new Pic("tst8"),
            new Pic("tst9"),
            new Pic("tst10"),
            new Pic("tst11"),
            new Pic("tst12"),
            new Pic("tst13")
    );

    @Override
    public int getItemCount() {
        return pics.size();
    }

    @Override
    public PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pic_layout, parent, false);
        return new PicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PicViewHolder holder, int position) {
        Pic pic = pics.get(position);
        holder.display(pic);
    }

/*
    PicsAdapter(Context _context, int resource, Pic[] pics) {
        super(_context, resource, pics);
        this.inflater = LayoutInflater.from(_context);
        this.pics = pics;
    }
*/


    private void fillPicView(View hebView, Pic pic){
        /*
        TextView viewName = (TextView) hebView.findViewById(R.id.hebergementItemViewName);
        viewName.setText(hebergement.getName());
        RatingBar viewNote = (RatingBar) hebView.findViewById(R.id.hebergementItemRatingBar);
        viewNote.setRating(hebergement.getNote());
        setPictoVisibility(hebView, R.id.imageViewMachineALaver, hebergement.isMachineALaver());
        setPictoVisibility(hebView, R.id.imageViewWifi, hebergement.isWifi());
        setPictoVisibility(hebView, R.id.imageViewRestaurant, hebergement.isRestaurant());
        setPictoVisibility(hebView, R.id.imageViewPetitDejeuner, hebergement.isPetitDejeuner());
        setPictoVisibility(hebView, R.id.imageViewCuisine, hebergement.isCuisine());
        setPictoVisibility(hebView, R.id.imageViewAccesHandicape, hebergement.isAccessHandicape());
        setPictoVisibility(hebView, R.id.imageViewChequesVacances, hebergement.isChequesVacances());
        setPictoVisibility(hebView, R.id.imageViewAnimaux, hebergement.isAnimaux());
        */
    }



    public class PicViewHolder extends RecyclerView.ViewHolder {

        private final TextView picTextView;

        private Pic pic;

        public PicViewHolder(final View itemView) {
            super(itemView);

            picTextView = ((TextView) itemView.findViewById(R.id.picTextView));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Test")
                            .setMessage(pic.getName())
                            .show();
                }
            });
        }

        public void display(Pic pic) {
            this.pic = pic;
            picTextView.setText(pic.getName());
        }
    }

}
