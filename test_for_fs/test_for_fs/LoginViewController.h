//
//  LogInViewController.h
//  test_for_fs
//
//  Created by qians on 2019/11/27.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef LoginViewController_h
#define LoginViewController_h

#import <UIKit/UIKit.h>
#import "AFManager.h"

//#import "test_for_fs-Swift.h"
//#import <Foundation/Foundation.h>
@protocol LoginDelegate <NSObject>

@required
- (void) loginAFNet:(NSString* )name passwd:(NSString* )passwd;

@end

@interface LoginViewController : UIViewController

- (void)showError:(NSString *)errorMsg;
//- (instancetype) initAddr:(NSString*)addr withRoom:(NSString*)room;
@property (weak, nonatomic) id<LoginDelegate> delegate;

@end

#endif /* LogInViewController_h */
