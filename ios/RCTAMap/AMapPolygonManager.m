/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import "AMapPolygonManager.h"

#import <React/RCTBridge.h>
#import <React/RCTConvert.h>
#import "RCTConvert+AMapKit.h"
#import "RCTConvert+CoreLocation.h"
#import <React/RCTEventDispatcher.h>
#import <React/UIView+React.h>
#import "AMapMarker.h"
#import <React/RCTViewManager.h>
#import "AMapPolygon.h"

@interface AMapPolygonManager()

@end

@implementation AMapPolygonManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
    AMapPolygon *polygon = [AMapPolygon new];
    return polygon;
}

RCT_EXPORT_VIEW_PROPERTY(coordinates, AMapCoordinateArray)
RCT_EXPORT_VIEW_PROPERTY(fillColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(strokeColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(strokeWidth, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(lineCap, CGLineCap)
RCT_EXPORT_VIEW_PROPERTY(lineJoin, CGLineJoin)
RCT_EXPORT_VIEW_PROPERTY(miterLimit, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(lineDashPhase, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(lineDashPattern, NSArray)

// NOTE(lmr):
// for now, onPress events for overlays will be left unimplemented. Seems it is possible with some work, but
// it is difficult to achieve in both ios and android so I decided to leave it out.
//RCT_EXPORT_VIEW_PROPERTY(onPress, RCTBubblingEventBlock)

@end
