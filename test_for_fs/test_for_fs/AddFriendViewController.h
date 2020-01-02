//
//  AddFriendViewController.h
//  test_for_fs
//
//  Created by qians on 2019/12/11.
//  Copyright © 2019 qians. All rights reserved.
//

#ifndef AddFriendViewController_h
#define AddFriendViewController_h

#import <UIKit/UIKit.h>

@protocol AddFriendDelegate <NSObject>

@required
- (void) AddFriendAFNet:(NSIndexPath *)indexPath uid:(NSString* )uid sid:(NSString* )sid tid:(NSString* )tid;
- (void) SearchAFNet:(NSString*)sid;

@end


@interface AddFriendViewController : UIViewController
- (void)showError:(NSString *)errorMsg;
@property (strong, nonatomic) UITableView* searchTableView;
@property (strong, nonatomic) UITextField* search;

@property (strong, nonatomic) NSMutableArray *friendsArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;
@property (strong, nonatomic) NSMutableDictionary *friendsDic;
@property (weak, nonatomic) id<AddFriendDelegate> delegate;

@end
#endif /* AddFriendViewController_h */
