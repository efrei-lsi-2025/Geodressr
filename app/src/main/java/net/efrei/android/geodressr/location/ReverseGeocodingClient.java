package net.efrei.android.geodressr.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class ReverseGeocodingClient {
    private static final String ApiEndpoint = "https://shinprog-geocoding.deno.dev/get-city/";

    final String apiUrl;
    public ReverseGeocodingClient(double lat, double lon, String key) {
        this.apiUrl = String.format(Locale.US, "%s?lat=%f&lon=%f&key=%s", ApiEndpoint, lat, lon, key);
    }

    public String executeRequest() {
        String responseString = "Unknown";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode ==  200) {
                InputStreamReader read = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader buff = new BufferedReader(read);
                responseString = buff.readLine();
                buff.close();
                read.close();
            }
            return responseString;
        } catch (Exception  e) {
            return responseString;
        }
    }
}