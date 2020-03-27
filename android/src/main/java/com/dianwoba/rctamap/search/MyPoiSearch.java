package com.dianwoba.rctamap.search;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.Photo;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

public class MyPoiSearch extends AMapSearch implements PoiSearch.OnPoiSearchListener {
  public PoiSearch poiSearch;

  public MyPoiSearch(Context context, String requestId, PoiSearch.Query query) {
    poiSearch = new PoiSearch(context, query);
    poiSearch.setOnPoiSearchListener(this);
    this.setRequestId(requestId);
  }

  @Override
  public void onPoiSearched(PoiResult poiResult, int resultId) {
    if (1000 != resultId) {
      this.sendEventWithError("request regeocode error");
      return;
    }
    WritableArray array = Arguments.createArray();

    for(PoiItem result: poiResult.getPois()) {
      WritableMap poi = Arguments.createMap();
      poi.putString("uid", result.getPoiId());
      poi.putString("name", result.getTitle());
      poi.putString("type", result.getTypeDes());
      poi.putString("typecode", result.getTypeCode());
      WritableMap location = Arguments.createMap();
      location.putDouble("longitude", result.getLatLonPoint().getLongitude());
      location.putDouble("latitude", result.getLatLonPoint().getLatitude());
      poi.putMap("location", location);
      poi.putString("address", result.getSnippet());
      poi.putString("tel", result.getTel());
      poi.putDouble("distance", result.getDistance());
      poi.putString("parkingType", result.getParkingType());
      poi.putString("shopID", result.getShopID());
      poi.putString("postcode", result.getPostcode());
      poi.putString("website", result.getWebsite());
      poi.putString("email", result.getEmail());
      poi.putString("province", result.getProvinceName());
      poi.putString("pcode", result.getProvinceCode());
      poi.putString("city", result.getCityName());
      poi.putString("citycode", result.getCityCode());
      poi.putString("district", result.getAdName());
      poi.putString("adcode", result.getAdCode());
      if (result.getEnter() != null) {
        WritableMap enterLocation = Arguments.createMap();
        enterLocation.putDouble("longitude", result.getEnter().getLongitude());
        enterLocation.putDouble("latitude", result.getEnter().getLatitude());
        poi.putMap("enterLocation", enterLocation);
      }
      LatLonPoint exit = result.getExit();
      if (exit != null) {
        WritableMap exitLocation = Arguments.createMap();
        exitLocation.putDouble("longitude", result.getExit().getLongitude());
        exitLocation.putDouble("latitude", result.getExit().getLatitude());
        poi.putMap("exitLocation", exitLocation);
      }
      poi.putString("direction", result.getDirection());
      poi.putBoolean("hasIndoorMap", result.isIndoorMap());
      poi.putString("businessArea", result.getBusinessArea());

      WritableArray images = Arguments.createArray();
      for(Photo photo: result.getPhotos()) {
        WritableMap pp = Arguments.createMap();
        pp.putString("title", photo.getTitle());
        pp.putString("url", photo.getUrl());
        images.pushMap(pp);
      }
      poi.putArray("images", images);

      array.pushMap(poi);
    }

    this.sendEventWithData(array);
  }

  @Override
  public void onPoiItemSearched(PoiItem poiItem, int resultId) {

  }
}
