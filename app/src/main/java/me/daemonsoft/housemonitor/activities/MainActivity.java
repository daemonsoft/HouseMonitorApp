package me.daemonsoft.housemonitor.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import android.widget.ToggleButton;

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
    private CollectionReference collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainDoorButton = findViewById(R.id.mainDoorButton);
        livingRoomSwitch = findViewById(R.id.livingRoomSwitch);

        // Reference to the collection "users"
        collection = FirebaseFirestore.getInstance().collection("devices");
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
        }
    }
}
