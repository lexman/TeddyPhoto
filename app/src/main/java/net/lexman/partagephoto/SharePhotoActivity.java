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
        PicToUploadAdapter adapter = new PicToUploadAdapter();
        rv.setAdapter(adapter);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        ArrayList<Uri> imageUris;
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

