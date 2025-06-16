package com.trailverse.app;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.trailverse.app.model.HikingSessionRequestDto;
import com.trailverse.app.model.PathPointDto;
import com.trailverse.app.network.ApiClient;
import com.trailverse.app.network.ApiService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartHikingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private List<LatLng> pathPoints = new ArrayList<>();
    private float totalDistance = 0f;
    private long startTime = 0;
    private boolean isTracking = false;
    private long endTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_hiking, container, false);

        locationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }

        ImageButton btnMyLocation = view.findViewById(R.id.btn_my_location);
        Button btnStartHiking = view.findViewById(R.id.btn_start_hiking);
        Button btnStopHiking = view.findViewById(R.id.btn_stop_hiking);

        btnMyLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null && mMap != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 18f));
                    }
                });
            }
        });

        btnStartHiking.setOnClickListener(startView -> {
            if (!isTracking) {
                startTime = System.currentTimeMillis();
                startLocationUpdates();
                isTracking = true;
                btnStopHiking.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "등산을 시작합니다!", Toast.LENGTH_SHORT).show();
            }
        });

        btnStopHiking.setOnClickListener(stopView -> {
            if (isTracking) {
                endTime = System.currentTimeMillis();
                isTracking = false;
                locationClient.removeLocationUpdates(locationCallback);

                long durationMillis = endTime - startTime;
                long minutes = (durationMillis / 1000) / 60;
                float distance = totalDistance;

                SharedPreferences prefs = requireContext().getSharedPreferences("TrailversePrefs", Context.MODE_PRIVATE);
                String userId = prefs.getString("userId", "20230919");

                showHikingSummaryDialog(userId, minutes, distance);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        LatLng defaultPos = new LatLng(35.1432, 129.0089);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPos, 15f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create().setInterval(3000).setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                if (result == null) return;
                Location location = result.getLastLocation();
                LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());

                if (!pathPoints.isEmpty()) {
                    LatLng last = pathPoints.get(pathPoints.size() - 1);
                    float[] distance = new float[1];
                    Location.distanceBetween(last.latitude, last.longitude, newPoint.latitude, newPoint.longitude, distance);
                    totalDistance += distance[0] / 1000f;
                }
                pathPoints.add(newPoint);
                if (mMap != null) {
                    mMap.clear();
                    mMap.addPolyline(new PolylineOptions().addAll(pathPoints).width(10));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationClient != null && locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void showHikingSummaryDialog(String userId, long minutes, float distanceKm) {
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_hiking_summary, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(bottomSheetView);

        TextView tvUser = bottomSheetView.findViewById(R.id.tv_summary_user);
        TextView tvTime = bottomSheetView.findViewById(R.id.tv_summary_time);
        TextView tvDistance = bottomSheetView.findViewById(R.id.tv_summary_distance);
        Button btnSave = bottomSheetView.findViewById(R.id.btn_save_hiking);

        tvUser.setText("사용자: " + userId);
        tvTime.setText("소요 시간: " + minutes + "분");
        tvDistance.setText("이동 거리: " + String.format("%.2f", distanceKm) + "km");

        btnSave.setOnClickListener(v -> {
            String startTimeStr = formatToIso(LocalDateTime.ofEpochSecond(startTime / 1000, 0, java.time.ZoneOffset.UTC));
            String endTimeStr = formatToIso(LocalDateTime.ofEpochSecond(endTime / 1000, 0, java.time.ZoneOffset.UTC));

            List<PathPointDto> pathList = new ArrayList<>();
            for (LatLng point : pathPoints) {
                pathList.add(new PathPointDto(point.latitude, point.longitude));
            }

            HikingSessionRequestDto dto = new HikingSessionRequestDto(userId, startTimeStr, endTimeStr, distanceKm, pathList);
            ApiService apiService = ApiClient.getApiService();
            apiService.saveHikingSession(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "DB에 저장됨", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "저장 실패 (응답 오류)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "저장 실패 (네트워크 오류)", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        dialog.show();
    }

    private String formatToIso(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
