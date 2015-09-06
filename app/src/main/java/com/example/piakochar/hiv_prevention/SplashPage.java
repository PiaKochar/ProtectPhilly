package com.example.piakochar.hiv_prevention;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.Intent;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

        ArrayList<String> jokes = getJoke();
        int i =  (int) (Math.random()*jokes.size());
        String joke = jokes.get(i);

        TextView text = (TextView)findViewById(R.id.textView5);
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/brandontext.otf");
        text.setTypeface(font);
        text.setText(joke);

    }

    public ArrayList<String> getJoke() {
        AssetManager assetManager = getAssets();
        ArrayList<String> list = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("condom_jokes.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            System.out.println("error reading file");
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
