//
//  AMapSearchManager.m
//  RCTAMap
//
//  Created by yuanmarshal on 16/6/1.
//  Copyright © 2016年 dianowba. All rights reserved.
//

#import "AMapSearchManager.h"
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTViewManager.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapSearchKit/AMapSearchKit.h>
#import <objc/runtime.h>

#import "AMapSearchObject+RequestId.h"

@implementation AMapSearchManager
{
    AMapSearchAPI *_search;
}

-(instancetype)init
{
    _search = [[AMapSearchAPI alloc] init];
    _search.delegate = self;
    return self;
}

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"DrivingStrategy": @{
                      @"Fastest":                           @(AMapDrivingStrategyFastest), //速度最快
                      @"MinFare":                           @(AMapDrivingStrategyMinFare), //避免收费
                      @"Shortest":                          @(AMapDrivingStrategyShortest), //距离最短
                      @"NoHighways":                        @(AMapDrivingStrategyNoHighways), //不走高速
                      @"AvoidCongestion":                   @(AMapDrivingStrategyAvoidCongestion) , //躲避拥堵
                      @"AvoidHighwaysAndFare":              @(AMapDrivingStrategyAvoidHighwaysAndFare), //不走高速且避免收费
                      @"AvoidHighwaysAndCongestion":        @(AMapDrivingStrategyAvoidHighwaysAndCongestion), //不走高速且躲避拥堵
                      @"AvoidFareAndCongestion":            @(AMapDrivingStrategyAvoidFareAndCongestion), //躲避收费和拥堵
                      @"AvoidHighwaysAndFareAndCongestion": @(AMapDrivingStrategyAvoidHighwaysAndFareAndCongestion) //不走高速躲避收费和拥堵
                      }};
}

@synthesize bridge = _bridge;

//RCT_EXPORT_METHOD(poiSearchByKeywords:(NSString *)requestId options:(NSDictionary *)options)
//{
//    AMapPOIKeywordsSearchRequest *request = [[AMapPOIKeywordsSearchRequest alloc]init];
//    request.keywords = options[@"keywords"];
//    request.city = options[@"city"];
//    request.cityLimit = options[@"cityLimit"];
//    request.types = options[@"types"];
//    request.page = options[@"page"];
//    request.offset = options[@"offset"];
//    
//    [_search AMapPOIKeywordsSearch:request];
//}
//
RCT_EXPORT_METHOD(poiAroundSearch:(NSString *)requestId options:(NSDictionary *)options)
{
    AMapPOIAroundSearchRequest *request = [[AMapPOIAroundSearchRequest alloc]init];
    request.keywords = options[@"keywords"];
    request.radius = [options[@"radius"] integerValue] ;
    NSDictionary *location = options[@"location"];
    request.location = [AMapGeoPoint locationWithLatitude:[location[@"latitude"] floatValue] longitude:[location[@"longitude"] floatValue]];

    request.types = options[@"types"];
    request.page = [options[@"page"] integerValue];
    request.offset = [options[@"offset"] integerValue];
    request.requestId = requestId;

    [_search AMapPOIAroundSearch:request];
}

RCT_EXPORT_METHOD(setApiKey:(NSString *)apiKey){
    [AMapServices sharedServices].apiKey = apiKey;
}


RCT_EXPORT_METHOD(inputTipsSearch:(NSString *)requestId keys:(NSString *) keys city:(NSString *)city)
{
    AMapInputTipsSearchRequest *_tipsRequest = [[AMapInputTipsSearchRequest alloc] init];

    _tipsRequest.keywords = keys;
    _tipsRequest.city = city;
    _tipsRequest.requestId = requestId;
    [_search AMapInputTipsSearch:_tipsRequest];
}

RCT_EXPORT_METHOD(weatherSearch:(NSString *)requestId city:(NSString *)city isLive:(BOOL) isLive)
{
    AMapWeatherSearchRequest *request = [[AMapWeatherSearchRequest alloc] init];
    request.city = city;
    request.type = isLive? AMapWeatherTypeLive: AMapWeatherTypeForecast;
    request.requestId = requestId;

    [_search AMapWeatherSearch:request];
}

RCT_EXPORT_METHOD(geocodeSearch:(NSString *)requestId address:(NSString *)address city:(NSString *)city)
{
    AMapGeocodeSearchRequest *request = [[AMapGeocodeSearchRequest alloc]init];
    request.address = address;
    request.city = city;
    request.requestId = requestId;

    [_search AMapGeocodeSearch:request];
}

RCT_EXPORT_METHOD(regeocodeSearch:(NSString *)requestId location:(AMapGeoPoint *)location radius:(NSInteger)radius)
{
    AMapReGeocodeSearchRequest *request = [[AMapReGeocodeSearchRequest alloc]init];
    request.location = location;
    request.radius = radius? radius: 1000;
    request.requireExtension = NO;
    request.requestId = requestId;

    [_search AMapReGoecodeSearch:request];
}

RCT_EXPORT_METHOD(distanceSearch:(NSString *)requestId latLonPoints:(NSArray<AMapGeoPoint*> *)latLonPoints dest:(AMapGeoPoint *)dest searchType:(NSInteger)searchType)
{
    AMapDistanceSearchRequest *request = [[AMapDistanceSearchRequest alloc]init];
    request.origins = latLonPoints;
    request.destination = dest;
    request.type = searchType;
    request.requestId = requestId;

    [_search AMapDistanceSearch:request];
}

RCT_EXPORT_METHOD(walkingRouteSearch:(NSString *)requestId fromOrigin:(AMapGeoPoint *)origin to:(AMapGeoPoint *)destination)
{
    AMapWalkingRouteSearchRequest *request = [[AMapWalkingRouteSearchRequest alloc]init];
    request.requestId = requestId;
    request.origin = origin;
    request.destination = destination;
    
    [_search AMapWalkingRouteSearch:request];
    
}

RCT_EXPORT_METHOD(ridingRouteSearch:(NSString *)requestId fromOrigin:(AMapGeoPoint *)origin to:(AMapGeoPoint *)destination)
{
    AMapRidingRouteSearchRequest *request = [[AMapRidingRouteSearchRequest alloc]init];
    request.requestId = requestId;
    request.origin = origin;
    request.destination = destination;

    [_search AMapRidingRouteSearch:request];

}

RCT_EXPORT_METHOD(drivingRouteSearch:(NSString *)requestId fromOrigin:(AMapGeoPoint *)origin to:(AMapGeoPoint *)destination with:(NSInteger)strategy)
{
    AMapDrivingRouteSearchRequest *request = [[AMapDrivingRouteSearchRequest alloc]init];
    request.requestId = requestId;
    request.origin = origin;
    request.destination = destination;
    request.strategy = strategy;
    request.requireExtension = YES;

    [_search AMapDrivingRouteSearch:request];
}

RCT_EXPORT_METHOD(transitRouteSearch:(NSString *)requestId fromOrigin:(AMapGeoPoint *)origin to:(AMapGeoPoint *)destination city:(NSString*)city with:(NSInteger)strategy)
{
    AMapTransitRouteSearchRequest *request = [[AMapTransitRouteSearchRequest alloc]init];
    request.requestId = requestId;
    request.origin = origin;
    request.destination = destination;
    request.city = city;
    request.strategy = strategy;
    request.requireExtension = YES;

    [_search AMapTransitRouteSearch:request];
}

RCT_EXPORT_METHOD(truckRouteSearch:(NSString *)requestId fromOrigin:(AMapGeoPoint *)origin to:(AMapGeoPoint *)destination with:(NSInteger)strategy options:(NSDictionary*)options)
{
    AMapTruckRouteSearchRequest* request = [[AMapTruckRouteSearchRequest alloc]init];
    request.requestId = requestId;
    request.origin = origin;
    request.destination = destination;
    if (options[@"plateProvince"] != NULL)
        request.plateProvince = options[@"plateProvince"];
    if (options[@"plateNumber"] != NULL)
        request.plateNumber = options[@"plateNumber"];
    if (options[@"size"] != NULL)
        request.size = (AMapTruckSizeType)options[@"size"];
    if (options[@"height"] != NULL)
        request.height = [options[@"height"] floatValue];
    if (options[@"width"] != NULL)
        request.width = [options[@"width"] floatValue];
    if (options[@"load"] != NULL)
        request.load = [options[@"load"] floatValue];
    if (options[@"weight"] != NULL)
        request.weight = [options[@"weight"] floatValue];
    if (options[@"axis"] != NULL)
        request.axis = [options[@"axis"] integerValue];

    [_search AMapTruckRouteSearch:request];

}

//实现输入提示的回调函数
-(void)onInputTipsSearchDone:(AMapInputTipsSearchRequest*)request response:(AMapInputTipsSearchResponse *)response
{
    //通过AMapInputTipsSearchResponse对象处理搜索结果
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    if (response.tips.count != 0) {
        for (AMapTip *p in response.tips)
        {
            NSDictionary *n = [self amapTipToJson: p];
            [arr addObject:n];
        }
    }
  
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                     @"requestId":request.requestId, @"data":arr}];
}


-(NSDictionary *)amapTipToJson:(AMapTip *)tip
{
    return @{@"name":tip.name,
             @"location":@{@"latitude":@(tip.location.latitude), @"longitude":@(tip.location.longitude)},
             @"district":tip.district
             };
}

-(void)onPOISearchDone:(AMapPOISearchBaseRequest *)request response:(AMapPOISearchResponse *)response {

    NSMutableArray *arr = [[NSMutableArray alloc] init];
    [response.pois enumerateObjectsUsingBlock:^(AMapPOI * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSMutableArray* images = [[NSMutableArray alloc] initWithCapacity: obj.images.count];
        [obj.images enumerateObjectsUsingBlock:^(AMapImage * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            [images addObject:@{
                @"url": obj.url,
                @"title": obj.title
            }];
        }];

        [arr addObject:@{
            @"uid": obj.uid,
            @"name": obj.name,
            @"type": obj.type,
            @"typecode": obj.typecode,
            @"location": @{
                    @"longitude": @(obj.location.longitude),
                    @"latitude": @(obj.location.latitude)
            },
            @"address": obj.address,
            @"tel": obj.tel,
            @"distance": @(obj.distance),
            @"parkingType": obj.parkingType,
            @"shopID": obj.shopID,
            @"postcode": obj.postcode,
            @"website": obj.website,
            @"email": obj.email,
            @"province": obj.province,
            @"pcode": obj.pcode,
            @"city": obj.city,
            @"citycode": obj.citycode,
            @"district": obj.district,
            @"adcode": obj.adcode,
            @"gridcode": obj.gridcode,
            @"enterLocation": @{
                    @"longitude": @(obj.enterLocation.longitude),
                    @"latitude": @(obj.enterLocation.latitude)
            },
            @"exitLocation": @{
                    @"longitude": @(obj.exitLocation.longitude),
                    @"latitude": @(obj.exitLocation.latitude)
            },
            @"direction": obj.direction,
            @"hasIndoorMap": @(obj.hasIndoorMap),
            @"businessArea": obj.businessArea,
            @"images": images
        }];
    }];

    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
    @"requestId":request.requestId, @"data":arr}];
}
//实现天气查询的回调函数
- (void)onWeatherSearchDone:(AMapWeatherSearchRequest *)request response:(AMapWeatherSearchResponse *)response
{
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    //如果是实时天气
    if(request.type == AMapWeatherTypeLive)
    {
        if (response.lives.count != 0) {
            for (AMapLocalWeatherLive *live in response.lives) {
                [arr addObject:[self dictionaryWithPropertiesOfObject:live]];
            }
        }
    }
    //如果是预报天气
    else if(response.forecasts.count != 0)
    {
        for (AMapLocalWeatherForecast *forecast in response.forecasts) {
            [arr addObject:[self amapLocalWeatherForecastToJson:forecast]];
        }
    }
    
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":request.requestId, @"data":arr}];
}


-(NSDictionary *)amapLocalWeatherForecastToJson:(AMapLocalWeatherForecast *) forecast
{
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    for (AMapLocalDayWeatherForecast *cast in forecast.casts) {
        [arr addObject:[self dictionaryWithPropertiesOfObject:cast]];
    }
    
    return @{@"adcode": forecast.adcode,
             @"province": forecast.province,
             @"city": forecast.city,
             @"reportTime": forecast.reportTime,
             @"casts": arr
             };
}

//接受处理地理编码
-(void)onGeocodeSearchDone:(AMapGeocodeSearchRequest *)request response:(AMapGeocodeSearchResponse *)response
{
    //通过AMapGeocodeSearchResponse对象处理搜索结果
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    if(response.geocodes.count != 0)
    {
        for (AMapGeocode *gc in response.geocodes)
        {
            NSDictionary *n = @{@"formattedAddress": gc.formattedAddress,
                                @"province": gc.province,
                                @"city": gc.city,
                                @"cityCode": gc.citycode,
                                @"district": gc.district,
                                @"township": gc.township,
                                @"neighborhood": gc.neighborhood,
                                @"building": gc.building,
                                @"adcode": gc.adcode,
                                @"location": @{@"latitude": @(gc.location.latitude), @"longitude": @(gc.location.longitude)},
                                @"level": gc.level
                                };
            [arr addObject:n];
        }
    }
    
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":request.requestId, @"data":arr}];
}

//接收处理 逆地址编码
-(void)onReGeocodeSearchDone:(AMapReGeocodeSearchRequest *)request response:(AMapReGeocodeSearchResponse *)response
{
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    if(response.regeocode != nil)
    {
        //通过AMapReGeocodeSearchResponse对象处理搜索结果
        NSDictionary *n = @{
                            @"formattedAddress":response.regeocode.formattedAddress,
                            @"province": response.regeocode.addressComponent.province,
                            @"city": response.regeocode.addressComponent.city,
                            @"cityCode": response.regeocode.addressComponent.citycode,
                            @"township": response.regeocode.addressComponent.township,
                            @"neighborhood": response.regeocode.addressComponent.neighborhood,
                            @"building": response.regeocode.addressComponent.building,
                            @"district": response.regeocode.addressComponent.district
                            };
        [arr addObject:n];
    }
    
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":request.requestId, @"data":arr}];
}

-(void)onDistanceSearchDone:(AMapDistanceSearchRequest*)request response:(AMapDistanceSearchResponse*)response {
    NSMutableArray *arr = [[NSMutableArray alloc] init];

    if(response.results != nil) {
        [response.results enumerateObjectsUsingBlock:^(AMapDistanceResult * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSDictionary* n = @{
                                @"destId": @(obj.destID),
                                @"distance": @(obj.distance),
                                @"duration": @(obj.duration),
                                @"code": @(obj.code),
                                @"info": obj.info ?: [NSNull null],
                                @"originId": @(obj.originID)
                                };
            [arr addObject:n];
        }];
    }

    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":request.requestId, @"data":arr}];
}

-(NSMutableArray*)polylineFromStr:(NSString*)polyline {
    NSArray* points = [polyline componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@",;"]];
    NSMutableArray* poly = [NSMutableArray arrayWithCapacity:points.count/2];
    for (int ii = 0; ii < points.count ; ii += 2) {
        [poly addObject:@{
                          @"longitude": @([points[ii] floatValue]),
                          @"latitude": @([points[ii + 1] floatValue])
                          }];
    }
    return poly;
}

//实现路径搜索的回调函数
- (void)onRouteSearchDone:(AMapRouteSearchBaseRequest *)request response:(AMapRouteSearchResponse *)response
{
    NSMutableArray *arr = [[NSMutableArray alloc] init];
    if(response.route != nil)
    {
        NSMutableDictionary * result = [self dictionaryFromModel:response.route];
        [result[@"paths"] enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSMutableDictionary* path = obj;
            NSMutableArray* steps = path[@"steps"];
            [steps enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                NSMutableDictionary* step = obj;
                step[@"polyline"] = [self polylineFromStr:step[@"polyline"]];
            }];
        }];
        [result[@"transits"] enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSMutableDictionary* path = obj;
            NSMutableArray* segments = path[@"segments"];
            [segments enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                NSMutableDictionary* segment = obj;
                NSMutableArray* buslines = segment[@"buslines"];
                [buslines enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                    NSMutableDictionary* busline = obj;
                    busline[@"polyline"] = [self polylineFromStr:busline[@"polyline"]];
                }];
                NSMutableDictionary* walking = segment[@"walking"];
                if (walking) {
                    NSMutableArray* steps = walking[@"steps"];
                    [steps enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                        NSMutableDictionary* step = obj;
                        step[@"polyline"] = [self polylineFromStr:step[@"polyline"]];
                    }];
                }
            }];
        }];

        [arr addObject:result];
    }

    //通过AMapNavigationSearchResponse对象处理搜索结果
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":request.requestId, @"data":arr}];
}
        
- (NSMutableDictionary *)dictionaryFromModel:(id)obj
{
    unsigned int count = 0;
        
    objc_property_t *properties = class_copyPropertyList([obj class], &count);
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:count];
        
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        id value = [obj valueForKey:key];

        //only add it to dictionary if it is not nil
        if (key && value) {
            if ([value isKindOfClass:[NSString class]]
                || [value isKindOfClass:[NSNumber class]]) {
                // 普通类型的直接变成字典的值
                [dict setObject:value forKey:key];
            }
            else if ([value isKindOfClass:[NSArray class]]
                     || [value isKindOfClass:[NSDictionary class]]) {
                // 数组类型或字典类型
                [dict setObject:[self idFromObject:value] forKey:key];
            }
            else {
                // 如果model里有其他自定义模型，则递归将其转换为字典
                [dict setObject:[self dictionaryFromModel:value] forKey:key];
            }
        } else if (key && value == nil) {
            // 如果当前对象该值为空，设为nil。在字典中直接加nil会抛异常，需要加NSNull对象
            [dict setObject:[NSNull null] forKey:key];
        }
    }
    
    free(properties);
    return dict;
}

- (id)idFromObject:(nonnull id)object
{
    if ([object isKindOfClass:[NSArray class]]) {
        if (object != nil && [object count] > 0) {
            NSMutableArray *array = [NSMutableArray array];
            for (id obj in object) {
                // 基本类型直接添加
                if ([obj isKindOfClass:[NSString class]]
                    || [obj isKindOfClass:[NSNumber class]]) {
                    [array addObject:obj];
                }
                // 字典或数组需递归处理
                else if ([obj isKindOfClass:[NSDictionary class]]
                         || [obj isKindOfClass:[NSArray class]]) {
                    [array addObject:[self idFromObject:obj]];
                }
                // model转化为字典
                else {
                    [array addObject:[self dictionaryFromModel:obj]];
                }
            }
            return array;
        }
        else {
            return object ? : [NSNull null];
        }
    }
    else if ([object isKindOfClass:[NSDictionary class]]) {
        if (object && [[object allKeys] count] > 0) {
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            for (NSString *key in [object allKeys]) {
                // 基本类型直接添加
                if ([object[key] isKindOfClass:[NSNumber class]]
                    || [object[key] isKindOfClass:[NSString class]]) {
                    [dic setObject:object[key] forKey:key];
                }
                // 字典或数组需递归处理
                else if ([object[key] isKindOfClass:[NSArray class]]
                         || [object[key] isKindOfClass:[NSDictionary class]]) {
                    [dic setObject:[self idFromObject:object[key]] forKey:key];
                }
                // model转化为字典
                else {
                    [dic setObject:[self dictionaryFromModel:object[key]] forKey:key];
                }
            }
            return dic;
        }
        else {
            return object ? : [NSNull null];
        }
    }

    return [NSNull null];
}

-(void)AMapSearchRequest:(id)request didFailWithError:(NSError *)error
{
    AMapSearchObject *search = (AMapSearchObject *)request;
    [self.bridge.eventDispatcher sendAppEventWithName:@"ReceiveAMapSearchResult" body:@{
                                                                                        @"requestId":search.requestId, @"error":@{
                                                                                                @"domain": error.domain,
                                                                                                @"userInfo": error.userInfo
                                                                                                }}];
}

- (NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    unsigned count;
    objc_property_t *properties = class_copyPropertyList([obj class], &count);
    
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
//        if ([[obj valueForKey:key] class] == [AMapGeoPoint class] ) {
//            @todo//
//        }
        [dict setObject:[obj valueForKey:key] forKey:key];
    }
    
    free(properties);
    
    return [NSDictionary dictionaryWithDictionary:dict];
}

@end
