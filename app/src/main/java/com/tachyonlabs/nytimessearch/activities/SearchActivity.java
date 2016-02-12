package com.tachyonlabs.nytimessearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tachyonlabs.nytimessearch.R;
import com.tachyonlabs.nytimessearch.adapters.ArticleArrayAdapter;
import com.tachyonlabs.nytimessearch.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    private final int REQUEST_CODE = 20;
    String beginDate;
    String endDate;
    String sortOrder;
    String newsDeskValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
        getSettings();
    }

    public void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass the article in to the intent
                intent.putExtra("article", article);
                // launch the activity
                startActivity(intent);
            }
        });
    }

    public void getSettings() {
        SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        beginDate = mSettings.getString("beginDate", getResources().getString(R.string.all_dates));
        endDate = mSettings.getString("endDate", getResources().getString(R.string.today));
        sortOrder = mSettings.getString("sortOrder", "newest");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final ImageView ivSplash = (ImageView) findViewById(R.id.ivSplash);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                // clear previous search results
                adapter.clear();

                AsyncHttpClient client = new AsyncHttpClient();
                String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
                RequestParams params = new RequestParams();
                params.put("api-key", "693d518cc15a1c220a4a1490ef49bdbc:10:23377");
//                if (!beginDate.equals("none")) {
//                    params.put("begin_date", beginDate);
//                }
                params.put("sort", sortOrder);
                params.put("page", 0);
                params.put("q", query);

                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //super.onSuccess(statusCode, headers, response);
                        JSONArray articleJsonResults = null;

                        try {
                            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                            adapter.addAll(Article.fromJSONArray(articleJsonResults));
                            // once there are search results, remove the splash and show the grid
                            gvResults.setVisibility(View.VISIBLE);
                            ivSplash.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
            intent.putExtra("beginDate", beginDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("sortOrder", sortOrder);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            sortOrder = data.getExtras().getString("sortOrder");
            beginDate = data.getExtras().getString("beginDate");
            endDate = data.getExtras().getString("endDate");
            SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("sortOrder", sortOrder);
            editor.putString("beginDate", beginDate);
            editor.putString("endDate", endDate);
            editor.commit();
        }
    }
}
