package course.labs.locationlab;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PlaceViewActivity extends ListActivity implements LocationListener {
    private static final long FIVE_MINS = 5 * 60 * 1000;
    private static final String TAG = "Lab-Location";

    // Set to false if you don't have network access
    public static boolean sHasNetwork = false;

    private Location mLastLocationReading;
    private PlaceViewAdapter mAdapter;
    private LocationManager mLocationManager;
    private boolean mMockLocationOn = false;

    // default minimum time between new readings
    private long mMinTime = 5000;

    // default minimum distance between old and new readings.
    private float mMinDistance = 1000.0f;

    // A fake location provider used for testing
    private MockLocationProvider mMockLocationProvider;
    private View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the app's user interface. This class is a ListActivity,
        // so it has its own ListView. ListView's adapter should be a PlaceViewAdapter

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ListView placesListView = getListView();

        footerView = getLayoutInflater().inflate(R.layout.footer_view, null);
        getListView().addFooterView(footerView);
        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "Entered footerView.OnClickListener.onClick()");
                if (mLastLocationReading == null) {
                    Toast.makeText(getApplicationContext(), "No location found!", Toast.LENGTH_LONG).show();
                    footerView.setClickable(false);
                    return;
                } else {
                    if (mAdapter.intersects(mLastLocationReading)) {
                        Toast.makeText(getApplicationContext(), "You already have this location badge", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        sHasNetwork = true;
                        new PlaceDownloaderTask(PlaceViewActivity.this, sHasNetwork).execute(mLastLocationReading);

                    }
                }
            }
        });

        placesListView.addFooterView(footerView);
        mAdapter = new PlaceViewAdapter(getApplicationContext());
        setListAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startMockLocationManager();
        if (null == mLastLocationReading
                || mLastLocationReading.getAccuracy() > mMinDistance
                || mLastLocationReading.getTime() < System.currentTimeMillis()
                - mMinTime) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
            footerView.setClickable(true);
            return;
        }

    }

    @Override
    protected void onPause() {
        mLocationManager.removeUpdates(this);

        shutdownMockLocationManager();
        super.onPause();
    }

    // Callback method used by PlaceDownloaderTask
    public void addNewPlace(PlaceRecord place) {
        Log.i(TAG, "Entered addNewPlace()");
        if (mLastLocationReading != null && mAdapter.intersects(mLastLocationReading)) {
            Toast.makeText(getApplicationContext(), "You already have this location badge", Toast.LENGTH_LONG).show();
            return;
        }
        if (place == null) {
            Toast.makeText(getApplicationContext(), "PlaceBadge could not be acquired", Toast.LENGTH_LONG).show();
            return;
        }
        if (place.getCountryName() == "") {
            Toast.makeText(getApplicationContext(), "There is no country at this location", Toast.LENGTH_LONG).show();
            return;
        }
        mAdapter.add(place);
    }

    // LocationListener methods
    @Override
    public void onLocationChanged(Location currentLocation) {
        footerView.setClickable(true);
        if (mLastLocationReading == null) {
            mLastLocationReading = currentLocation;
            return;
        }

        if (currentLocation.getTime() < mLastLocationReading.getTime())
            return;

        mLastLocationReading = currentLocation;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // not implemented
    }

    @Override
    public void onProviderEnabled(String provider) {
        // not implemented
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // not implemented
    }

    // Returns age of location in milliseconds
    private long ageInMilliseconds(Location location) {
        return System.currentTimeMillis() - location.getTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_badges:
                mAdapter.removeAllViews();
                return true;
            case R.id.place_one:
                footerView.setClickable(true);
                mMockLocationProvider.pushLocation(37.422, -122.084);
                return true;
            case R.id.place_no_country:
                mMockLocationProvider.pushLocation(0, 0);
                return true;
            case R.id.place_two:
                footerView.setClickable(true);
                mMockLocationProvider.pushLocation(38.996667, -76.9275);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shutdownMockLocationManager() {
        if (mMockLocationOn) {
            mMockLocationProvider.shutdown();
        }
    }

    private void startMockLocationManager() {
        if (!mMockLocationOn) {
            mMockLocationProvider = new MockLocationProvider(
                    LocationManager.NETWORK_PROVIDER, this);
        }
    }
}