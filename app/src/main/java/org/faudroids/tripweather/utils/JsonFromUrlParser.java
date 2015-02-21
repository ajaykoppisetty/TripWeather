package org.faudroids.tripweather.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

//I know that's kind of a shitty class name, but i couldn't figure out a better one at the moment :D
public class JsonFromUrlParser {

    JSONObject get(URL url) {
        StringBuilder sBuilder = new StringBuilder();

        try {
            InputStreamReader input = new InputStreamReader(url.openStream());
            BufferedReader bReader = new BufferedReader(input);
            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
                sBuilder.append("\n");
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(sBuilder.toString());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
