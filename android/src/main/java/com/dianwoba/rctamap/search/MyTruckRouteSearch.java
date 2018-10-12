package com.dianwoba.rctamap.search;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TruckPath;
import com.amap.api.services.route.TruckRouteRestult;
import com.amap.api.services.route.TruckStep;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

/**
 * Created by marshal on 16/6/7.
 */
class MyTruckRouteSearch extends AMapSearch implements RouteSearch.OnTruckRouteSearchListener {
    public RouteSearch truckRouteSearch;

    public MyTruckRouteSearch(Context context, String requestId) {
        truckRouteSearch = new RouteSearch(context);
        truckRouteSearch.setOnTruckRouteSearchListener(this);
        this.setRequestId(requestId);
    }

    @Override
    public void onTruckRouteSearched(TruckRouteRestult truckRouteRestult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }

        WritableArray arrray = Arguments.createArray();

        WritableMap map = Arguments.createMap();

        WritableMap origin = Arguments.createMap();
        origin.putDouble("latitude", truckRouteRestult.getStartPos().getLatitude());
        origin.putDouble("longitude", truckRouteRestult.getStartPos().getLongitude());
        map.putMap("origin", origin);

        WritableMap destination = Arguments.createMap();
        destination.putDouble("latitude", truckRouteRestult.getTargetPos().getLatitude());
        destination.putDouble("longitude", truckRouteRestult.getTargetPos().getLongitude());
        map.putMap("destination", destination);

        if (truckRouteRestult.getPaths() != null) {
            WritableArray paths = Arguments.createArray();

            for (TruckPath path: truckRouteRestult.getPaths()
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

                for (TruckStep step: path.getSteps()
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
}
