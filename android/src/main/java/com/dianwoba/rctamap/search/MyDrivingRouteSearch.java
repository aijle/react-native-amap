package com.dianwoba.rctamap.search;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by marshal on 16/6/7.
 */
class MyDrivingRouteSearch extends AMapSearch implements RouteSearch.OnRouteSearchListener {
    public RouteSearch routeSearch;

    public MyDrivingRouteSearch(Context context, String requestId) {
        routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(this);
        this.setRequestId(requestId);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }

        WritableArray arrray = Arguments.createArray();

        WritableMap map = Arguments.createMap();

        WritableMap origin = Arguments.createMap();
        origin.putDouble("latitude", driveRouteResult.getStartPos().getLatitude());
        origin.putDouble("longitude", driveRouteResult.getStartPos().getLongitude());
        map.putMap("origin", origin);

        WritableMap destination = Arguments.createMap();
        destination.putDouble("latitude", driveRouteResult.getTargetPos().getLatitude());
        destination.putDouble("longitude", driveRouteResult.getTargetPos().getLongitude());
        map.putMap("destination", destination);

        if (driveRouteResult.getPaths() != null) {
            WritableArray paths = Arguments.createArray();

            for (DrivePath path: driveRouteResult.getPaths()
                    ) {
                WritableMap pathw = Arguments.createMap();

                pathw.putDouble("distance", path.getDistance());
                pathw.putDouble("duration", path.getDuration());
                pathw.putInt("restriction", path.getRestriction());
                pathw.putString("strategy", path.getStrategy());
                pathw.putDouble("tollDistance", path.getTollDistance());
                pathw.putDouble("tolls", path.getTolls());
                pathw.putDouble("totalTrafficLights", path.getTotalTrafficlights());

                WritableArray steps = Arguments.createArray();

                for (DriveStep step: path.getSteps()
                        ) {
                    WritableMap stepw = Arguments.createMap();

                    stepw.putString("action", step.getAction());
                    stepw.putString("assistantAction", step.getAssistantAction());
                    stepw.putDouble("distance", step.getDistance());
                    stepw.putDouble("duration", step.getDuration());
                    stepw.putString("instruction", step.getInstruction());
                    stepw.putString("orientation", step.getOrientation());
                    stepw.putString("road", step.getRoad());
                    stepw.putDouble("tollDistance", step.getTollDistance());
                    stepw.putDouble("tolls", step.getTolls());
                    stepw.putString("tollRoad", step.getTollRoad());

                    WritableArray polylines = Arguments.createArray();

                    for (LatLonPoint p: step.getPolyline()
                            ) {
                        WritableMap pw = Arguments.createMap();

                        pw.putDouble("latitude", p.getLatitude());
                        pw.putDouble("longitude", p.getLongitude());

                        polylines.pushMap(pw);
                    }

                    stepw.putArray("polyline", polylines);
                    //tmcs
                    //cities

                    steps.pushMap(stepw);
                }

                pathw.putArray("steps", steps);

                paths.pushMap(pathw);
            }

            map.putArray("paths", paths);
        }

        arrray.pushMap(map);
        this.sendEventWithData(arrray);
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }
    }
}
