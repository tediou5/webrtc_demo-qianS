//
//  ProcessCommand.h
//  test_for_fs
//
//  Created by qians on 2019/12/24.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef ProcessCommand_h
#define ProcessCommand_h

#import <Foundation/Foundation.h>

@protocol SignalEventNotify <NSObject>

@optional
- (void) leaved: (NSString* ) room;
- (void) join :(NSString* )friendId;
- (void) joinCall:(NSString* )friendID userID:(NSString* )user;
- (void) full;
- (void) byeFrom: (NSString* ) room User:(NSString*) uid;
- (void) answer: (NSString* ) sdp;
- (void) offer: (NSString* ) sdp;
- (void) candidate: (NSDictionary* ) dict;

@end

@interface ProcessCommand : NSObject

- (void) doApplyAddCmd: (NSString* )info;

- (void) doFull;
- (void) doCallCmd:(NSString* )friendID userID:(NSString* )user;
- (void) doJoin:(NSString* )friendId;
- (void) doCallOfferCmd: (NSString* )sdp;
- (void) doCallAnswerCmd: (NSString* )sdp;

@property (retain, nonatomic) id<SignalEventNotify> delegate;

@end
#endif /* ProcessCommand_h */
