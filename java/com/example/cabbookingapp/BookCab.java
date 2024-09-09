package com.example.cabbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BookCab extends FragmentActivity implements OnMapReadyCallback {


    Location currentLocation;
    private Polyline polyline;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    FusedLocationProviderClient fusedClient;

    private static final int REQUEST_CODE = 101;
    FrameLayout map;
    GoogleMap gMap;
    Marker marker;
    SearchView searchView;
    private Button proceedButton;
    private double distance;
    private DatabaseReference usersRef;
    GoogleMap googleMap;
    private DirectionsApiRequest directionsRequest;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cab);
        gMap=googleMap;
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("driver");
        map = findViewById(R.id.map);
        proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setVisibility(View.GONE);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the desired action when the button is clicked
                showSuccessDialog();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = searchView.getQuery().toString();
                if (isValidSearchResult(loc)) {
                    proceedButton.setVisibility(View.VISIBLE); // Show the button
                } else {
                    Toast.makeText(BookCab.this, "Invalid search result", Toast.LENGTH_SHORT).show();
                }

                if (loc == null) {
                    Toast.makeText(BookCab.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(BookCab.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
                        if (addressList.size() > 0) {
                            for (Address address : addressList) {
                                Log.d("Address", address.toString());
                            }
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                            if (marker != null) {
                                marker.remove();
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                            gMap.animateCamera(cameraUpdate);
                            marker = gMap.addMarker(markerOptions);
                            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            LatLng destinationLatLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

                            distance = calculateDistance(currentLatLng, destinationLatLng);
                            saveUserData(loc);
                            drawPolyline(currentLatLng, destinationLatLng);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

    }
    private void drawPolyline(LatLng currentLatLng, LatLng destinationLatLng){
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyAmZFb1NObA0AWrUx3dJzu6IFDheo2vpuI") // Replace with your own API key
                .build();

        // Request the directions from the Directions API asynchronously
        directionsRequest = DirectionsApi.newRequest(geoApiContext)// Set the travel mode
                .origin(new com.google.maps.model.LatLng(currentLatLng.latitude, currentLatLng.longitude))
                .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude));

        directionsRequest.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Process the directions result
                if (result != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];
                    String encodedPolyline = route.overviewPolyline.getEncodedPath();

                    // Decode the polyline coordinates
                    List<LatLng> decodedPolyline = PolyUtil.decode(encodedPolyline);

                    // Create and configure the PolylineOptions
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.add(currentLatLng, destinationLatLng);
                    polylineOptions.addAll(decodedPolyline);
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(5f);

                    // Remove previous polyline if it exists
                    if (polyline != null) {
                        polyline.remove();
                    }

                    // Add the polyline to the map
                    polyline = gMap.addPolyline(polylineOptions);

                    // Move the camera to show the entire polyline
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng point : decodedPolyline) {
                        builder.include(point);
                    }
                    builder.include(currentLatLng);
                    builder.include(destinationLatLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                    gMap.moveCamera(cameraUpdate);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the API request failure
                e.printStackTrace();
            }
        });
    }

    private double calculateDistance(LatLng start, LatLng end) {
        double earthRadius = 6371; // Radius of the Earth in kilometers
        double latDiff = Math.toRadians(end.latitude - start.latitude);
        double lngDiff = Math.toRadians(end.longitude - start.longitude);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(start.latitude)) * Math.cos(Math.toRadians(end.latitude))
                * Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return distance;// Distance in kilometers
    }
    private double calculateFare(double distance) {
        double ratePerKilometer = 10.0;// Define your rate per kilometer
        double basefare=25.0;
        float fare = (float) (basefare+(ratePerKilometer * distance));

        // Add any additional charges or calculations based on your fare rules

        return fare;
    }



    private void showPaymentDialog(float fare){
        ConstraintLayout paymentConstraintLayout=findViewById(R.id.paymentConstraintLayout);
        AlertDialog.Builder builder = new AlertDialog.Builder(BookCab.this);
        View paymentView = LayoutInflater.from(BookCab.this).inflate(R.layout.payment_dialog, paymentConstraintLayout);
        TextView fareTextView = paymentView.findViewById(R.id.fare2);
        fareTextView.setText("Total Fare: " + fare + " RS");
        builder.setView(paymentView);
        AlertDialog paymentDialog = builder.create();
        paymentDialog.show();// Update the TextView with the calculated fare
        Button pay = paymentView.findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PaymentActivity
                Intent intent = new Intent(BookCab.this, PaymentActivity.class);
                intent.putExtra("fare", fare); // Pass the fare value to PaymentActivity if needed
                startActivity(intent);
                saveUserData(fare);

                // Dismiss the payment dialog
                paymentDialog.dismiss();
            }
        });

    }
    private void saveUserData(float fare){
        if(currentUser!=null){
            String userId=currentUser.getUid();
            DatabaseReference userRef=usersRef.child(userId);
            userRef.child("fare").setValue(fare);

        }
    }
    private void saveUserData(String loc){
        if(currentUser!=null){
            String userId=currentUser.getUid();
            DatabaseReference userRef=usersRef.child(userId);
            userRef.child("destination").setValue(loc);

        }
    }

    private void showSuccessDialog(){
        double fare=calculateFare(distance);
        ConstraintLayout successConstraintLayout=findViewById(R.id.successConstraintLayout);
        View view= LayoutInflater.from(BookCab.this).inflate(R.layout.success_dialog,successConstraintLayout);
        TextView successDesc=view.findViewById(R.id.successDesc);
        TextView car=view.findViewById(R.id.car);

        Button successDone=view.findViewById(R.id.successDone);


        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String carno = dataSnapshot.child("carno").getValue(String.class);
                        successDesc.setText("Driver Name:"+name);
                        car.setText("Car No:" + carno);
                    }
                }
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(BookCab.this);
        builder.setView(view);
        final AlertDialog alertDialog=builder.create();

        successDone.findViewById(R.id.successDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showPaymentDialog((float) fare);

            }
        });
        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }



    private boolean isValidSearchResult(String loc) {
        // Implement your validation logic here
        // You can check the search result against your desired conditions
        // For example, you can check if the search result is not empty or meets certain criteria

        return !TextUtils.isEmpty(loc);
    }
    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;

        }
        Task<Location> task=fusedClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLocation=location;
                    SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment!=null;
                    supportMapFragment.getMapAsync(BookCab.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap=googleMap;
        LatLng latLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("My Current Location");
        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        gMap.addMarker(markerOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }
}
