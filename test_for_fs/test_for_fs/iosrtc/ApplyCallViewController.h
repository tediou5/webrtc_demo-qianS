//
//  ViewController.h
//  webRTC
//
//  Created by qianS on 2019/11/2.
//  Copyright © 2019年 qianS. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol ApplyCallDelegate <NSObject>
@required
- (void) doAcceptCall: (NSString* )friendId userID:(NSString* )userId;
@end

@interface ApplyCallViewController : UIViewController
- (void)showError:(NSString *)errorMsg;

@property (weak, nonatomic) id<ApplyCallDelegate> delegate;

@end

