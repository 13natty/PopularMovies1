package com.nattysoft.popularmovies;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by F3838284 on 2015/09/03.
 */

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private ProgressDialog pDialog;
    private Target loadTarget;
    private boolean favorite = false;
    private String movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("title"));

        TextView year = (TextView) findViewById(R.id.year);
        year.setText(getIntent().getStringExtra("release_date").substring(0, 4));

        movie_id = getIntent().getStringExtra("id");

        //TextView adultTextView = (TextView) findViewById(R.id.adult);
        //adultTextView.setText("Adult :"+getIntent().getStringExtra("adult"));

        //TextView original_languageTextView = (TextView) findViewById(R.id.original_language);
        //original_languageTextView.setText("Language :"+getIntent().getStringExtra("original_language"));

        //TextView original_titleTextView = (TextView) findViewById(R.id.original_title);
        //original_titleTextView.setText("Original Title :"+getIntent().getStringExtra("original_title"));

        //TextView release_dateTextView = (TextView) findViewById(R.id.release_date);
        //release_dateTextView.setText("Release Date :"+getIntent().getStringExtra("release_date"));

        //TextView popularityTextView = (TextView) findViewById(R.id.popularity);
        //popularityTextView.setText("Popularity :"+getIntent().getStringExtra("popularity"));

        //TextView vote_averageTextView = (TextView) findViewById(R.id.vote_average);
        //vote_averageTextView.setText("Average :"+getIntent().getStringExtra("vote_average"));

        //TextView vote_countTextView = (TextView) findViewById(R.id.vote_count);
        //vote_countTextView.setText("Votes :"+getIntent().getStringExtra("vote_count"));

        TextView overviewTextView = (TextView) findViewById(R.id.overview);
        overviewTextView.setText("" + getIntent().getStringExtra("overview"));

        try {
            //new GetTrailers().execute();
            ArrayList<Object> passing = new ArrayList<Object>();
            passing.add("http://api.themoviedb.org/3/movie/"+movie_id+"/videos?api_key="+MainActivity.KEY);
            passing.add("Loading trailers ...");
            passing.add(DetailsActivity.this);
            AsyncTask task = new httpGet().execute(passing);

            Object taskResults = task.get();
            Log.d("taskResults ","taskResults >>>>>>>>>>>>>>>>>>>>.. "+taskResults);
        }catch (Exception e){
            Log.d("Exception ","Exception >>>>>>>>>>>>>>>>>>>>.. "+e.getMessage());
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            loadBitmap("http://image.tmdb.org/t/p/w500/" + getIntent().getStringExtra("backdrop_path"));
        }else {
            loadBitmap("http://image.tmdb.org/t/p/w780/" + getIntent().getStringExtra("backdrop_path"));
        }
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            loadBitmap("http://image.tmdb.org/t/p/original/" + getIntent().getStringExtra("backdrop_path"));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            loadBitmap("http://image.tmdb.org/t/p/w780/" + getIntent().getStringExtra("backdrop_path"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadBitmap(String url) {

        if (loadTarget == null) loadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                setBackgroundImage(bitmap);
                if(pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "onBitmapFailed");
                if(pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG,"onPrepareLoad");
                pDialog = new ProgressDialog(DetailsActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
            }
        };

        Picasso.with(this).load(url).into(loadTarget);
    }

    private void setBackgroundImage(Bitmap bitmap) {
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        favorite = ((ToggleButton) view).isChecked();
    }

}