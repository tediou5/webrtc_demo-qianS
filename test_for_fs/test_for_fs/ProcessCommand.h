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
//#import "CallViewController.h"

@protocol ProcessCommandEvents <NSObject>

@required
- (void) leaved: (NSString* ) room;
- (void) join :(NSString* )friendId;
- (void) otherjoin:(NSString* )friendID userID:(NSString* )user;
- (void) full;
- (void) byeFrom: (NSString* ) room User:(NSString*) uid;
- (void) answer: (NSString* ) sdp;
- (void) offer: (NSString* ) sdp;
- (void) candidate: (NSDictionary* ) dict;

@end

@interface ProcessCommand : NSObject
//- (void) creatCallView:(NSString* )friendId name:(NSString* )name sview:(UIViewController* )sview;
+ (ProcessCommand*) getInstance;
- (void) doApplyAddCmd: (NSString* )info;

- (void) doFull;
- (void) doMakeCallCmd:(ProcessCommand* )pCmd friendID:(NSString* )friendID;
- (void) doAcceptCallCmd:(ProcessCommand* )pCmd friendID:(NSString* )friendID userID:(NSString* )user;
- (void) doCallOfferCmd:(ProcessCommand* )pCmd sdp:(NSString* )sdp;
- (void) doCallAnswerCmd:(ProcessCommand* )pCmd sdp:(NSString* )sdp;
- (void) doCallCandidateCmd:(ProcessCommand* )pCmd info:(NSString* )info;

@property (retain, nonatomic) id<ProcessCommandEvents> delegate;

@end
#endif /* ProcessCommand_h */
