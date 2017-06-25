package shruti.agarwal.org.iotapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class wordnik extends AppCompatActivity implements View.OnClickListener {

    int count;
    final  String tag_json_obj = "json_obj_req";
    public  String result, word;
    String fexamples, fmeaning, fantonyms, fsynonyms, fpronounciation;
    EditText getword;
    ProgressDialog pDialog;
    Button meaning, example, pronounciation, synonyms, antonyms, add, getinfo;
    TextView tv;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        getword = (EditText)findViewById(R.id.getword);
        getinfo = (Button)findViewById(R.id.getinfo);
        add = (Button)findViewById(R.id.add);
        meaning = (Button)findViewById(R.id.meaning);
        example = (Button)findViewById(R.id.example);
        synonyms = (Button)findViewById(R.id.synonyms);
        antonyms = (Button)findViewById(R.id.antonyms);
        pronounciation = (Button)findViewById(R.id.pronounciation);
        tv = (TextView)findViewById(R.id.textView4);
        word = getword.getText().toString();
        meaning.setOnClickListener( this);
        example.setOnClickListener( this);
        synonyms.setOnClickListener( this);
        antonyms.setOnClickListener( this);
        pronounciation.setOnClickListener(this);
        add.setOnClickListener(this);
        getinfo.setOnClickListener(this);

        //add button will be enabled when all dta is available
        add.setEnabled(false);

    }


    public void getinfo()
    {

        try {

            db = SQLiteDatabase.openDatabase("/sdcard/words", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("create table if not exists userdata (word text(333) primary key, meaning text(100000), examples text(100000), synonyms text(100000), antonyms text(100000), pronounciation text(333))");
            Cursor rec = db.rawQuery("select * from userdata where word='" + getword.getText() + "' ", null);
            rec.moveToFirst();
            if (!rec.isAfterLast()) {
                Toast.makeText(getApplicationContext(), "word ifo is = "+rec.getString(rec.getColumnIndex("meaning"))+" "+rec.getString(rec.getColumnIndex("examples"))+" "+rec.getString(rec.getColumnIndex("synonyms"))+" "+rec.getString(rec.getColumnIndex("antonyms"))+" "+rec.getString(rec.getColumnIndex("pronounciation")), Toast.LENGTH_LONG).show();
            } else
            {
                try {
                    count = 4;
                    meaning(getword.getText().toString());

                    examples(getword.getText().toString());

                    synonyms(getword.getText().toString());

                    antonyms(getword.getText().toString());

                    pronounciation(getword.getText().toString());

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                db.close();
                }
            }
        catch(Exception ex){
            Toast.makeText(getApplicationContext(), "database error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }


    }

    public void add() throws JSONException {
      //  getinfo(getword.getText().toString());
        try
        {

            db = SQLiteDatabase.openDatabase("/sdcard/words", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("create table if not exists userdata (word text(333) primary key, meaning text(100000), examples text(100000), synonyms text(100000), antonyms text(100000), pronounciation text(333))");
            db.execSQL("insert into userdata values ('" +word+ "','"+fmeaning+"','"+fexamples+"','"+fsynonyms+"','"+fantonyms+"','"+fpronounciation+"')");

            db.close();
            Toast.makeText(getApplicationContext(), "data inserted", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), fmeaning+" "+fexamples+" "+fsynonyms+" "+fantonyms+" "+fpronounciation, Toast.LENGTH_LONG).show();
            add.setEnabled(false);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "database error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }

    }



    public String examples(String s) throws JSONException {

        final String TAG = "KRISHNA";
        word = s;
        String url = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + word + "/examples";

        final String app_id = "a60d2e9c";
        final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";

        JsonObjectRequest jsonObjReq1 = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {
                        try {

                            String examples = "";
                            JSONArray jsonaaray = response.getJSONArray("results").getJSONObject(0).
                                    getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").
                                    getJSONObject(0).getJSONArray("senses");

                            for (int i = 0; i < jsonaaray.length(); i++) {
                                String x = jsonaaray.getJSONObject(i).getJSONArray("examples").getJSONObject(0).get("text").toString();
                                examples += String.valueOf(i + 1);
                                examples += ".  ";
                                examples += x;
                                examples += '\n';
                            }
                           setExamples(examples);
                            count-=1;
                            tv.setText(fexamples);
                            if(count==0)
                            {
                                add.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                String str = "not available";
                setExamples(str);
                count-=1;
                tv.setText(fexamples);
                if(count==0)
                {
                    add.setEnabled(true);
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("app_id", app_id);
                headers.put("app_key", app_key);

                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq1, tag_json_obj);
        return getExamples();


    }

    public String pronounciation(String s) throws JSONException {

        final String TAG = "KRISHNA";

        word = s;
        String url = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/"+word+"/pronunciations";


        final String app_id = "a60d2e9c";
        final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";



        JsonObjectRequest jsonObjReq1 = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {


                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonaaray = response.getJSONArray("results").getJSONObject(0).
                                    getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("pronunciations");

                            String pronounciation = "";
                            for(int i=0; i<jsonaaray.length();i++)
                            {
                                String x = jsonaaray.getJSONObject(i).get("phoneticSpelling").toString();
                                pronounciation+=String.valueOf(i+1);
                                pronounciation+=".  ";
                                pronounciation+=x;
                                pronounciation+='\n';

                            }

                            setPronounciation(pronounciation);
                            count--;
                            tv.setText(fpronounciation);
                            if(count==0)
                            {
                                add.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                String str = "not available";
                setPronounciation(str);
                count--;

                tv.setText(fpronounciation);
                if(count==0)
                {
                    add.setEnabled(true);
                }
              //  pDialog.hide();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept","application/json");
                headers.put("app_id", app_id);
                headers.put("app_key", app_key);

                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq1, tag_json_obj);

        return getPrenounciation();

    }

    public String meaning(String s) throws JSONException {
        final String TAG = "KRISHNA";

        word = s;
        String url = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/"+word+"/definitions";

        final String app_id = "a60d2e9c";
        final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {


                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonaaray = response.getJSONArray("results").getJSONObject(0).
                                    getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").
                                    getJSONObject(0).getJSONArray("senses");
                            String meanings="";
                            for(int i=0; i<jsonaaray.length();i++)
                            {
                                String x = jsonaaray.getJSONObject(i).getJSONArray("definitions").get(0).toString();

                                meanings+=String.valueOf(i+1);
                                meanings+=".  ";
                                meanings+=x;
                                meanings+='\n';

                            }

                            setMeaning(meanings);
                            count-=1;

                            Log.d(TAG, fmeaning );
                            tv.setText(fmeaning);

                            if(count==0)
                            {
                                add.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                String str = "not available";
                setMeaning(str);
                count-=1;

                tv.setText(fmeaning);
                if(count==0)
                {
                    add.setEnabled(true);
                }
             //   pDialog.hide();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept","application/json");
                headers.put("app_id", app_id);
                headers.put("app_key", app_key);

                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return getMeaning();

    }

    public String synonyms(String s) throws JSONException {
        final String TAG = "KRISHNA";

        word = s;
        word = s;
        String url = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/"+word+"/synonyms";

        final String app_id = "a60d2e9c";
        final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {


                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonaaray = response.getJSONArray("results").getJSONObject(0).
                                    getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").
                                    getJSONObject(0).getJSONArray("senses").getJSONObject(0).getJSONArray("subsenses");

                            int c=0;

                            String synonyms="";
                            for(int i=0; i<jsonaaray.length();i++)
                            {

                                JSONArray json = jsonaaray.getJSONObject(i).getJSONArray("synonyms");
                                for(int j =0; j<json.length(); j++)
                                {
                                    String x = json.getJSONObject(j).get("text").toString();
                                    synonyms+=String.valueOf(c+1);
                                    synonyms+=".  ";
                                    synonyms+=x;
                                    synonyms+='\n';
                                    c+=1;
//                                    Log.d(TAG, );
                                }


                            }

                            setSynonyms(synonyms);
                            count-=1;

                            tv.setText(fsynonyms);
                            if(count==0)
                            {
                                add.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
                String str = "not available";
                setSynonyms(str);
                count-=1;

                tv.setText(fsynonyms);
                if(count==0)
                {
                    add.setEnabled(true);
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept","application/json");
                headers.put("app_id", app_id);
                headers.put("app_key", app_key);

                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return getSynonyms();

    }

    public String antonyms(String s) throws JSONException {
        final String TAG = "KRISHNA";

        word = s;
        word = s;
        String url = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/"+word+"/antonyms";


        final String app_id = "a60d2e9c";
        final String app_key = "1f35962e934ebd9a7a8b602ecbe4b9f3";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {


                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonaaray = response.getJSONArray("results").getJSONObject(0).
                                    getJSONArray("lexicalEntries").getJSONObject(0).getJSONArray("entries").
                                    getJSONObject(0).getJSONArray("senses");

                            String antonyms="";
                            int c=0;
                            for(int i=0; i<jsonaaray.length();i++)
                            {
                                JSONArray json = jsonaaray.getJSONObject(i).getJSONArray("antonyms");
                                for(int j =0; j<json.length(); j++)
                                {
                                    String x = json.getJSONObject(j).get("text").toString();
                                    antonyms+=String.valueOf(c+1);
                                    antonyms+=".  ";
                                    antonyms+=x;
                                    antonyms+='\n';

                                    c+=1;
                                   // Log.d(TAG, json.getJSONObject(j).get("text").toString());
                                }

                            }


                            setAntonyms(antonyms);
                            count-=1;

                            tv.setText(fantonyms);
                            if(count==0)
                            {
                                add.setEnabled(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                String str = "not available";
                setAntonyms(str);
                count-=1;

                tv.setText(fantonyms);
                if(count==0)
                {
                    add.setEnabled(true);
                }
              //  pDialog.hide();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept","application/json");
                headers.put("app_id", app_id);
                headers.put("app_key", app_key);

                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return getAntonyms();

    }

    public void onClick(View v) {

        if(v==add)
        {
            try {
              add();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(v==getinfo)
        {
            try {
               getinfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(v==meaning)
        {
            try {
                meaning(getword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(v==example)
        {
            try {

                examples(getword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if(v==pronounciation)
        {
            try {
                pronounciation(getword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if(v==synonyms)
        {
            try {
                synonyms(getword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(v==antonyms)
        {
            try {
                antonyms(getword.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setMeaning(String s)
    {
         fmeaning = s;

    }

    public String getMeaning()

    {
        return fmeaning;
    }


    public void setExamples(String s)
    {
        fexamples = s;

    }

    public String getExamples()

    {
        return fexamples;
    }


    public void setSynonyms(String s)
    {
        fsynonyms = s;

    }

    public String getSynonyms()

    {
        return fsynonyms;
    }


    public void setAntonyms(String s)
    {
        fantonyms = s;

    }

    public String getAntonyms()

    {
        return fantonyms;
    }


    public void setPronounciation(String s)
    {
        fpronounciation = s;

    }

    public String getPrenounciation()

    {
        return fpronounciation;
    }


    public boolean isRequestQueueEmpty()
    {
        return tag_json_obj.isEmpty();
    }


}





