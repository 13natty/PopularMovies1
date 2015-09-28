package com.nattysoft.popularmovies;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;



    // JSON Node names
    private static final String TAG_RESULTS = "results";
    private static final String TAG_ADULT = "adult";
    private static final String TAG_BACKDROP_PATH = "backdrop_path";
    private static final String TAG_GENRE_IDS = "genre_ids";
    private static final String TAG_ID = "id";
    private static final String TAG_ORIGINAL_LANGUAGE = "original_language";
    private static final String TAG_ORIGINAL_TITLE = "original_title";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_POPULARITY = "popularity";
    private static final String TAG_TITLE = "title";
    private static final String TAG_VIDEO = "video";
    private static final String TAG_VOTE_AVERAGE = "vote_average";
    private static final String TAG_VOTE_COUNT = "vote_count";

    // movies JSONArray
    JSONArray movies = null;

    ArrayList<ImageItem> moviesList;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private String poster_path;
    private String title;
    private String adult;
    private String backdrop_path;
    private String genre_ids;
    private String mov_id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String popularity;
    private String video;
    private String vote_average;
    private String vote_count;
    private String key;

    // URL to get themoviedb JSON
    private static String url;
    private static int sort = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getString(R.string.movie_api_key);

        url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+key;
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<ImageItem>();

        // Calling async task to get json
        new GetMovies().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_sort:
                final CharSequence[] items = {
                        "Sort order by Most Popular", "Sort order by Highest-Rated"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Change Sort order");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item){
                            case 0:
                                if(sort!=0) {
                                    url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+key;
                                    sort = 0;
                                    moviesList = new ArrayList<ImageItem>();
                                    new GetMovies().execute();
                                }else{
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Most Popular sorted")
                                            .setMessage("Your movies are already sorted according to most popular")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                                break;
                            case 1:
                                if(sort!=1) {
                                    url = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+key;
                                    sort = 1;
                                    moviesList = new ArrayList<ImageItem>();
                                    new GetMovies().execute();
                                }else{
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Highest-Rated sorted")
                                            .setMessage("Your movies are already sorted according to highest-rated")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMovies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.d("Response: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    movies = jsonObj.getJSONArray(TAG_RESULTS);

                    // looping through All Movies
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject c = movies.getJSONObject(i);

                        poster_path = c.getString(TAG_POSTER_PATH);
                        title = c.getString(TAG_TITLE);
                        adult = c.getString(TAG_ADULT);
                        backdrop_path = c.getString(TAG_BACKDROP_PATH);
                        genre_ids = c.getString(TAG_GENRE_IDS);
                        mov_id = c.getString(TAG_ID);
                        original_language = c.getString(TAG_ORIGINAL_LANGUAGE);
                        original_title = c.getString(TAG_ORIGINAL_TITLE);
                        overview = c.getString(TAG_OVERVIEW);
                        release_date = c.getString(TAG_RELEASE_DATE);
                        popularity = c.getString(TAG_POPULARITY);
                        video = c.getString(TAG_VIDEO);
                        vote_average = c.getString(TAG_VOTE_AVERAGE);
                        vote_count = c.getString(TAG_VOTE_COUNT);

                        ImageItem movie = new ImageItem(poster_path, title);


                        // adding a movie to movie list
                        moviesList.add(movie);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            gridAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item_layout, moviesList);
            gridView = (GridView) findViewById(R.id.gridView);
            gridView.setAdapter(gridAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //get details of the clicked item
                    JSONObject c = null;
                    try {
                        c = movies.getJSONObject(position);

                        poster_path = c.getString(TAG_POSTER_PATH);
                        title = c.getString(TAG_TITLE);
                        adult = c.getString(TAG_ADULT);
                        backdrop_path = c.getString(TAG_BACKDROP_PATH);
                        genre_ids = c.getString(TAG_GENRE_IDS);
                        mov_id = c.getString(TAG_ID);
                        original_language = c.getString(TAG_ORIGINAL_LANGUAGE);
                        original_title = c.getString(TAG_ORIGINAL_TITLE);
                        overview = c.getString(TAG_OVERVIEW);
                        release_date = c.getString(TAG_RELEASE_DATE);
                        popularity = c.getString(TAG_POPULARITY);
                        video = c.getString(TAG_VIDEO);
                        vote_average = c.getString(TAG_VOTE_AVERAGE);
                        vote_count = c.getString(TAG_VOTE_COUNT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                    //Create intent
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("imageURL", item.getImageURL());
                    intent.putExtra("adult", adult);
                    intent.putExtra("backdrop_path", backdrop_path);
                    intent.putExtra("genre_ids", genre_ids);
                    intent.putExtra("id", mov_id);
                    intent.putExtra("original_language", original_language);
                    intent.putExtra("original_title", original_title);
                    intent.putExtra("overview", overview);
                    intent.putExtra("release_date", release_date);
                    intent.putExtra("popularity", popularity);
                    intent.putExtra("video", video);
                    intent.putExtra("vote_average", vote_average);
                    intent.putExtra("vote_count", vote_count);

                    //Start details activity
                    startActivity(intent);
                }
            });

        }
    }
}