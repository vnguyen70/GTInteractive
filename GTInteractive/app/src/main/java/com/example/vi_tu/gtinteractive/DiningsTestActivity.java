package com.example.vi_tu.gtinteractive;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vi_tu.gtinteractive.domain.Dining;
import com.example.vi_tu.gtinteractive.persistence.DiningPersistence;
import com.example.vi_tu.gtinteractive.persistence.PersistenceHelper;
import com.example.vi_tu.gtinteractive.utilities.NetworkErrorDialogFragment;
import com.example.vi_tu.gtinteractive.utilities.NetworkUtils;

import org.joda.time.DateTime;

import java.util.List;

public class DiningsTestActivity extends AppCompatActivity implements NetworkErrorDialogFragment.NetworkErrorDialogListener {

    private TextView tvDiningsTest;

    private DiningPersistence diningsDB;

    private NetworkUtils networkUtils;

    public static final String REQUEST_TAG = "DiningsTestActivity";

    private static final long DININGS_CACHE_DURATION_MS = 86400000; // number of milliseconds in 1 day

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinings_test);
        tvDiningsTest = (TextView) findViewById(R.id.tv_dinings_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PersistenceHelper dbHelper = new PersistenceHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        diningsDB = new DiningPersistence(db);

        networkUtils = new NetworkUtils(getApplicationContext(), getFragmentManager());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        long nowMS = DateTime.now().getMillis();
        long diningsCacheExpiredMS = sharedPreferences.getLong("diningsCacheExpiredMS", 0);

        if (nowMS >= diningsCacheExpiredMS) {
            networkUtils.loadDiningsFromAPI(diningsDB);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("diningsCacheExpiredMS", nowMS + DININGS_CACHE_DURATION_MS);
            editor.apply();
        } else {
            Log.d("NETWORK_TEST", "dinings already loaded");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dinings_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_display:
                displayResults();
                return true;
            case R.id.action_reload:
                tvDiningsTest.setText("");
                networkUtils.loadDiningsFromAPI(diningsDB);
                return true;
            case R.id.action_clear:
                diningsDB.deleteAll();
                displayResults();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayResults() {
        List<Dining> dList = diningsDB.getAll();
        String results = "";
        for (Dining d : dList) {
            results += d.getDiningId() + " - " + d.getNameTokens() + " - " + d.getBuildingId() + "\n";
        }
        tvDiningsTest.setText("" + dList.size() + " dinings found:\n\n" + results);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog) {
        tvDiningsTest.setText("");
        networkUtils.loadDiningsFromAPI(diningsDB);
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        // TODO: display toast?
    }
}
