package net.lexman.partagephoto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.ByteString;

public class SharePhotoActivity extends AppCompatActivity {

    protected  ArrayList<Uri> imageUris;
    protected String mediaTypeSt;
    //private String POST_PICTURE_URL = "http://partagephoto.local:8081/albums/album1/newphoto";
    private String POST_PICTURE_URL = "http://partagephoto.local:8081/albums/album1/";
    private final OkHttpClient client = new OkHttpClient();
    protected PicToUploadAdapter adapter;

    private final int MB = 1024 * 1024;
    private final int MAX_UPLOAD_SIZE = 12 * MB;


    public class UploadTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute(){}

        private final OkHttpClient client = new OkHttpClient();
        private String POST_URL = "http://partagephoto.local:8081/albums/album1/";

        public void postImage(Uri uri) throws  IOException {
            Log.d("postImage", uri.toString());
            RequestBody body = RequestBody.create(
                    MediaType.parse(mediaTypeSt),
                    readImage(uri));
            Request request = new Request.Builder()
                    .url(POST_PICTURE_URL)
                    .header("Authorization", "Basic " + "xxx")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.d("postImage", "FAILURE");
                Log.d("postImage", response.body().string());
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            Log.d("DEBUG", responseBody);
        }

        protected Void doInBackground(Void... params) {
            int i = 0;
            while (i < imageUris.size()) {
                try {
                    Uri uri = imageUris.get(0);
                    Log.d("postImages", uri.toString());
                    postImage(uri);
                    imageUris.remove(i);
                    // adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    // skip this picture if upload failed,
                    // in case the error is due to the image
                    i++;
                    e.printStackTrace();
                }
            }
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
        setContentView(R.layout.activity_share_photo);
        RecyclerView rv = (RecyclerView)findViewById(R.id.rvUploader);
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new PicToUploadAdapter();
        rv.setAdapter(adapter);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        mediaTypeSt = type;
        if (Intent.ACTION_SEND.equals(action) && type.startsWith("image/")) {
            imageUris = new ArrayList<>();
            imageUris.add((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
            adapter.setList(imageUris);
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type.startsWith("image/")) {
            imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            adapter.setList(imageUris);
        }
    }

    public void onShareClick(View v) {
        Toast.makeText(this.getBaseContext(), "Click", Toast.LENGTH_LONG);
        Log.d("onShareClick", "clik");
        UploadTask dlt = new UploadTask();
        dlt.execute();
    }

    protected byte[] readImage(Uri uri) throws IOException {
        InputStream fis = getContentResolver().openInputStream(uri);
        byte[] buff = new byte[MAX_UPLOAD_SIZE];
        int nbReads = fis.read(buff);
        if (nbReads == MAX_UPLOAD_SIZE) {
            String msg = "Picture " + uri.toString() + " can't be uploaded because it weights more than " + MAX_UPLOAD_SIZE  + " MB";
            throw new IOException(msg);
        }
        return buff;
    }

}


class PicToUploadAdapter extends RecyclerView.Adapter<PicToUploadAdapter.PicToUploadHolder> {

    private ArrayList<Uri> picsToUploadUris = new ArrayList();

    @Override
    public int getItemCount() {
        return picsToUploadUris.size();
    }

    @Override
    public PicToUploadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pic_to_upload_layout, parent, false);
        return new PicToUploadHolder(view);
    }

    @Override
    public void onBindViewHolder(PicToUploadHolder holder, int position) {
        Uri picToUpload = picsToUploadUris.get(position);
        holder.display(picToUpload);
    }

    public void setList(ArrayList<Uri> picsToUploadUris) {
        this.picsToUploadUris = picsToUploadUris;
    }

    public class PicToUploadHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PicToUploadHolder(final View itemView) {
            super(itemView);
            imageView = ((ImageView) itemView.findViewById(R.id.imageView));
        }

        public void display(Uri picToUpload) {
            imageView.setImageURI(picToUpload);
        }
    }
}

