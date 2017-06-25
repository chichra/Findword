package shruti.agarwal.org.iotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//add dependencies to your class
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aasf on 15/2/17.
 */

public class xml extends AppCompatActivity
{
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            new CallbackTask().execute(dictionaryEntries());
        }

        private String dictionaryEntries() {
            final String language = "en";
            final String word = "Ace";
            final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
            return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
        }


        //in android calling network requests on the main thread forbidden by default
        //create class to do async job
        private class CallbackTask extends AsyncTask<String, Integer, String> {

            @Override
            protected String doInBackground(String... params) {

                //TODO: replace with your own app id and app key
                final String app_id = "a60d2e9c";
                final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";
                try {
                    URL url = new URL(params[0]);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Accept","application/json");
                    urlConnection.setRequestProperty("app_id",app_id);
                    urlConnection.setRequestProperty("app_key",app_key);

                    // read the output from the server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    return stringBuilder.toString();

                }
                catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("vishakha", result);
                System.out.println(result);
            }
        }
    }