package me.daemonsoft.housemonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.daemonsoft.housemonitor.R;
import me.daemonsoft.housemonitor.models.Device;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ToggleButton mainDoorButton;
    private Switch livingRoomSwitch;
    private Switch mainRoomSiwtch;
    private CollectionReference collection;

    // [START declare_auth]
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (null == currentUser) {
            startLoginActivity();
        }


/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView mainDoorLabel = findViewById(R.id.mainDoorLabel);
        mainDoorLabel.setGravity(Gravity.CENTER_VERTICAL);

        mainDoorButton = findViewById(R.id.mainDoorButton);
        livingRoomSwitch = findViewById(R.id.livingRoomSwitch);
        mainRoomSiwtch = findViewById(R.id.mainRoomSwitch);
        final LineChart energyChart = findViewById(R.id.energy_chart);
        LineChart waterChart = findViewById(R.id.water_chart);


        // Reference to the collection "users"


        collection = FirebaseFirestore.getInstance()
                .collection(currentUser.getUid() + "/house/devices");

        FirebaseFirestore.getInstance()
                .collection(currentUser.getUid() + "/house/energy-comsuption")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        //YourData[] dataObjects = ...;

                        List<Entry> entries = new ArrayList<Entry>();

                        //for (YourData data : dataObjects) {

                        // turn your data into Entry objects
                        //    entries.add(new Entry(data.getValueX(), data.getValueY()));
                        // }

                        float counter = 0;

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("date") != null) {
                                counter++;
                                entries.add(new Entry(counter, doc.getDouble("value").floatValue()));
                            }
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Label");

                        LineData lineData = new LineData(dataSet);
                        energyChart.setData(lineData);
                        energyChart.invalidate();

                    }
                });

        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<Device> devices = new ArrayList<>();
                Device deviceFireStore;
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        deviceFireStore = doc.toObject(Device.class);
                        deviceFireStore.setId(doc.getId());
                        devices.add(deviceFireStore);
                    }
                }
                for (Device device : devices) {
                    Log.d(TAG, "Current cites in CA: " + device.getId());
                    if ("maindoor".equals(device.getId())) {
                        mainDoorButton.setChecked(0 != device.getStatus());
                    } else if ("livingroomligth".equals(device.getId())) {
                        livingRoomSwitch.setChecked(0 != device.getStatus());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {
        final Map<String, Object> data = new HashMap<>();
        switch (view.getId()) {
            case R.id.mainDoorButton:
                data.put("status", mainDoorButton.isChecked() ? 1 : 0);
                collection.document("maindoor").set(data, SetOptions.merge());
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                data.put("status", 0);
                                collection.document("maindoor").set(data, SetOptions.merge());
                            }
                        },
                        3000
                );
                break;
            case R.id.livingRoomSwitch:
                data.put("status", livingRoomSwitch.isChecked() ? 1 : 0);
                collection.document("livingroomligth").set(data, SetOptions.merge());
                break;
            case R.id.mainRoomSwitch:
                data.put("status", mainRoomSiwtch.isChecked() ? 1 : 0);
                collection.document("mainroomligth").set(data, SetOptions.merge());
                break;
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

    }

    // [END on_start_check_user]

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
