package com.dianwoba.rctamap.search;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by marshal on 16/6/6.
 */
public class AMapSearchManager extends ReactContextBaseJavaModule {
    private ReactContext reactContext;

    public AMapSearchManager(ReactApplicationContext rContext) {
        super(rContext);
        reactContext = rContext;
    }

    @Override
    public String getName() {
        return "AMapSearchManager";
    }

    @ReactMethod
    public void inputTipsSearch(String requestId, String keys, String city) {
        InputtipsQuery inputtipsQuery = new InputtipsQuery(keys, city);
        MyInputtips request = new MyInputtips(reactContext, requestId);
        request.reactContext = reactContext;

        request.inputTips.setQuery(inputtipsQuery);
        request.inputTips.requestInputtipsAsyn();
    }

    @ReactMethod
    public void weatherSearch(String requestId, String city, Boolean isLive) {
        WeatherSearchQuery query = new WeatherSearchQuery(city,
                isLive? WeatherSearchQuery.WEATHER_TYPE_LIVE: WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        MyWeatherSearch request = new MyWeatherSearch(reactContext, requestId);
        request.reactContext = reactContext;

        request.weatherSearch.setQuery(query);
        request.weatherSearch.searchWeatherAsyn();
    }

    @ReactMethod
    public void geocodeSearch(String requestId, String address, String city) {
        MyGeocodeSearch request = new MyGeocodeSearch(reactContext, requestId);
        request.reactContext = reactContext;
        GeocodeQuery query = new GeocodeQuery(address, city);

        request.geocodeSearch.getFromLocationNameAsyn(query);
    }

    @ReactMethod
    public void regeocodeSearch(String requestId, ReadableMap latlon, Float radius) {
        MyGeocodeSearch request = new MyGeocodeSearch(reactContext, requestId);
        request.reactContext = reactContext;
        LatLonPoint point = new LatLonPoint(latlon.getDouble("latitude"), latlon.getDouble("longitude"));
        RegeocodeQuery query = new RegeocodeQuery(point, radius != null?radius:1000, GeocodeSearch.AMAP);

        request.geocodeSearch.getFromLocationAsyn(query);
    }

    @ReactMethod
    public void distanceSearch(String requestId, ReadableArray latLonPoints, ReadableMap dest, int searchType ) {
        MyDistanceSearch request = new MyDistanceSearch(reactContext, requestId);
        request.reactContext = reactContext;
        List<LatLonPoint> latLonPoints1 = new ArrayList<>();
        for (int ii = 0; ii < latLonPoints.size(); ii ++) {
            ReadableMap latLon = latLonPoints.getMap(ii);
            latLonPoints1.add(new LatLonPoint(latLon.getDouble("latitude"), latLon.getDouble("longitude")));
        }
        DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        distanceQuery.setOrigins(latLonPoints1);
        LatLonPoint destLatLon = new LatLonPoint(dest.getDouble("latitude"), dest.getDouble("longitude"));
        distanceQuery.setDestination(destLatLon);
        distanceQuery.setType(searchType);
        request.distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
    }

    @ReactMethod
    public void truckRouteSearch(String requestId, ReadableMap origin, ReadableMap destination, int strategy, ReadableMap options ) {
        MyTruckRouteSearch request = new MyTruckRouteSearch(reactContext, requestId);
        request.reactContext = reactContext;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(origin.getDouble("latitude"), origin.getDouble("longitude")), new LatLonPoint(destination.getDouble("latitude"), destination.getDouble("longitude")));
        int truckSize = RouteSearch.TRUCK_SIZE_LIGHT;
        if (options.hasKey("size")) {
            truckSize = options.getInt("size");
        }
        if (options.hasKey("plateProvince")) {
            fromAndTo.setPlateProvince(options.getString("plateProvince"));
        }
        if (options.hasKey("plateNumber")) {
            fromAndTo.setPlateNumber(options.getString("plateNumber"));
        }
        RouteSearch.TruckRouteQuery truckRouteQuery = new RouteSearch.TruckRouteQuery(fromAndTo, strategy, null, truckSize);
        if (options.hasKey("height")) {
            truckRouteQuery.setTruckHeight((float)options.getDouble("height"));
        }
        if (options.hasKey("width")) {
            truckRouteQuery.setTruckWidth((float)options.getDouble("width"));
        }
        if (options.hasKey("load")) {
            truckRouteQuery.setTruckLoad((float)options.getDouble("load"));
        }
        if (options.hasKey("weight")) {
            truckRouteQuery.setTruckWeight((float)options.getDouble("weight"));
        }
        if (options.hasKey("axis")) {
            truckRouteQuery.setTruckAxis((float)options.getDouble("axis"));
        }
        request.truckRouteSearch.calculateTruckRouteAsyn(truckRouteQuery);
    }

    @ReactMethod
    public void drivingRouteSearch(String requestId, ReadableMap origin, ReadableMap destination, int strategy) {
        MyDrivingRouteSearch request = new MyDrivingRouteSearch(reactContext, requestId);
        request.reactContext = reactContext;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(origin.getDouble("latitude"), origin.getDouble("longitude")), new LatLonPoint(destination.getDouble("latitude"), destination.getDouble("longitude")));

        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, strategy, null, null, "");
        request.routeSearch.calculateDriveRouteAsyn(query);
    }
}
