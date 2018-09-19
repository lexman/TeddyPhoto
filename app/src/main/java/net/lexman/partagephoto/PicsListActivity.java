package net.lexman.partagephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//LD_PRELOAD='/usr/lib/x86_64-linux-gnu/libstdc++.so.6' ~/Android/Sdk/tools/emulator -netdelay none -netspeed full -avd Nexus_4_API_21

public class PicsListActivity extends AppCompatActivity {

    protected PicsAdapter adapter;

    public class DownloadTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute(){}

        private final OkHttpClient client = new OkHttpClient();
        private String DOWNLOAD_URL = "http://partagephoto.local:8081/albums/album1/";

        protected ArrayList<Pic> json2Pics(Response response) throws Exception {
            ArrayList<Pic> result = new ArrayList<>();
            String body = response.body().string();
            JSONArray values = new JSONArray(body);
            Log.d("json2Pics", "" + body);
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
                ArrayList<Pic> pics = json2Pics(response);
                adapter.setList(pics);
            } catch (Exception e) {
                Log.d("loadAlbum", "Failed to load album :");
                Log.d("loadAlbum", e.toString());
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void... params) {
            Log.d("doInBackground", DOWNLOAD_URL);
            loadAlbum();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "onPostExecute");
            adapter.notifyDataSetChanged();
        }
    }

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
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new PicsAdapter();
        rv.setAdapter(adapter);
        DownloadTask dlt = new DownloadTask();
        dlt.execute();
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

    private String url;

    private String thumbUrl;
    private Double ts;

    public Pic(String url, String thumbUrl, Double ts) {
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.ts = ts;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

}

class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicViewHolder> {

    private ArrayList<Pic> pics = new ArrayList();
    private static ImageLoader imageLoader;

    private static ImageLoader createImageLoader(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ImageLoader result = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(15);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        return result;
    }

    private void initImageLoaderIfNeeded(Context context) {
        if (imageLoader == null) {
            imageLoader = createImageLoader(context);
        }
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    @Override
    public PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pic_layout, parent, false);
        initImageLoaderIfNeeded(context);

        return new PicViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PicViewHolder holder, int position) {
        Pic pic = pics.get(position);
        holder.display(pic);
    }

    public void setList(ArrayList<Pic> newPics) {
        this.pics = newPics;
    }

    public class PicViewHolder extends RecyclerView.ViewHolder {

        private Pic pic;
        private NetworkImageView picImage;

        public PicViewHolder(final View itemView) {
            super(itemView);

            picImage = ((NetworkImageView) itemView.findViewById(R.id.picImage));
            ImageView imgDownload = ((ImageView) itemView.findViewById(R.id.picImageDownload));

            imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Url")
                            .setMessage(pic.getUrl())
                            .show();
                }
            });
        }

        public void display(Pic pic) {
            this.pic = pic;
            String url = pic.getThumbUrl();
            picImage.setImageUrl(url,imageLoader);
            picImage.setErrorImageResId(R.drawable.ic_autorenew_black_24dp);
        }
    }



}
