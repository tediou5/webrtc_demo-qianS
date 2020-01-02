//
//  DeleteFriendsViewController.h
//  test_for_fs
//
//  Created by qians on 2019/12/24.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef DeleteFriendsViewController_h
#define DeleteFriendsViewController_h

#import <UIKit/UIKit.h>
#import "CallViewController.h"

@protocol FriendsListDelegate <NSObject>

@required
- (void) GetContactsAFNet;
- (void) DeleteAFNet:(NSString* )uid cid:(NSString* )cid;
- (void) MakeCallAFNet:(NSString* )friendId name:(NSString* )name;

@end

@interface FriendsListViewController : UIViewController
@property (strong, nonatomic) UITableView* friendsTableView;
@property (strong, nonatomic) CallViewController* callView;

@property (strong, nonatomic) NSMutableDictionary* friendsDic;
@property (strong, nonatomic) NSMutableArray* friendsArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;

@property (weak, nonatomic) id<FriendsListDelegate> delegate;

- (void)showError:(NSString *)errorMsg;
- (void) refresh;
- (void) leave;

@end
#endif /* DeleteFriendsViewController_h */
