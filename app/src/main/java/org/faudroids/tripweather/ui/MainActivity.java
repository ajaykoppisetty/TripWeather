package org.faudroids.tripweather.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.core.PlacesAutoCompleteAdapter;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView autoCompleteFrom = (AutoCompleteTextView) findViewById(R.id
                .autocomplete_from);
        autoCompleteFrom.setAdapter(new PlacesAutoCompleteAdapter(this,
                R.layout.autocomplete_item));
        AutoCompleteTextView autoCompleteTo = (AutoCompleteTextView) findViewById(R.id
                .autocomplete_to);
        autoCompleteTo.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
