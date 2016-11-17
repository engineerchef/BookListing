package com.tolgaduran.android.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button search_btn;
    private Button clear_btn;
    private EditText titleSearchEditText;
    private EditText authorSearchEditText;
    private EditText subjectSearchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_btn = (Button) findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);

        clear_btn = (Button) findViewById(R.id.clear_btn);
        clear_btn.setOnClickListener(this);
    }

    public final boolean CheckInternetConn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(this, "Please check your internet connection!!!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        titleSearchEditText = (EditText) findViewById(R.id.search_title_text);
        authorSearchEditText = (EditText) findViewById(R.id.search_author_text);
        subjectSearchEditText = (EditText) findViewById(R.id.search_subject_text);
        if (CheckInternetConn(this)) {
            if (v.getId() == R.id.search_btn) {

                String titleString = formatSearchText(titleSearchEditText.getText().toString());
                String authorString = formatSearchText(authorSearchEditText.getText().toString());
                String subjectString = formatSearchText(subjectSearchEditText.getText().toString());

                if (titleString.equals("") && authorString.equals("") && subjectString.equals("")) {
                    Toast.makeText(MainActivity.this, getString(R.string.no_input_toast_message), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, BookReportActivity.class);
                    intent.putExtra("TITLE_SEARCH_STRING", titleString);
                    intent.putExtra("AUTHOR_SEARCH_STRING", authorString);
                    intent.putExtra("SUBJECT_SEARCH_STRING", subjectString);
                    startActivity(intent);
                }
            } else {
                titleSearchEditText.setText("");
                authorSearchEditText.setText("");
                subjectSearchEditText.setText("");
            }
        }
    }

    private String formatSearchText(String string) {
        String trimmedString = string.trim();
        do {
            trimmedString = trimmedString.replace(" ", "+");
        } while (trimmedString.contains(" "));
        return trimmedString;
    }
}
