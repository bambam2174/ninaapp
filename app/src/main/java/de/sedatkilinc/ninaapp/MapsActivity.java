package de.sedatkilinc.ninaapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveCanceledListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private static long LOCATION_REFRESH_TIME = 500;
    private static float LOCATION_REFRESH_DISTANCE = 0.01f;
    private int mAccuracy = 6;
    private int mCounter = 5;
    private Location mLocation = null;

    /*
    private final GoogleMap.OnCameraMoveListener mLISTENER = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {

        }
    };
*/
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", location.toString());
            Log.d("foo:Location accuracy", String.valueOf(location.getAccuracy()));
            Log.d("foo:mLocation accuracy", String.valueOf(mLocation.getAccuracy()));
            if(mLocation == null || mLocation.getAccuracy() > location.getAccuracy()) {
                Log.d("foo:mLocation before", String.valueOf(mLocation.getAccuracy()));
                mLocation = location;
                Log.d("foo:mLocation after", String.valueOf(mLocation.getAccuracy()));
            }

            if (mLocation.getAccuracy() <= mAccuracy) {
                final LatLng currLatLong = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currLatLong).title("You are here alt:" + mLocation.getAltitude()));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLong));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLong, 13));
                if (mMap.getCameraPosition().zoom > 16) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(1.0f), 1500, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            MapsActivity.this.animateMaptoLatLng(currLatLong);
                        }

                        @Override
                        public void onCancel() {
                            MapsActivity.this.animateMaptoLatLng(currLatLong);
                        }
                    });
                } else {
                    MapsActivity.this.animateMaptoLatLng(currLatLong);
                }

                MapsActivity.this.postCurrentPosition();
            } else {
                mCounter--;
                Log.d("foo:Counter", String.valueOf(mCounter));
                Log.d("foo:mAccuracy", String.valueOf(mAccuracy));
                if (mCounter < 0) {
                    mAccuracy++;
                    mCounter = 5;
                }
            }
            //mMap.zoo
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("onStatusChanged " + i, s);
            Log.d("Location ", mLocation.toString());
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("onProviderEnabled ", s);
            Log.d("Location ", mLocation.toString());
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("onProviderDisabled ", s);
            Log.d("Location ", mLocation.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission()) {
            this.mLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), false));
            Log.d("LastLoc Accuracy", String.valueOf(this.mLocation.getAccuracy()));
            this.mLocation.setAccuracy(20);
            Log.d("LastLoc2 Accuracy", String.valueOf(this.mLocation.getAccuracy()));
        }
    }

    private void obtainLocation() {
        if (this.checkLocationPermission()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.LOCATION_HARDWARE};
            requestPermissions(perms, 1340);
        }
    }

    private boolean checkLocationPermission() {
        Log.d("ACCESS_FINE_LOCATION", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
        Log.d("ACCESS_COARSE_LOCATION", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)));
        Log.d("XS_EXTRA_COMMANDS", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)));
        Log.d("LOCATION_HARDWARE", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (this.mLocation != null) {
            LatLng currLatLong = new LatLng(this.mLocation.getLatitude(), this.mLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currLatLong).title("Marker last location"));

            this.animateMaptoLatLng(currLatLong);
        }
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
    }

    @Override
    public void onCameraMove() {
        Log.d("onCameraMove", String.valueOf(mMap.getCameraPosition().bearing));
    }

    @Override
    public void onCameraIdle() {
        Log.d("onCameraIdle", String.valueOf(mMap.getCameraPosition().bearing));
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.d("onCameraMoveStarted", String.valueOf(mMap.getCameraPosition().bearing));
        Log.d("int i", String.valueOf(i));
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.d("onCameraMoveCanceled", String.valueOf(mMap.getCameraPosition().bearing));
    }

    private void animateMaptoLatLng(LatLng latLng) {
        Log.d("Zoom before anim", String.valueOf(mMap.getCameraPosition().zoom));


        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
        CameraPosition cameraPosition = cameraPositionBuilder
                .target(latLng)
                .zoom(17)
                .bearing(90)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.d("Zoom after anim", String.valueOf(mMap.getCameraPosition().zoom));
    }

    public void geoLocate(View view) {
        Toast.makeText(this, "Accuracy " + this.mLocation.getAccuracy() , Toast.LENGTH_SHORT).show();
        Log.d("geoLocate", view.toString());
        mAccuracy = 6;
        mCounter = 5;
        this.mLocation.setAccuracy(20);
        this.obtainLocation();
    }

    private void postCurrentPosition() {
        Toast.makeText(this, "Accuracy " + this.mLocation.getAccuracy(), Toast.LENGTH_LONG).show();
        mLocationManager.removeUpdates(mLocationListener);
        RequestService requestService = new RequestService(this);
        requestService.postLocation("http://istanbulchicks.hol.es?controller=gps&action=setgps&json=1", 1, 1, this.mLocation.getLatitude(), mLocation.getLongitude());
    }

    public void getLocation(View view) {
        Log.d("getLocation", view.toString());
        RequestService requestService = new RequestService(this);
        String szResponse = requestService.getResponse("http://istanbulchicks.hol.es?controller=gps&action=getgpsbyid&aa=1&json=1");
        Log.d("getLocation", szResponse);
        JSONObject objJSON = null;

        JSONArray arrJSON = null;
        try {
            //objJSON = new JSONObject(szResponse);
            arrJSON = new JSONArray(szResponse);

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
//        if (objJSON != null)
//            arrJSON = objJSON.getJSONArray();
        Log.d("array json", arrJSON.toString());
        try {
            for (int i = 0; i < arrJSON.length(); i++) {

                JSONObject currObj = arrJSON.getJSONObject(i);

                LatLng currLatLong = new LatLng(currObj.getDouble("lat"), currObj.getDouble("long"));
                mMap.addMarker(new MarkerOptions().position(currLatLong).title("Bullen"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
