package shruti.agarwal.org.iotapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aasf on 15/2/17.
 */

public class sqlite extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase db;
    Button search;
    EditText getword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        search = (Button)findViewById(R.id.search);
        getword = (EditText)findViewById(R.id.getword);
        search.setOnClickListener(this);

        try
        {

        db = SQLiteDatabase.openDatabase("/sdcard/words", null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("create table if not exists userdata (word text(333) primary key, meaning text(100000), examples text(100000), synonyms text(100000), antonyms text(100000), pronounciation text(333))");

        }

        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "database error:" + ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void search()
    {

        Cursor rec = db.rawQuery("select * from userdata where word='"+getword.getText().toString()+"'", null);

        rec.moveToFirst();
        if(!rec.isAfterLast())
        {
            Toast.makeText(this, ( rec.getString(rec.getColumnIndex("meaning"))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, ( rec.getString(rec.getColumnIndex("examples"))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, ( rec.getString(rec.getColumnIndex("synonyms"))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, ( rec.getString(rec.getColumnIndex("antonyms"))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, ( rec.getString(rec.getColumnIndex("pronounciation"))), Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(getApplicationContext(),"word not found!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        if(v==search)
        {
            search();
        }
    }
}
