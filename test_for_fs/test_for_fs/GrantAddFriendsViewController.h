//
//  GrantAddFriendsViewController.h
//  test_for_fs
//
//  Created by qianS on 2019/12/20.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef GrantAddFriendsViewController_h
#define GrantAddFriendsViewController_h

#import <UIKit/UIKit.h>

@protocol GrantAddDelegate <NSObject>

@required
- (void) GrantAddAFNet:(NSIndexPath *)indexPath uid:(NSString* )uid sid:(NSString* )sid tid:(NSString* )tid type:(NSString* )type;

@end

@interface GrantAddFriendsViewController : UIViewController

@property (weak, nonatomic) id<GrantAddDelegate> delegate;
- (void)showError:(NSString *)errorMsg;

@property (strong, nonatomic) UITableView* ApplyAddTableView;
@property (strong, nonatomic) NSMutableDictionary* applyAddDic;
@property (strong, nonatomic) NSMutableArray* applyAddArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;
@end
#endif /* GrantAddFriendsViewController_h */
