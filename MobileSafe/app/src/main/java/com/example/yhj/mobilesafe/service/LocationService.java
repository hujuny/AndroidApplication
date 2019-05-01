package com.example.yhj.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import static android.location.Criteria.ACCURACY_FINE;

public class LocationService extends Service {

    private LocationManager lm;
    private SharedPreferences mPref;
    private LocationListener listener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria ct = new Criteria();//标准
        ct.setCostAllowed(true);//允许付费
        ct.setAccuracy(Criteria.ACCURACY_FINE);//精确度良好
        String bestProvider = lm.getBestProvider(ct, true);//设置最好的提供者
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);//参数一位置提供者，参数二最短更新时间，参数三最短更新距离
        super.onCreate();
    }


    private class MyLocationListener implements LocationListener {

        @Override//位置发生变化
        public void onLocationChanged(Location location) {
            //将经纬度保存在sp中
            mPref.edit().putString("location","j:"+location.getLongitude()+";w:"+location.getLatitude()).apply();
            stopSelf();//停掉服务
        }

        @Override//位置提供者发生变化
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override//用户打开gps
        public void onProviderEnabled(String provider) {

        }

        @Override//用户关闭gps
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(listener);//当activity销毁时，停止更新位置，节省电量
        super.onDestroy();
    }
}
