package ecoext.com.ecoext;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetUserInformationFromDataBase extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            Log.i("Web Content", result);
            JSONObject json = new JSONObject(result);
            String weather = json.getString("weather");

            JSONArray arr = new JSONArray(weather);

            for (int i = 0; i< arr.length(); i++) {
                // convert the list in individual objects
                JSONObject jsonPart = arr.getJSONObject(i);

                Log.i("main", jsonPart.getString("main"));
                Log.i("description", jsonPart.getString("description"));
            }
        } catch (JSONException e) {

        }

    }
}
