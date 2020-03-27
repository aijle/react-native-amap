package com.dianwoba.rctamap.search;

import android.content.Context;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.Doorway;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.Railway;
import com.amap.api.services.route.RailwaySpace;
import com.amap.api.services.route.RailwayStationItem;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RideStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteBusWalkItem;
import com.amap.api.services.route.RouteRailwayItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TaxiItem;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.text.SimpleDateFormat;

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

      WritableArray arrray = Arguments.createArray();

      WritableMap map = Arguments.createMap();

      WritableMap origin = Arguments.createMap();
      origin.putDouble("latitude", busRouteResult.getStartPos().getLatitude());
      origin.putDouble("longitude", busRouteResult.getStartPos().getLongitude());
      map.putMap("origin", origin);

      WritableMap destination = Arguments.createMap();
      destination.putDouble("latitude", busRouteResult.getTargetPos().getLatitude());
      destination.putDouble("longitude", busRouteResult.getTargetPos().getLongitude());
      map.putMap("destination", destination);

      if (busRouteResult.getPaths() != null) {
        WritableArray paths = Arguments.createArray();

        for (BusPath path: busRouteResult.getPaths()
        ) {
          WritableMap pathw = Arguments.createMap();

          pathw.putDouble("distance", path.getDistance());
          pathw.putDouble("duration", path.getDuration());
          pathw.putDouble("cost", path.getCost());
          pathw.putDouble("walkingDistance", path.getWalkDistance());

          WritableArray segments = Arguments.createArray();

          for (BusStep segment: path.getSteps()
          ) {
            WritableMap segmentw = Arguments.createMap();

            WritableArray busLines = Arguments.createArray();
            for(RouteBusLineItem busLineItem: segment.getBusLines()) {
              WritableMap pw = Arguments.createMap();

              WritableMap arrivalBusStation = Arguments.createMap();
              BusStationItem arrival = busLineItem.getArrivalBusStation();
              arrivalBusStation.putString("uid", arrival.getBusStationId());
              arrivalBusStation.putString("name", arrival.getBusStationName());
              WritableMap al = Arguments.createMap();
              LatLonPoint ap = arrival.getLatLonPoint();
              al.putDouble("latitude", ap.getLatitude());
              al.putDouble("longitude", ap.getLongitude());
              arrivalBusStation.putMap("location", al);
              pw.putMap("arrivalStop", arrivalBusStation);

              WritableMap departureBusStation = Arguments.createMap();
              BusStationItem departure = busLineItem.getDepartureBusStation();
              departureBusStation.putString("uid", departure.getBusStationId());
              departureBusStation.putString("name", departure.getBusStationName());
              WritableMap dl = Arguments.createMap();
              LatLonPoint dp = departure.getLatLonPoint();
              dl.putDouble("latitude", dp.getLatitude());
              dl.putDouble("longitude", dp.getLongitude());
              departureBusStation.putMap("location", dl);
              pw.putMap("departureStop", departureBusStation);

              WritableArray passStations = Arguments.createArray();
              for(BusStationItem passStation: busLineItem.getPassStations()) {
                WritableMap busStation = Arguments.createMap();
                busStation.putString("uid", passStation.getBusStationId());
                busStation.putString("name", passStation.getBusStationName());
                WritableMap pl = Arguments.createMap();
                LatLonPoint pp = passStation.getLatLonPoint();
                pl.putDouble("latitude", pp.getLatitude());
                pl.putDouble("longitude", pp.getLongitude());
                busStation.putMap("location", pl);

                passStations.pushMap(busStation);
              }
              pw.putArray("viaBusStops", passStations);

              pw.putDouble("distance", busLineItem.getDistance());
              pw.putDouble("duration", busLineItem.getDuration());

              WritableArray polyline = Arguments.createArray();
              for (LatLonPoint p: busLineItem.getPolyline()
              ) {
                WritableMap pww = Arguments.createMap();

                pww.putDouble("latitude", p.getLatitude());
                pww.putDouble("longitude", p.getLongitude());

                polyline.pushMap(pww);
              }
              pw.putArray("polyline", polyline);

              pw.putString("uid", busLineItem.getBusLineId());
              pw.putString("name", busLineItem.getBusLineName());
              pw.putString("type", busLineItem.getBusLineType());

              busLines.pushMap(pw);
            }
            segmentw.putArray("buslines", busLines);

            Doorway entranceD = segment.getEntrance();
            if (entranceD != null) {
              WritableMap el = Arguments.createMap();
              LatLonPoint elp = entranceD.getLatLonPoint();
              el.putDouble("latitude", elp.getLatitude());
              el.putDouble("longitude", elp.getLongitude());

              segmentw.putString("enterName", entranceD.getName());
              segmentw.putMap("enterLocation", el);
            }

            Doorway exitD = segment.getExit();
            if (exitD != null) {
              WritableMap el = Arguments.createMap();
              LatLonPoint elp = exitD.getLatLonPoint();
              el.putDouble("latitude", elp.getLatitude());
              el.putDouble("longitude", elp.getLongitude());

              segmentw.putString("exitName", exitD.getName());
              segmentw.putMap("exitLocation", el);
            }

            RouteRailwayItem railwayI = segment.getRailway();
            if (railwayI != null) {
              WritableMap railway = Arguments.createMap();
              railway.putDouble("distance", railwayI.getDistance());
              railway.putString("time", railwayI.getTime());
              railway.putString("trip", railwayI.getTrip());
              railway.putString("type", railwayI.getType());

              WritableArray alters = Arguments.createArray();
              for (Railway rl :
                railwayI.getAlters()) {
                WritableMap alter = Arguments.createMap();
                alter.putString("name", rl.getName());
                alter.putString("id", rl.getID());
                alters.pushMap(alter);
              }
              railway.putArray("alters", alters);

              WritableArray spaces = Arguments.createArray();
              for (RailwaySpace rs : railwayI.getSpaces()) {
                WritableMap space = Arguments.createMap();
                space.putString("code", rs.getCode());
                space.putDouble("cost", rs.getCost());
                spaces.pushMap(space);
              }
              railway.putArray("spaces", spaces);

              WritableArray viaStops = Arguments.createArray();
              for (RailwayStationItem vs : railwayI.getViastops()) {
                WritableMap vsm = Arguments.createMap();
                vsm.putString("adcode", vs.getAdcode());
                vsm.putString("id", vs.getID());
                vsm.putString("name", vs.getName());
                vsm.putString("time", vs.getTime());
                vsm.putDouble("wait", vs.getWait());
                vsm.putBoolean("isEnd", vs.isEnd());
                vsm.putBoolean("isStart", vs.isStart());
                WritableMap vsl = Arguments.createMap();
                vsl.putDouble("latitude", vs.getLocation().getLatitude());
                vsl.putDouble("longitude", vs.getLocation().getLongitude());
                vsm.putMap("location", vsl);
                viaStops.pushMap(vsm);
              }
              railway.putArray("viaStops", viaStops);

              WritableMap departure = Arguments.createMap();
              departure.putString("adcode", railwayI.getDeparturestop().getAdcode());
              departure.putString("id", railwayI.getDeparturestop().getID());
              departure.putString("name", railwayI.getDeparturestop().getName());
              departure.putString("time", railwayI.getDeparturestop().getTime());
              departure.putDouble("wait", railwayI.getDeparturestop().getWait());
              departure.putBoolean("isEnd", railwayI.getDeparturestop().isEnd());
              departure.putBoolean("isStart", railwayI.getDeparturestop().isStart());
              WritableMap dl = Arguments.createMap();
              dl.putDouble("latitude", railwayI.getDeparturestop().getLocation().getLatitude());
              dl.putDouble("longitude", railwayI.getDeparturestop().getLocation().getLongitude());
              departure.putMap("location", dl);
              railway.putMap("departureStation", departure);

              WritableMap arrival = Arguments.createMap();
              arrival.putString("adcode", railwayI.getArrivalstop().getAdcode());
              arrival.putString("id", railwayI.getArrivalstop().getID());
              arrival.putString("name", railwayI.getArrivalstop().getName());
              arrival.putString("time", railwayI.getArrivalstop().getTime());
              arrival.putDouble("wait", railwayI.getArrivalstop().getWait());
              arrival.putBoolean("isEnd", railwayI.getArrivalstop().isEnd());
              arrival.putBoolean("isStart", railwayI.getArrivalstop().isStart());
              WritableMap al = Arguments.createMap();
              al.putDouble("latitude", railwayI.getArrivalstop().getLocation().getLatitude());
              al.putDouble("longitude", railwayI.getArrivalstop().getLocation().getLongitude());
              arrival.putMap("location", al);
              railway.putMap("arrivalStation", arrival);

              segmentw.putMap("railway", railway);
            }

            TaxiItem taxiI = segment.getTaxi();
            if (taxiI != null) {
                WritableMap taxi = Arguments.createMap();
                taxi.putDouble("duration", taxiI.getDuration());
                taxi.putDouble("distance", taxiI.getDistance());
                taxiI.getOrigin();
                WritableMap origin1 = Arguments.createMap();
                origin1.putDouble("latitude", taxiI.getOrigin().getLatitude());
                origin1.putDouble("longitude", taxiI.getOrigin().getLongitude());
                taxi.putMap("origin", origin1);

                WritableMap destination1 = Arguments.createMap();
                destination1.putDouble("latitude", taxiI.getDestination().getLatitude());
                destination1.putDouble("longitude", taxiI.getDestination().getLongitude());
                taxi.putMap("destination", destination1);

                segmentw.putMap("taxi", taxi);
            }

            RouteBusWalkItem walkItem = segment.getWalk();
            if (walkItem != null) {
              WritableMap walking = Arguments.createMap();
              walking.putDouble("distance", walkItem.getDistance());
              walking.putDouble("duration", walkItem.getDuration());

              WritableMap origin1 = Arguments.createMap();
              origin1.putDouble("latitude", walkItem.getOrigin().getLatitude());
              origin1.putDouble("longitude", walkItem.getOrigin().getLongitude());
              walking.putMap("origin", origin1);

              WritableMap destination1 = Arguments.createMap();
              destination1.putDouble("latitude", walkItem.getDestination().getLatitude());
              destination1.putDouble("longitude", walkItem.getDestination().getLongitude());
              walking.putMap("destination", destination1);

              WritableArray wsteps = Arguments.createArray();
              for(WalkStep walkStep: walkItem.getSteps()) {
                WritableMap wstep = Arguments.createMap();

                wstep.putString("action", walkStep.getAction());
                wstep.putString("assistantAction", walkStep.getAssistantAction());
                wstep.putDouble("distance", walkStep.getDistance());
                wstep.putDouble("duration", walkStep.getDuration());
                wstep.putString("instruction", walkStep.getInstruction());
                wstep.putString("orientation", walkStep.getOrientation());
                wstep.putString("road", walkStep.getRoad());

                WritableArray polylines = Arguments.createArray();

                for (LatLonPoint p: walkStep.getPolyline()
                ) {
                  WritableMap pw = Arguments.createMap();

                  pw.putDouble("latitude", p.getLatitude());
                  pw.putDouble("longitude", p.getLongitude());

                  polylines.pushMap(pw);
                }

                wstep.putArray("polyline", polylines);
                //tmcs
                //cities

                wsteps.pushMap(wstep);
              }
              walking.putArray("steps", wsteps);

              segmentw.putMap("walking", walking);
            }
            segments.pushMap(segmentw);
          }

          pathw.putArray("segments", segments);

          paths.pushMap(pathw);
        }

        map.putArray("transits", paths);
      }

      arrray.pushMap(map);
      this.sendEventWithData(arrray);
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

      WritableArray arrray = Arguments.createArray();

      WritableMap map = Arguments.createMap();

      WritableMap origin = Arguments.createMap();
      origin.putDouble("latitude", walkRouteResult.getStartPos().getLatitude());
      origin.putDouble("longitude", walkRouteResult.getStartPos().getLongitude());
      map.putMap("origin", origin);

      WritableMap destination = Arguments.createMap();
      destination.putDouble("latitude", walkRouteResult.getTargetPos().getLatitude());
      destination.putDouble("longitude", walkRouteResult.getTargetPos().getLongitude());
      map.putMap("destination", destination);

      if (walkRouteResult.getPaths() != null) {
        WritableArray paths = Arguments.createArray();

        for (WalkPath path: walkRouteResult.getPaths()
        ) {
          WritableMap pathw = Arguments.createMap();

          pathw.putDouble("distance", path.getDistance());
          pathw.putDouble("duration", path.getDuration());

          WritableArray steps = Arguments.createArray();

          for (WalkStep step: path.getSteps()
          ) {
            WritableMap stepw = Arguments.createMap();

            stepw.putString("action", step.getAction());
            stepw.putString("assistantAction", step.getAssistantAction());
            stepw.putDouble("distance", step.getDistance());
            stepw.putDouble("duration", step.getDuration());
            stepw.putString("instruction", step.getInstruction());
            stepw.putString("orientation", step.getOrientation());
            stepw.putString("road", step.getRoad());

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
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }

      WritableArray arrray = Arguments.createArray();

      WritableMap map = Arguments.createMap();

      WritableMap origin = Arguments.createMap();
      origin.putDouble("latitude", rideRouteResult.getStartPos().getLatitude());
      origin.putDouble("longitude", rideRouteResult.getStartPos().getLongitude());
      map.putMap("origin", origin);

      WritableMap destination = Arguments.createMap();
      destination.putDouble("latitude", rideRouteResult.getTargetPos().getLatitude());
      destination.putDouble("longitude", rideRouteResult.getTargetPos().getLongitude());
      map.putMap("destination", destination);

      if (rideRouteResult.getPaths() != null) {
        WritableArray paths = Arguments.createArray();

        for (RidePath path: rideRouteResult.getPaths()
        ) {
          WritableMap pathw = Arguments.createMap();

          pathw.putDouble("distance", path.getDistance());
          pathw.putDouble("duration", path.getDuration());

          WritableArray steps = Arguments.createArray();

          for (RideStep step: path.getSteps()
          ) {
            WritableMap stepw = Arguments.createMap();

            stepw.putString("action", step.getAction());
            stepw.putString("assistantAction", step.getAssistantAction());
            stepw.putDouble("distance", step.getDistance());
            stepw.putDouble("duration", step.getDuration());
            stepw.putString("instruction", step.getInstruction());
            stepw.putString("orientation", step.getOrientation());
            stepw.putString("road", step.getRoad());

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
