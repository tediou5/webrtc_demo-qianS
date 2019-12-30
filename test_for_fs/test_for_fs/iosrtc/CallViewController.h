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

#import "AFManager.h"
#import "ProcessCommand.h"

#import <WebRTC/WebRTC.h>
#import <MBProgressHUD/MBProgressHUD.h>

@interface CallViewController : UIViewController

- (instancetype) initWithId:(NSString* )friend userID:(NSString* )user;

@end


#endif /* CallViewController_h */
