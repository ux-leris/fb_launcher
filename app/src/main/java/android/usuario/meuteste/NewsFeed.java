package android.usuario.meuteste;

/**
 * Copyright 2014-present UFSCar - LERIS - Laboratory of Studies in Networks, Innovation and Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Locale;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.app.ListActivity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.speech.tts.TextToSpeech;
        import android.speech.tts.TextToSpeech.OnInitListener;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.facebook.AccessToken;
        import com.facebook.GraphRequest;
        import com.facebook.GraphResponse;
        import com.facebook.HttpMethod;

public class NewsFeed extends ListActivity implements OnInitListener {

    private ProgressDialog pDialog;

    // URL to get feeds JSON
    private String url = "";

    // JSON Node names
    private static final String TAG_ENTRIES = "data";
    private static final String TAG_FROM = "from";
    private static final String TAG_NAME = "name";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_DESCRIPTION = "name";
    private static final String TAG_DESCRIPTION2 = "description";

    private String token;
    private String userId;
    private String message = "";
    private String title = "";

    // feeds JSONArray
    JSONArray entries = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> entriesList;

    // TTS
    private TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news_feed);
        /*Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                token = params.getString("token");
                userId = params.getString("userId");
            }
        }*/


        entriesList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();
        // tts
        Context context = this;
        OnInitListener listener = this;
        tts = new TextToSpeech(context, listener);

        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                title = ((TextView) view.findViewById(R.id.title)).getText()
                        .toString();
                message = ((TextView) view.findViewById(R.id.message))
                        .getText().toString();
                tts.speak(title +  message, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        // Calling async task to get json
        new GetFeeds().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetFeeds extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = ProgressDialog.show(NewsFeed.this,
                    getString(R.string.app_name),
                    getString(R.string.news_feed_message), false, true);
            pDialog.setIcon(R.drawable.icon);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/feed",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            String jsonStr = response.getRawResponse();
                            Log.v("feed", response.toString());
                            if (jsonStr != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(jsonStr);

                                    // Getting JSON Array node
                                    entries = jsonObj.getJSONArray(TAG_ENTRIES);
                                    Log.d("Entries: ", "> " + entries);

                                    // looping through All Feeds
                                    for (int i = 0; i < entries.length(); i++) {
                                        JSONObject c = entries.getJSONObject(i);

                                        if (c.has(TAG_MESSAGE)) {
                                            message = c.getString(TAG_MESSAGE);
                                        } // if it's a story, it has only description
                                        else if (c.has(TAG_DESCRIPTION)) {
                                            message = c.getString(TAG_DESCRIPTION);
                                        } else
                                        {message = c.getString(TAG_DESCRIPTION2);}
                                        // From node is JSON Object
                                        JSONObject from = c.getJSONObject(TAG_FROM);

                                        title = from.getString(TAG_NAME);


                                        // tmp hashmap for single message feed
                                        HashMap<String, String> feed = new HashMap<String, String>();

                                        // adding each child node to HashMap key => value
                                        feed.put(TAG_NAME, title);
                                        feed.put(TAG_MESSAGE, message);


                                        // adding feed to feed list
                                        entriesList.add(feed);
                                        // clear the fields
                                        title = "";
                                        message = "";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e("ServiceHandler", "Couldn't get any data from the url");
                            }
                        }
                    }
            );
            // Creating service handler class instance

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
            ListAdapter adapter = new SimpleAdapter(NewsFeed.this, entriesList,
                    R.layout.list_item, new String[] { TAG_NAME, TAG_MESSAGE },
                    new int[] { R.id.title, R.id.message});

            setListAdapter(adapter);
        }

    }

    // tts
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (true) {
                int cod = tts.isLanguageAvailable(new Locale("pt", "BR"));
                if (cod == TextToSpeech.LANG_AVAILABLE) {
                    tts.setLanguage(new Locale("pt", "BR"));
                } else
                    Toast.makeText(
                            this,
                            "O idioma Portugu�s n‹o Ž suportado no seu aparelho!",
                            Toast.LENGTH_LONG).show();
            }
        }

    }

}
