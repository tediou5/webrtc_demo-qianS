//
//  SignInViewController.h
//  test_for_fs
//
//  Created by qians on 2019/11/26.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef SignInViewController_h
#define SignInViewController_h

#import <UIKit/UIKit.h>
#import "AFManager.h"
//#import <Foundation/Foundation.h>

@protocol SignInDelegate <NSObject>

@required
- (void) signInAFNet;

@end

@interface SignInViewController : UIViewController

- (void)showError:(NSString *)errorMsg;
//- (instancetype) initAddr:(NSString*)addr withRoom:(NSString*)room;
@property (weak, nonatomic) id<SignInDelegate> delegate;

@end


#endif /* SignInViewController_h */
