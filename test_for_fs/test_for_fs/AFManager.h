//
//  AFManager.h
//  test_for_fs
//
//  Created by qianS on 2019/12/17.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef AFManager_h
#define AFManager_h

#import <Foundation/Foundation.h>
#import <AFNetworking.h>

@interface AFManager : NSObject

- (instancetype) init;

- (void) login: (NSString* )name passwd:(NSString* )passwd group:(dispatch_group_t)group;
- (void) logout: (NSString* )uid group:(dispatch_group_t)group;
- (void) signIn: (NSString* )phoneNum authCode:(NSString* )authCode group:(dispatch_group_t)group;
- (void) getAuthCode: (NSString* )phoneNum group:(dispatch_group_t)group;
- (NSMutableDictionary* ) search:(NSString* )sid keyword:(NSString* )keyword group:(dispatch_group_t)group;
- (void) applyAddDevice: (NSString* )cid sid:(NSString* )sid tid:(NSString* )tid group:(dispatch_group_t)group;
- (void) grantAddDevice: (NSString* )uid sid:(NSString* )sid tid:(NSString* )tid type:(NSString* )type group:(dispatch_group_t)group;
- (void) deleteDevice: (NSString* )uid cid:(NSString* )cid group:(dispatch_group_t)group;

- (bool) getIsSuccess;

@end

#endif /* AFManager_h */
