package com.dianwoba.rctamap.search;

import android.content.Context;

import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

/**
 * Created by marshal on 16/6/7.
 */
class MyDistanceSearch extends AMapSearch implements DistanceSearch.OnDistanceSearchListener {
    public DistanceSearch distanceSearch;

    public MyDistanceSearch(Context context, String requestId) {
        distanceSearch = new DistanceSearch(context);
        distanceSearch.setDistanceSearchListener(this);
        this.setRequestId(requestId);
    }

    @Override
    public void onDistanceSearched(DistanceResult distanceResult, int resultId) {
        if (1000 != resultId) {
            this.sendEventWithError("request distance error");
            return;
        }

        WritableArray arrray = Arguments.createArray();

        List<DistanceItem> distanceItems = distanceResult.getDistanceResults();
        for (DistanceItem distanceItem:distanceItems
             ) {
            WritableMap map = Arguments.createMap();
            map.putInt("distId", distanceItem.getDestId());
            map.putDouble("distance", distanceItem.getDistance());
            map.putDouble("duration", distanceItem.getDuration());
            map.putInt("code", distanceItem.getErrorCode());
            map.putString("info", distanceItem.getErrorInfo());
            map.putInt("originId", distanceItem.getOriginId());
            arrray.pushMap(map);
        }

        this.sendEventWithData(arrray);
    }
}
