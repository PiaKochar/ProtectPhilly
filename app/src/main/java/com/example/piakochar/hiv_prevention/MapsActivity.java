package com.example.piakochar.hiv_prevention;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.drm.DrmManagerClient;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.piakochar.hiv_prevention.service.CondomsServiceCallback;
import com.example.piakochar.hiv_prevention.service.JSONParser;
import com.example.piakochar.hiv_prevention.service.JSONParserCallback;
import com.example.piakochar.hiv_prevention.service.ParseDirections;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.piakochar.hiv_prevention.service.GetDirections;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapsActivity extends FragmentActivity implements JSONParserCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ProgressDialog dialog;
    private LatLng here;
    private GetDirections gd;

    private JSONParserCallback context = this;
    private Polyline line;
    private Double[][] coordinates;
    private String[] siteName;
    private String[] addr;
    private boolean allowDirections;
    private String[] directions;
    final static String DIRECTIONS = "com.example.piakochar.hiv_prevention.DIRECTIONS";

    private boolean clinics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mMap.setOnInfoWindowClickListener(getInfoWindowClickListener());


        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();
        clinics = false;
        allowDirections = false;
        //service.getData();
        geoJSONReader("GIS_HEALTH.Condom_distribution_sites.json");

    }

    public void geoJSONReader(String filename) {
        try {

            AssetManager assetManager = getAssets();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(filename)));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            dialog.hide();

            String s = result.toString();
            System.out.println("RESULTS FROM READ: " + result);
            JSONParser parser = new JSONParser(s, this, clinics);
            coordinates = parser.getCoordinates();
            siteName = parser.getSiteName();
            addr = parser.getAddr();

            System.out.println("FILENAME: " + filename);
            for (int i = 0; i < siteName.length; i++ ) {
                System.out.println("SITENAMES: " + siteName[i]);
            }

            BitmapDescriptor bitmapDescriptor;
            if (clinics) {
                bitmapDescriptor = BitmapDescriptorFactory.fromAsset("clinicLogo.png");
            } else {
                bitmapDescriptor = BitmapDescriptorFactory.fromAsset("condomLogo.png");
            }
//            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
//            BitmapDescriptorFactory.fromAsset("blue_dude.png");
            for (int i = 0; i < coordinates.length; i++) {
                LatLng place = new LatLng(coordinates[i][1], coordinates[i][0]);
                Marker placeMarker = mMap.addMarker(new MarkerOptions().position(place).icon(bitmapDescriptor).title(siteName[i]).snippet(String.format(addr[i] + ": Click to select route")));
            }


        } catch (Exception e) {
            serviceFailure(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // Enable MyLocation Layer of Google Map
        //mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        try {
            Location myLocation = locationManager.getLastKnownLocation(provider);
//            // set map type
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
            if (myLocation == null) {
                here = new LatLng(39.905317, -75.173490);
            } else {
                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();
                here = new LatLng(latitude, longitude);
            }

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(here));

            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(here, 14);
            mMap.animateCamera(yourLocation);

            // Zoom in the Google Map
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
            mMap.addMarker(new MarkerOptions().position(here).title("You are here!").snippet("Wells Fargo Center"));

        } catch (SecurityException e) {
            serviceFailure(e);

        }
    }

    public GoogleMap.OnInfoWindowClickListener getInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                double here_latitude = here.latitude;
                double here_longitude = here.longitude;
                double dest_latitude = marker.getPosition().latitude;
                double dest_longitude = marker.getPosition().longitude;
                String endpoint = "http://maps.googleapis.com/maps/api/directions/json?origin=" + here_latitude + "," + here_longitude + "&destination=" + dest_latitude + "," + dest_longitude + "&sensor=false&mode=%22WALKING%22";
                //Toast.makeText(getApplicationContext(), "Clicked window with title..." + marker.getTitle(), Toast.LENGTH_SHORT).show();
                if (line != null) {
                    mMap.clear();
                    BitmapDescriptor bitmapDescriptor;
                    if (clinics) {
                        bitmapDescriptor = BitmapDescriptorFactory.fromAsset("clinicLogo.png");
                    } else {
                        bitmapDescriptor = BitmapDescriptorFactory.fromAsset("condomLogo.png");
                    }
//                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
//            BitmapDescriptorFactory.fromAsset("blue_dude.png");
                    for (int i = 0; i < coordinates.length; i++) {
                        LatLng place = new LatLng(coordinates[i][1], coordinates[i][0]);
                        Marker placeMarker = mMap.addMarker(new MarkerOptions().position(place).icon(bitmapDescriptor).title(siteName[i]).snippet(String.format("%s \n%s", addr[i], "Click Here For Directions")));
                    }
                    mMap.addMarker(new MarkerOptions().position(here).title("You are here!").snippet("Wells Fargo Center"));
                }
                gd = new GetDirections(endpoint, context);
                dialog = new ProgressDialog((Context)context);
                dialog.setMessage("Loading...");
                dialog.show();

            }
        };
    }

    @Override
    public void serviceSuccess() {
        dialog.hide();
        String[] dist = gd.getDistance();
        String[] duration = gd.getDuration();
        String[] instr = gd.getInstructions();

        String lineString = gd.getOverviewPolyline();
        List<LatLng> list = decodePoly(lineString);

        for (int i = 0; i < list.size() - 1; i++) {
            LatLng src = list.get(i);
            LatLng dest = list.get(i + 1);
            line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(5).color(Color.BLUE).geodesic(true));
        }
        allowDirections = true;

        directions = new String[dist.length];
        for (int i = 0; i < dist.length; i++) {
            directions[i] = instr[i].replace("<b>", "").replace("</b>", "").
                    replace("<div style=\"font-size:0.9em\">", " ").replace("</div>", "") + "\n" + dist[i] + ", " + duration[i];
        }

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public void clinics(View view) {
        clinics = (!clinics);
        allowDirections = false;
        TextView clinics_button = (TextView)findViewById(R.id.clinics_button);
        if (clinics) {
            clinics_button.setText("Condoms");
            mMap.clear();
            geoJSONReader("clinics.json");
            mMap.addMarker(new MarkerOptions().position(here).title("You are here!").snippet("Wells Fargo Center"));

        } else {
            clinics_button.setText("Clinics");
            mMap.clear();
            geoJSONReader("GIS_HEALTH.Condom_distribution_sites.json");
            mMap.addMarker(new MarkerOptions().position(here).title("You are here!").snippet("Wells Fargo Center"));

        }
    }

    public void directions(View view) {
        if (!allowDirections) {
            Toast.makeText(getApplicationContext(), "Please highlight a route!", Toast.LENGTH_SHORT).show();
            return;
        }
//        System.out.println("DIRECTIONS BUTTON");
        Intent intent = new Intent(this, DirectionsList.class);
        intent.putExtra(DIRECTIONS, directions);
        startActivity(intent);
    }

    public void info(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void splash(View view) {
        Intent intent = new Intent(this, SplashPage.class);
        startActivity(intent);
    }
    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show();
    }
}
