//
//  CallViewController.h
//  webRTC
//
//  Created by qianS on 2019/11/2.
//  Copyright © 2019年 qianS. All rights reserved.
//

#ifndef CallViewController_h
#define CallViewController_h

#import <UIKit/UIKit.h>
#import "ProcessCommand.h"
#import <WebRTC/WebRTC.h>
#import <MBProgressHUD/MBProgressHUD.h>

@protocol CallViewDelegate <NSObject>
@required
- (void)sendAnswerAFNet:(NSString* )sdp friendId:(NSString* )friendId;
- (void)sendCandidateAFNet:(NSDictionary* )sdp friendId:(NSString* )friendId;
- (void)sendOfferAFNet:(NSString* )sdp friendId:(NSString* )friendId;

@end

@interface CallViewController : UIViewController
@property (weak, nonatomic) id<CallViewDelegate> delegate;
- (instancetype) initWithId:(ProcessCommand* )pCmd friendID:(NSString* )friend userID:(NSString* )user;

@end


#endif /* CallViewController_h */
