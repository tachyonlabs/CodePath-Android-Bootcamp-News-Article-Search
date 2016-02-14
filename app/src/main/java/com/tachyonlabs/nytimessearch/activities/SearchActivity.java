package com.tachyonlabs.nytimessearch.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tachyonlabs.nytimessearch.R;
import com.tachyonlabs.nytimessearch.adapters.ArticlesAdapter;
import com.tachyonlabs.nytimessearch.models.Article;
import com.tachyonlabs.nytimessearch.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    RecyclerView rvResults;
    ArrayList<Article> articles;
    ArticlesAdapter adapter;
    String searchQuery;
    private final int REQUEST_CODE = 20;
    String beginDate;
    String endDate;
    String sortOrder;
    boolean allNewsDeskValues;
    boolean newsDeskArtsSelected;
    boolean newsDeskFashionAndStyleSelected;
    boolean newsDeskSportsSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
        getSharedPreferencesSettings();
    }

    public void setupViews() {
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticlesAdapter(articles);
        rvResults.setAdapter(adapter);
        // Set layout manager to position the items
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        // following tip #10, stop article results from jumping around in the grid
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        // Attach the layout manager to the recycler view
        rvResults.setLayoutManager(gridLayoutManager);
        // hook up listener for grid click
        adapter.setOnItemClickListener(new ArticlesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
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
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });
    }

    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        //items.addAll(moreItems);
        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        fetchArticles(offset);
        //int curSize = adapter.getItemCount();
        //adapter.notifyItemRangeInserted(curSize, items.size() - 1);
    }

    public void getSharedPreferencesSettings() {
        SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        beginDate = mSettings.getString("beginDate", getResources().getString(R.string.earliest_datepicker_date));
        endDate = mSettings.getString("endDate", getResources().getString(R.string.today));
        sortOrder = mSettings.getString("sortOrder", "newest");
        allNewsDeskValues = mSettings.getBoolean("allNewsDeskValues", true);
        newsDeskArtsSelected = mSettings.getBoolean("newsDeskArtsSelected", false);
        newsDeskFashionAndStyleSelected = mSettings.getBoolean("newsDeskFashionAndStyleSelected", false);
        newsDeskSportsSelected = mSettings.getBoolean("newsDeskSportsSelected", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                int currentArticleCount = adapter.getItemCount();
                // clear previous search results
                articles.clear();
                // and notify the adapter
                adapter.notifyItemRangeRemoved(0, currentArticleCount);

                searchQuery = query;
                fetchArticles(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void fetchArticles(int page) {
        final ImageView ivSplash = (ImageView) findViewById(R.id.ivSplash);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "693d518cc15a1c220a4a1490ef49bdbc:10:23377");
        params.put("begin_date", convertToday(beginDate));
        params.put("end_date", convertToday(endDate));
        params.put("sort", sortOrder);
        params.put("page", page);
        params.put("q", searchQuery);
        if (!allNewsDeskValues) {
            // assemble the news desk filters
            String newDeskFilters = "news_desk:(";
            if (newsDeskArtsSelected) {
                newDeskFilters += "\"Arts\" ";
            }
            if (newsDeskFashionAndStyleSelected) {
                newDeskFilters += "\"Fashion & Style\" ";
            }
            if (newsDeskSportsSelected) {
                newDeskFilters += "\"Sports\" ";
            }
            newDeskFilters += ")";
            params.put("fq", newDeskFilters);
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    int previousArticlesLength = articles.size();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyItemRangeInserted(previousArticlesLength, articleJsonResults.length());
                    // once there are search results, remove the splash and show the grid
                    rvResults.setVisibility(View.VISIBLE);
                    ivSplash.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
            intent.putExtra("allNewsDeskValues", allNewsDeskValues);
            intent.putExtra("newsDeskArtsSelected", newsDeskArtsSelected);
            intent.putExtra("newsDeskFashionAndStyleSelected", newsDeskFashionAndStyleSelected);
            intent.putExtra("newsDeskSportsSelected", newsDeskSportsSelected);
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
            allNewsDeskValues = data.getExtras().getBoolean("allNewsDeskValues");
            newsDeskArtsSelected = data.getExtras().getBoolean("newsDeskArtsSelected");
            newsDeskFashionAndStyleSelected = data.getExtras().getBoolean("newsDeskFashionAndStyleSelected");
            newsDeskSportsSelected = data.getExtras().getBoolean("newsDeskSportsSelected");
            SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("sortOrder", sortOrder);
            editor.putString("beginDate", beginDate);
            editor.putString("endDate", endDate);
            editor.putBoolean("allNewsDeskValues", allNewsDeskValues);
            editor.putBoolean("newsDeskArtsSelected", newsDeskArtsSelected);
            editor.putBoolean("newsDeskFashionAndStyleSelected", newsDeskFashionAndStyleSelected);
            editor.putBoolean("newsDeskSportsSelected", newsDeskSportsSelected);
            editor.commit();
        }
    }

    public String convertToday(String dateString) {
        if (dateString.equals(getResources().getString(R.string.today))) {
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);
        } else {
            return dateString;
        }

    }
}
