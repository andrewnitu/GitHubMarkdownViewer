package com.andrewnitu.githubmarkdownviewer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andrewnitu.githubmarkdownviewer.R;
import com.andrewnitu.githubmarkdownviewer.adapter.RepoListAdapter;
import com.andrewnitu.githubmarkdownviewer.model.Repo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String baseUrl = "https://api.github.com";

    private EditText usernameBox;
    private RecyclerView recyclerView;
    private ArrayList<Repo> repos;
    private RepoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repos = new ArrayList<Repo>();

        usernameBox = (EditText) findViewById(R.id.edit_text);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        adapter = new RepoListAdapter(repos);
        recyclerView.setAdapter(adapter);
    }

    public void repoRequest(final String username) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the URL to request the repositories for a user
        String requestURL = baseUrl + "/users/" + username + "/repos";

        // Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(requestURL,
                new Response.Listener<JSONArray>() {
                    // Do on a successful request
                    @Override
                    public void onResponse(JSONArray response) {
                        // If successful, clear the current repo list to make way for the new one
                        repos.clear();

                        try {
                            int numExtracted = 0;

                            // For each repo
                            while (numExtracted < response.length()) {
                                // Retrieve the name
                                String repoName = response.getJSONObject(numExtracted).getString("name");

                                // Create the request URL based on the username and reponame
                                String requestUrl = baseUrl + "/repos/" + username + "/" + repoName + "/contents";

                                // TODO: Add some second useful field for a repo
                                repos.add(new Repo(repoName,
                                        response.getJSONObject(numExtracted).getString("name")));
                                numExtracted++;
                            }
                        } catch (JSONException e) {
                        }

                        // Update the RecyclerView (don't wait for the user to)
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Give an error!
                        Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find that user!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public ArrayList<Repo> repoContainsMarkdown(String username, String reponame) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the URL to request the contents for a specific repo
        String requestURL = baseUrl + "/repos/" + username + "/" + reponame;

        // Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find that user!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void retrieveRepos(View view) {
        repoRequest(usernameBox.getText().toString());
    }
}
