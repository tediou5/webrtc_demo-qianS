//
//  UIViewController+SignOutViewController.h
//  test_for_fs
//
//  Created by qians on 2019/11/26.
//  Copyright © 2019 qians. All rights reserved.
//


#ifndef SignOutViewController_h
#define SignOutViewController_h

#import <UIKit/UIKit.h>
//#import <Foundation/Foundation.h>


@protocol SignOutDelegate <NSObject>

@required
- (void) signOutAFNet;

@end

@interface SignOutViewController : UIViewController

//- (instancetype) initAddr:(NSString*)addr withRoom:(NSString*)room;
@property (weak, nonatomic) id<SignOutDelegate> delegate;

@end


#endif /* SignOutViewController_h */
