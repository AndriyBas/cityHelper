package com.parse.anywall.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.parse.*;
import com.parse.anywall.Application;
import com.parse.anywall.Const;
import com.parse.anywall.R;
import com.parse.anywall.model.Issue;
import org.json.JSONArray;

import java.util.*;

public class MainActivity extends CityHelperBaseActivity implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        MenuItem.OnMenuItemClickListener, GoogleMap.OnInfoWindowClickListener {

    /*
     * Define a request code to send to Google Play services This code is returned in
     * Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    /*
     * Constants for handling location results
     */
    // Conversion from feet to meters
    private static final float METERS_PER_FEET = 0.3048f;

    // Conversion from kilometers to meters
    private static final int METERS_PER_KILOMETER = 1000;

    // Initial offset for calculating the map bounds
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;

    // Accuracy for calculating the map bounds
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 100;

    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 1000;

    /*
     * Other class member variables
     */
    // Map fragment
    private MapFragment map;

    // Represents the circle around a map
    private Circle mapCircle;

    // Fields for the map radius in feet
    private static float radius;
    public static float lastRadius;

    // Fields for helping process map and location changes
    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
    private int mostRecentMapUpdate = 0;
    private boolean hasSetUpInitialLocation = false;
    private String selectedObjectId;
    private int selectedObjectPosition;
    public static Location lastLocation = null;
    public static Location currentLocation = null;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient locationClient;

    // Adapter for the Parse query
    public static ParseQueryAdapter<Issue> mIssueAdapter;


    public static Issue CURRENT_ISSUE;
    public static ListView postsView;


    public static Context localAppContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        localAppContext = this;

        super.onCreate(savedInstanceState);
        radius = Application.getSearchDistance();
        lastRadius = radius;
        setContentView(R.layout.activity_main);

        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new LocationClient(this, this, this);

        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Issue> factory =
                new ParseQueryAdapter.QueryFactory<Issue>() {
                    public ParseQuery<Issue> create() {
                        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
                        ParseQuery<Issue> query = ParseQuery.getQuery(Issue.class);
                        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                        query.include("author");
//                        query.include("photo");
                        query.orderByDescending("createdAt");
                        query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                                * METERS_PER_FEET / METERS_PER_KILOMETER);
                        query.setLimit(MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        // Set up the query adapter
        mIssueAdapter = new ParseQueryAdapter<Issue>(this, factory) {
            @Override
            public View getItemView(Issue issue, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.item_task_post, null);
                }

                ImageView issueImage = (ImageView) view.findViewById(R.id.item_post_image);
                TextView title = (TextView) view.findViewById(R.id.item_post_title);
                TextView status = (TextView) view.findViewById(R.id.status_text);
                TextView description = (TextView) view.findViewById(R.id.item_post_desc);

                title.setText(issue.getTitle());
                status.setText("Статус: " + issue.getStatus());
                description.setText("Опис: " + issue.getDetail());


                try {
                    if (issue.getPhoto() != null) {
                        byte[] bytes = issue.getPhoto().getData();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        issueImage.setImageBitmap(bmp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        mIssueAdapter.setAutoload(false);


        // Disable pagination, we'll manage the query limit ourselves
        mIssueAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        postsView = (ListView) findViewById(R.id.postsView);
        postsView.setAdapter(mIssueAdapter);

        // Set up the handler for an item's selection
        postsView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Issue item = mIssueAdapter.getItem(position);

                Const.isNewIssue = false;

                String currentObjectId = item.getObjectId();

                if (currentObjectId == selectedObjectId) {

                    selectedObjectPosition = position;

                    CURRENT_ISSUE = item;

                    Intent i = new Intent(MainActivity.this, IssueActivity.class);

                    startActivity(i);

                    return;
                }

                selectedObjectId = currentObjectId;
                selectedObjectPosition = position;

                map.getMap().animateCamera(
                        CameraUpdateFactory.newLatLng(new LatLng(item.getLocation().getLatitude(), item
                                .getLocation().getLongitude())), new CancelableCallback() {
                            public void onFinish() {
                                Marker marker = mapMarkers.get(item.getObjectId());
                                if (marker != null) {
                                    marker.showInfoWindow();
                                }
                            }

                            public void onCancel() {
                            }
                        }
                );
                Marker marker = mapMarkers.get(item.getObjectId());
                if (marker != null) {
                    marker.showInfoWindow();
                }


            }
        });

        // Set up the map fragment
        map = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        // Enable the current location "blue dot"
        map.getMap().setMyLocationEnabled(true);
        // Set up the camera change handler
        map.getMap().setOnCameraChangeListener(new OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // When the camera changes, update the query
                doMapQuery();
            }
        });

        map.getMap().setOnInfoWindowClickListener(this);


        SeekBar seekBar = (SeekBar) findViewById(R.id.main_seek_bar);
        seekBar.setProgress((int) Application.getSearchDistance());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = Math.max(progress, 400);

                seekBar.setProgress(progress);

                radius = progress;

                Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
                if (myLoc != null) {
                    LatLng latLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
                    updateCircle(latLng);
                    updateZoom(latLng);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public static void requeryIssues(final List<String> statuses, final List<String> tags) {
// Set up a customized query

        mIssueAdapter.clear();
        mIssueAdapter.notifyDataSetChanged();

        ParseQueryAdapter.QueryFactory<Issue> factory =
                new ParseQueryAdapter.QueryFactory<Issue>() {
                    public ParseQuery<Issue> create() {
                        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
                        ParseQuery<Issue> query = ParseQuery.getQuery(Issue.class);
//                        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                        query.include("author");
//                        query.include("photo");
                        if (statuses != null) {
                            query.whereContainedIn("status", statuses);
                        }

                        query.orderByDescending("createdAt");
                        query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                                * METERS_PER_FEET / METERS_PER_KILOMETER);
                        query.setLimit(MAX_POST_SEARCH_RESULTS);

                        if (tags != null) {
                            List<String> ids = new ArrayList<String>();
                            try {
                                List<Issue> list = query.find();
                                for (int i = 0; i < list.size(); i++) {
                                    JSONArray arr = list.get(i).getTags();
                                    for (int j = 0; j < arr.length(); j++) {
                                        if (tags.contains(arr.getString(j))) {
                                            ids.add(list.get(i).getObjectId());
                                            break;
                                        }
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            query.whereContainedIn("objectId", ids);
                        }

                        return query;
                    }
                };


        // Set up the query adapter
        mIssueAdapter = new ParseQueryAdapter<Issue>(localAppContext, factory) {
            @Override
            public View getItemView(Issue issue, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.item_task_post, null);
                }

                ImageView issueImage = (ImageView) view.findViewById(R.id.item_post_image);
                TextView title = (TextView) view.findViewById(R.id.item_post_title);
                TextView status = (TextView) view.findViewById(R.id.status_text);
                TextView description = (TextView) view.findViewById(R.id.item_post_desc);

                title.setText(issue.getTitle());
                status.setText("Статус: " + issue.getStatus());
                description.setText("Опис: " + issue.getDetail());


                try {
                    if (issue.getPhoto() != null) {
                        byte[] bytes = issue.getPhoto().getData();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        issueImage.setImageBitmap(bmp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        mIssueAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        mIssueAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        postsView.setAdapter(mIssueAdapter);

        mIssueAdapter.loadObjects();


    }

    /*
     * Called when the Activity is no longer visible at all. Stop updates and disconnect.
     */
    @Override
    public void onStop() {
        // If the client is connected
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();

        super.onStop();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Connect to the location services client
        locationClient.connect();
    }

    /*
     * Called when the Activity is resumed. Updates the view.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Get the latest search distance preference
        radius = Application.getSearchDistance();
        // Checks the last saved location to show cached data if it's available
        if (lastLocation != null) {
            LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            // If the search distance preference has been changed, move
            // map to new bounds.
            if (lastRadius != radius) {
                updateZoom(myLatLng);
            }
            // Update the circle map
            updateCircle(myLatLng);
        }
        // Save the current radius
        lastRadius = radius;
        // Query for the latest data to update the views.
        doMapQuery();
        doListQuery();
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to start
     * an Activity that handles Google Play services problems. The result of this call returns here,
     * to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case FragmentActivity.RESULT_OK:

                        if (Application.APPDEBUG) {
                            // Log the result
                            Log.d(Application.APPTAG, "Connected to Google Play services");
                        }

                        break;

                    // If any other result was returned by Google Play services
                    default:
                        if (Application.APPDEBUG) {
                            // Log the result
                            Log.d(Application.APPTAG, "Could not connect to Google Play services");
                        }
                        break;
                }

                // If any other request code was received
            default:
                if (Application.APPDEBUG) {
                    // Report that this Activity received an unknown requestCode
                    Log.d(Application.APPTAG, "Unknown request code received for the activity");
                }
                break;
        }
    }

    /*
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Application.APPDEBUG) {
                // In debug mode, log the status
                Log.d(Application.APPTAG, "Google play services available");
            }
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), Application.APPTAG);
            }
            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client finishes successfully. At
     * this point, you can request the current location or start periodic updates
     */
    public void onConnected(Bundle bundle) {
        if (Application.APPDEBUG) {
            Log.d("Connected to location services", Application.APPTAG);
        }
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    /*
     * Called by Location Services if the connection to the location client drops because of an error.
     */
    public void onDisconnected() {
        if (Application.APPDEBUG) {
            Log.d("Disconnected from location services", Application.APPTAG);
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects. If the error has a resolution, try
        // sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                if (Application.APPDEBUG) {
                    // Thrown if Google Play services canceled the original PendingIntent
                    Log.d(Application.APPTAG, "An error occurred when connecting to location services.", e);
                }
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /*
     * Report location updates to the UI.
     */
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!hasSetUpInitialLocation) {
            // Zoom to the current location.
            updateZoom(myLatLng);
            hasSetUpInitialLocation = true;
        }
        // Update map radius indicator
        updateCircle(myLatLng);
        doMapQuery();
        doListQuery();
    }

    /*
     * In response to a request to start updates, send a request to Location Services
     */
    private void startPeriodicUpdates() {
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    /*
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {
        locationClient.removeLocationUpdates(this);
    }

    /*
     * Get the current location
     */
    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            return locationClient.getLastLocation();
        } else {
            return null;
        }
    }

    /*
     * Set up a query to update the list view
     */
    private void doListQuery() {
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info is available, load the data
        if (myLoc != null) {
            // Refreshes the list view with new data based
            // usually on updated location data.
            mIssueAdapter.loadObjects();
        }
    }


    private void requeryMap() {


    }


    /*
     * Set up the query to update the map view
     */
    private void doMapQuery() {
        final int myUpdateNumber = ++mostRecentMapUpdate;
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info isn't available, clean up any existing markers
        if (myLoc == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }

        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);
        // Create the map Parse query
        ParseQuery<Issue> mapQuery = ParseQuery.getQuery(Issue.class);
        // Set up additional query filters
        mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
        // Kick off the query in the background
        mapQuery.findInBackground(new FindCallback<Issue>() {
            @Override
            public void done(List<Issue> objects, ParseException e) {
                if (e != null) {
                    if (Application.APPDEBUG) {
                        Log.d(Application.APPTAG, "An error occurred while querying for map mIssueAdapter.", e);
                    }
                    return;
                }
        /*
         * Make sure we're processing results from
         * the most recent update, in case there
         * may be more than one in progress.
         */
                if (myUpdateNumber != mostRecentMapUpdate) {
                    return;
                }
                // Posts to show on the map
                Set<String> toKeep = new HashSet<String>();
                // Loop through the results of the search
                for (Issue issueItem : objects) {
                    // Add this issueItem to the list of map pins to keep
                    toKeep.add(issueItem.getObjectId());
                    // Check for an existing marker for this issueItem
                    Marker oldMarker = mapMarkers.get(issueItem.getObjectId());
                    // Set up the map marker's location
                    MarkerOptions markerOpts =
                            new MarkerOptions().position(new LatLng(issueItem.getLocation().getLatitude(), issueItem
                                    .getLocation().getLongitude()));
                    // Set up the marker properties based on if it is within the search radius
                    if (issueItem.getLocation().distanceInKilometersTo(myPoint) > radius * METERS_PER_FEET
                            / METERS_PER_KILOMETER) {
                        // Check for an existing out of range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() == null) {
                                // Out of range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now out of range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        // Display a red marker with a predefined title and no snippet
                        markerOpts =
                                markerOpts.title(getResources().getString(R.string.post_out_of_range)).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        // Check for an existing in range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() != null) {
                                // In range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now in range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        // Display a green marker with the issueItem information
                        markerOpts =
                                markerOpts.title(issueItem.getTitle()).snippet(issueItem.getDetail())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .draggable(true);
                    }
                    // Add a new marker
                    Marker marker = map.getMap().addMarker(markerOpts);
                    mapMarkers.put(issueItem.getObjectId(), marker);
                    if (issueItem.getObjectId().equals(selectedObjectId)) {
                        marker.showInfoWindow();
                        selectedObjectId = null;
                        selectedObjectPosition = -1;
                    }
                }
                // Clean up old markers.
                cleanUpMarkers(toKeep);
            }
        });
    }

    /*
     * Helper method to clean up old markers
     */
    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<String>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }

    /*
     * Helper method to get the Parse GEO point representation of a location
     */
    private static ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    /*
     * Displays a circle on the map representing the search radius
     */
    private void updateCircle(LatLng myLatLng) {
        if (mapCircle == null) {
            mapCircle =
                    map.getMap().addCircle(
                            new CircleOptions().center(myLatLng).radius(radius * METERS_PER_FEET));
            int baseColor = Color.DKGRAY;
            mapCircle.setStrokeColor(baseColor);
            mapCircle.setStrokeWidth(2);
            mapCircle.setFillColor(Color.argb(50, Color.red(baseColor), Color.green(baseColor),
                    Color.blue(baseColor)));
        }
        mapCircle.setCenter(myLatLng);
        mapCircle.setRadius(radius * METERS_PER_FEET); // Convert radius in feet to meters.
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    private void updateZoom(LatLng myLatLng) {
        // Get the bounds to zoom to
        LatLngBounds bounds = calculateBoundsWithCenter(myLatLng);
        // Zoom to the given bounds
        map.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
    }

    /*
     * Helper method to calculate the offset for the bounds used in map zooming
     */
    private double calculateLatLngOffset(LatLng myLatLng, boolean bLatOffset) {
        // The return offset, initialized to the default difference
        double latLngOffset = OFFSET_CALCULATION_INIT_DIFF;
        // Set up the desired offset distance in meters
        float desiredOffsetInMeters = radius * METERS_PER_FEET;
        // Variables for the distance calculation
        float[] distance = new float[1];
        boolean foundMax = false;
        double foundMinDiff = 0;
        // Loop through and get the offset
        do {
            // Calculate the distance between the point of interest
            // and the current offset in the latitude or longitude direction
            if (bLatOffset) {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude
                        + latLngOffset, myLatLng.longitude, distance);
            } else {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude,
                        myLatLng.longitude + latLngOffset, distance);
            }
            // Compare the current difference with the desired one
            float distanceDiff = distance[0] - desiredOffsetInMeters;
            if (distanceDiff < 0) {
                // Need to catch up to the desired distance
                if (!foundMax) {
                    foundMinDiff = latLngOffset;
                    // Increase the calculated offset
                    latLngOffset *= 2;
                } else {
                    double tmp = latLngOffset;
                    // Increase the calculated offset, at a slower pace
                    latLngOffset += (latLngOffset - foundMinDiff) / 2;
                    foundMinDiff = tmp;
                }
            } else {
                // Overshot the desired distance
                // Decrease the calculated offset
                latLngOffset -= (latLngOffset - foundMinDiff) / 2;
                foundMax = true;
            }
        } while (Math.abs(distance[0] - desiredOffsetInMeters) > OFFSET_CALCULATION_ACCURACY);
        return latLngOffset;
    }

    /*
     * Helper method to calculate the bounds for map zooming
     */
    public LatLngBounds calculateBoundsWithCenter(LatLng myLatLng) {
        // Create a bounds
        LatLngBounds.Builder builder = LatLngBounds.builder();

        // Calculate east/west points that should to be included
        // in the bounds
        double lngDifference = calculateLatLngOffset(myLatLng, false);
        LatLng east = new LatLng(myLatLng.latitude, myLatLng.longitude + lngDifference);
        builder.include(east);
        LatLng west = new LatLng(myLatLng.latitude, myLatLng.longitude - lngDifference);
        builder.include(west);

        // Calculate north/south points that should to be included
        // in the bounds
        double latDifference = calculateLatLngOffset(myLatLng, true);
        LatLng north = new LatLng(myLatLng.latitude + latDifference, myLatLng.longitude);
        builder.include(north);
        LatLng south = new LatLng(myLatLng.latitude - latDifference, myLatLng.longitude);
        builder.include(south);

        return builder.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_user).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_top).setOnMenuItemClickListener(this);
        menu.findItem(R.id.menu_item_add_task).setOnMenuItemClickListener(this);
        return true;
    }


    /*
         * Show a dialog returned by Google Play services for the connection error code
         */
    private void showErrorDialog(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), Application.APPTAG);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_settings): {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            case (R.id.action_user): {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;
            }

            case (R.id.action_top): {
                startActivity(new Intent(MainActivity.this, RateActivity.class));
                return true;
            }
            case (R.id.menu_item_add_task): {

                // Only allow mIssueAdapter if we have a location
                Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;

                if (myLoc == null) {
                    Toast.makeText(MainActivity.this,
                            "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
                    return true;
                }


                MarkerOptions markerOpts =
                        new MarkerOptions().position(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()));

                markerOpts =
                        markerOpts.title("New issue, click to edit")
                                .snippet("Drag to change location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                .draggable(true);


                Marker marker = map.getMap().addMarker(markerOpts);


                marker.showInfoWindow();

                updateZoom(new LatLng(myLoc.getLatitude(), myLoc.getLongitude()));

                selectedObjectPosition = -1;
                Const.isNewIssue = true;

                return true;

            }
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent i = new Intent(MainActivity.this, IssueActivity.class);

        final ParseGeoPoint myPoint = new ParseGeoPoint(marker.getPosition().latitude,
                marker.getPosition().longitude);

        if (selectedObjectPosition == -1) {
            CURRENT_ISSUE = new Issue();
            CURRENT_ISSUE.setLocation(myPoint);
            ParseACL acl = new ParseACL();
            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setWriteAccess(ParseUser.getCurrentUser(), true);
            CURRENT_ISSUE.setACL(acl);

            CURRENT_ISSUE.setAuthor(ParseUser.getCurrentUser());
            CURRENT_ISSUE.setDonation(0);
            CURRENT_ISSUE.setParticipants(0);
            marker.remove();
            Const.isNewIssue = false;

        }

//        mIssueAdapter.getI

        startActivity(i);


    }

    /*
     * Define a DialogFragment to display the error dialog generated in showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /*
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}
