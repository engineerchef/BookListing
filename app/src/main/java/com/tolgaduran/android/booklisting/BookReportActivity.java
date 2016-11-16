package com.tolgaduran.android.booklisting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Java_Engineer on 16.11.2016.
 */
public class BookReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = BookReportActivity.class.getSimpleName();

    private String gbRequestURL = "";
    private String titleText;
    private String authorText;
    private String subjectText;
    private int hits = 0;
    private int index = 0;
    private static final int MAX_HITS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_report);

        TextView prevBtn = (TextView) findViewById(R.id.previous_btn);
        TextView nextBtn = (TextView) findViewById(R.id.next_btn);
        ImageView prevArrow = (ImageView) findViewById(R.id.prev_arrow);
        ImageView nextArrow = (ImageView) findViewById(R.id.next_arrow);

        Intent intent = getIntent();

        titleText = intent.getStringExtra("TITLE_SEARCH_STRING");
        if (!titleText.equals("")) {
            titleText = getString(R.string.TITLE_QUERY_URL) + titleText; //Adding prefix for Title search to use in URL for google API.
        } else {
            titleText = "";
        }

        authorText = intent.getStringExtra("AUTHOR_SEARCH_STRING");
        if (!authorText.equals("")) {
            authorText = getString(R.string.AUTHOR_QUERY_URL) + authorText; //Adding prefix for Author search to use in URL for google API.
        } else {
            authorText = "";
        }

        subjectText = intent.getStringExtra("SUBJECT_SEARCH_STRING");
        if (!subjectText.equals("")) {
            subjectText = getString(R.string.SUBJECT_QUERY_URL) + subjectText; //Adding prefix for Subject search to use in URL for google API.
        } else {
            subjectText = "";
        }

        gbRequestURL = getString(R.string.MAIN_QUERY_URL) + titleText + authorText + subjectText + getString(R.string.INDEX_URL) + String.valueOf(index) + getString(R.string.MAX_RESULTS_URL) + String.valueOf(MAX_HITS);

        BookSearchAsyncTask task = new BookSearchAsyncTask();
        task.execute();

        if (prevBtn != null && nextBtn != null && prevArrow != null && nextArrow != null) {
            nextBtn.setOnClickListener(this);
            prevBtn.setOnClickListener(this);
            nextArrow.setOnClickListener(this);
            prevArrow.setOnClickListener(this);
        }
    }

    /**
     * Update the screen to display information from the given {@link BookObject}.
     */
    private void updateUi(final ArrayList<BookObject> books) {

        final BookAdapter bookAdapter = new BookAdapter(this, books);

        ListView booksListView = (ListView) findViewById(R.id.list);
        if (booksListView != null) {
            booksListView.setAdapter(bookAdapter);

            booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(books.get(position).getUrl())));
                }
            });
        }

        TextView prevBtn = (TextView) findViewById(R.id.previous_btn);
        TextView nextBtn = (TextView) findViewById(R.id.next_btn);
        ImageView prevArrow = (ImageView) findViewById(R.id.prev_arrow);
        ImageView nextArrow = (ImageView) findViewById(R.id.next_arrow);

        if (prevBtn != null && nextBtn != null && prevArrow != null && nextArrow != null) {

            if (hits <= MAX_HITS + index) {
                nextBtn.setVisibility(View.GONE);
                nextArrow.setVisibility(View.GONE);
            } else {
                nextBtn.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
            }

            if (index == 0) {
                prevBtn.setVisibility(View.GONE);
                prevArrow.setVisibility(View.GONE);
            } else {
                prevBtn.setVisibility(View.VISIBLE);
                prevArrow.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.previous_btn || v.getId() == R.id.prev_arrow) {
            index = index - MAX_HITS;
            gbRequestURL = getString(R.string.MAIN_QUERY_URL) + titleText + authorText + subjectText + getString(R.string.INDEX_URL) + String.valueOf(index) + getString(R.string.MAX_RESULTS_URL) + String.valueOf(MAX_HITS);
            BookSearchAsyncTask task = new BookSearchAsyncTask();
            task.execute();
        } else {
            index = index + MAX_HITS;
            gbRequestURL = getString(R.string.MAIN_QUERY_URL) + titleText + authorText + subjectText + getString(R.string.INDEX_URL) + String.valueOf(index) + getString(R.string.MAX_RESULTS_URL) + String.valueOf(MAX_HITS);
            BookSearchAsyncTask task = new BookSearchAsyncTask();
            task.execute();
        }
    }

    private class BookSearchAsyncTask extends AsyncTask<URL, Void, ArrayList> {

        ProgressDialog asyncDialog = new ProgressDialog(BookReportActivity.this);

        @Override
        protected void onPreExecute() {

            asyncDialog.setMessage(getString(R.string.loading_book_data));
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<BookObject> doInBackground(URL... urls) {
            URL url = createUrl(gbRequestURL);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown, problem making Http Request", e);
            }

            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(ArrayList books) {
            if (books == null || books.size() == 0) {
                asyncDialog.dismiss();
                searchResultsDialogShower(getString(R.string.no_data));
            } else {
                updateUi(books);
                asyncDialog.dismiss();
            }
        }

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error with HttpRequest.  Error response code: " + urlConnection.getResponseCode());

                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException thrown, problem retriving google books JSON results", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<BookObject> extractFeatureFromJson(String booksJSON) {

            final ArrayList<BookObject> books = new ArrayList<>();
            if (TextUtils.isEmpty(booksJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(booksJSON);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

                hits = baseJsonResponse.optInt("totalItems");

                // If there are results in the itemsArray
                if (itemsArray.length() > 0) {

                    // Extract out the first feature (which is an earthquake)
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject bookItem = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = bookItem.getJSONObject("volumeInfo");
                        JSONObject images = volumeInfo.optJSONObject("imageLinks");
                        JSONArray authors = volumeInfo.optJSONArray("authors");

                        // Extract out the title, author, details, and urls.
                        String imageUrl;
                        String author;
                        String title = volumeInfo.optString("title");
                        if (authors != null) {
                            author = authors.optString(0);
                        } else {
                            author = "unknown"; //Add unknown if no author string is found
                        }
                        String description = volumeInfo.optString("description");
                        String url = volumeInfo.getString("infoLink");
                        if (images != null) {
                            imageUrl = images.optString("thumbnail");
                        } else {
                            imageUrl = "";
                        }
                        BookObject bookObject = new BookObject(title, author, description, url, imageUrl);
                        books.add(bookObject);
                    }
                    // Create a new {@link Event} object
                    return new ArrayList<>(books);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
            }
            return null;
        }
    }

    public void searchResultsDialogShower(String alertMessage) {

        LayoutInflater factory = LayoutInflater.from(this);
        final View searchDialogView = factory.inflate(R.layout.custom_alert, null);

        final AlertDialog searchDialog = new AlertDialog.Builder(this).create();
        searchDialog.setView(searchDialogView);
        searchDialog.show();

        Button okBtn = (Button) searchDialogView.findViewById(R.id.ok_btn);

        TextView messageTextView = (TextView) searchDialogView.findViewById(R.id.alert_message);
        messageTextView.setText(alertMessage);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
                finish();
            }
        });
    }
}
