//
//  config.h
//  apprtc
//
//  Created by qians on 2019/12/30.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef config_h
#define config_h
static NSString *kTURNRefererURLString = @"https://appr.tc";
static NSString * const kARDIceServerRequestUrl = @"https://appr.tc/params";
static NSString * const kARDRoomServerHostUrl =@"https://appr.tc";

static NSString * const joinFormat = @"/join/%@";
static NSString * const joinFormatLoopback = @"/join/%@?debug=loopback";
static NSString * const messageFormat = @"/message/%@/%@";
static NSString * const leaveFormat = @"/leave/%@/%@";

#endif /* config_h */
