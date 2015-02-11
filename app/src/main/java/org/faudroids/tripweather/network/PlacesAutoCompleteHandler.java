package org.faudroids.tripweather.network;

import android.util.Log;

import org.faudroids.tripweather.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class PlacesAutoCompleteHandler {

    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private static final String AUTOCOMPLETE_TYPE = "/autocomplete";
    private static final String OUTPUT_FORMAT_JSON = "/json";


    public ArrayList<String> complete(String input) throws RuntimeException {

        ArrayList<String> results = null;
        HttpURLConnection httpConnection = null;

        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE_URL + AUTOCOMPLETE_TYPE +
                    OUTPUT_FORMAT_JSON);
            sb.append("?key=" + R.string.google_places_key);
            sb.append("&components;=country:de");
            sb.append("&input;=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            httpConnection = (HttpURLConnection) url.openConnection();
            InputStream in = httpConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                jsonResults.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            Log.d("JSON object", "JSON: " + jsonResults.toString());

            String jsonStatus = jsonObject.getString("status");

            if(jsonStatus.equals(R.string.places_api_auth_error)) {
                throw new RuntimeException(jsonObject.getString("error_message"));
            }

            JSONArray predictions = jsonObject.getJSONArray("predictions");

            results = new ArrayList<>(predictions.length());
            for (int i = 0; i < predictions.length(); i++) {
                results.add(predictions.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("AUTOCOMPLETE", "List size: " + results.size());
        return results;
    }
}
