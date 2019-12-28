//
//  ViewController.m
//  test_for_fs
//
//  Created by qians on 2019/11/26.
//  Copyright © 2019 qians. All rights reserved.
//

#import "ViewController.h"
#import "SignInViewController.h"
#import "SignOutViewController.h"
#import "LoginViewController.h"
#import "ApplyCallViewController.h"
#import "AddFriendViewController.h"
#import "GrantAddFriendsViewController.h"
#import "FriendsListViewController.h"
#import "CallViewController.h"
#import "ProcessCommand.h"

//#import "test_for_fs-Swift.h"
//#import "test_for_fs-Bridging-Header.h"

//OpenStomp* stomp;

@interface ViewController ()

@property (strong, nonatomic) UIButton* signInBtn;
@property (strong, nonatomic) UIButton* signOutBtn;
@property (strong, nonatomic) UIButton* loginBtn;
@property (strong, nonatomic) UIButton* addFriendBtn;
@property (strong, nonatomic) UIButton* applyAddBtn;
@property (strong, nonatomic) UIButton* friendsListBtn;

@property (strong, nonatomic) UIButton* testBtn;

@property (strong, nonatomic) UIButton* applyCallBtn;

@property (strong, nonatomic) SignInViewController* signInView;
@property (strong, nonatomic) SignOutViewController* signOutView;
@property (strong, nonatomic) LoginViewController* loginView;
@property (strong, nonatomic) ApplyCallViewController* applyCallView;
@property (strong, nonatomic) AddFriendViewController* addFriendView;
@property (strong, nonatomic) GrantAddFriendsViewController* applyAddView;
@property (strong, nonatomic) FriendsListViewController* friendsListView;
@property (strong, nonatomic) CallViewController* callView;

@end

@implementation ViewController

- (instancetype) init{
    self = [super init];
    
    NSLog(@"init!");
    
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self isFirstRun];
    [self showUI];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

/** 点击空白处回收键盘 */
- (void)touchesBegan:(NSSet<UITouch *> *)touches   withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void) isFirstRun{
    bool isFirst = [[[NSUserDefaults standardUserDefaults] valueForKey:@"isFirstRun"] boolValue];
    if (!isFirst){
        NSLog(@"is first");
        NSMutableDictionary* applyAddDic = [NSMutableDictionary dictionary];
        NSMutableDictionary* applyCallDic = [NSMutableDictionary dictionary];
        //[applyAddDic setObject:@"" forKey:@""];
        
        [[NSUserDefaults standardUserDefaults] setObject:@"user01" forKey:@"name"];
        [[NSUserDefaults standardUserDefaults] setObject:@"abcd1234" forKey:@"passwd"];
        [[NSUserDefaults standardUserDefaults] setObject:@YES forKey:@"isFirstRun"];
        [[NSUserDefaults standardUserDefaults] setObject:@YES forKey:@"isCouldCall"];
        [[NSUserDefaults standardUserDefaults] setObject:applyAddDic forKey:@"applyAddDic"];
        [[NSUserDefaults standardUserDefaults] setObject:applyCallDic forKey:@"applyCallDic"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }else{
        NSLog(@"is not the first");
        [[NSUserDefaults standardUserDefaults] setObject:@YES forKey:@"isCouldCall"];
        //NSMutableDictionary* applyCallDic = [NSMutableDictionary dictionary];
        //[applyCallDic setObject:@"" forKey:@""];
        //[[NSUserDefaults standardUserDefaults] setObject:applyCallDic forKey:@"applyCallDic"];
        //[[NSUserDefaults standardUserDefaults] synchronize];
//        NSMutableDictionary* applyAddDic = [NSMutableDictionary dictionary];
//        [applyAddDic removeAllObjects];
//        [[NSUserDefaults standardUserDefaults] setObject:applyAddDic forKey:@"applyAddDic"];
//        [[NSUserDefaults standardUserDefaults] synchronize];
    }
}

- (void)clickSignInBtn:(UIButton*) sender{
    NSLog(@"Click Sign In Button");
    self.signInView = [[SignInViewController alloc] init];
    [self.signInView.view setFrame:self.view.bounds];
    [self.signInView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.signInView];
    [self.signInView didMoveToParentViewController:self];
    
    [self.view addSubview:self.signInView.view];
}

- (void)clickSignOutBtn:(UIButton*) sender{
    NSLog(@"Click Sign out Button");
    self.signOutView = [[SignOutViewController alloc] init];
    [self.signOutView.view setFrame:self.view.bounds];
    [self.signOutView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.signOutView];
    [self.signOutView didMoveToParentViewController:self];
    
    [self.view addSubview:self.signOutView.view];
}

- (void)clickLogInBtn:(UIButton*) sender{
    NSLog(@"Click Login Button");
    self.loginView = [[LoginViewController alloc] init];
    [self.loginView.view setFrame:self.view.bounds];
    [self.loginView.view setBackgroundColor:[UIColor whiteColor]];
    [self addChildViewController:self.loginView];
    [self.loginView didMoveToParentViewController:self];
    
    [self.view addSubview:self.loginView.view];
}

- (void)clickTestBtn:(UIButton*) sender{
    NSLog(@"Click Test Button");
    NSLog(@"name = %@", [[NSUserDefaults standardUserDefaults] valueForKey:@"name"]);
    NSLog(@"Token = %@", [[NSUserDefaults standardUserDefaults] valueForKey:@"token"]);
    NSLog(@"applyAddDic = %@", [[NSUserDefaults standardUserDefaults] valueForKey:@"applyAddDic"]);
    NSLog(@"stClientID.id = %@", [[NSUserDefaults standardUserDefaults] valueForKey:@"id"]);
    NSLog(@"friends = %@", [[NSUserDefaults standardUserDefaults] valueForKey:@"friends"]);
}



- (void)clickIosrtcBtn:(UIButton*) sender{
    NSLog(@"Click iosrtc Button");
    self.applyCallView = [[ApplyCallViewController alloc] init];
    [self.applyCallView.view setFrame:self.view.bounds];
    [self.applyCallView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.applyCallView];
    [self.applyCallView didMoveToParentViewController:self];
    
    [self.view addSubview:self.applyCallView.view];
}

- (void)clickAddFriendBtn:(UIButton*) sender{
    NSLog(@"Click Add Friend Button");
    self.addFriendView = [[AddFriendViewController alloc] init];
    [self.addFriendView.view setFrame:self.view.bounds];
    [self.addFriendView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.addFriendView];
    [self.addFriendView didMoveToParentViewController:self];
    
    [self.view addSubview:self.addFriendView.view];
}

- (void)clickApplyAddBtn:(UIButton*) sender{
    NSLog(@"Click Add Friend Button");
    self.applyAddView = [[GrantAddFriendsViewController alloc] init];
    [self.applyAddView.view setFrame:self.view.bounds];
    [self.applyAddView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.applyAddView];
    [self.applyAddView didMoveToParentViewController:self];
    
    [self.view addSubview:self.applyAddView.view];
}

- (void)clickFriendsListBtn:(UIButton*) sender{
    NSLog(@"Click Delete Friend Button");
    self.friendsListView = [[FriendsListViewController alloc] init];
    [self.friendsListView.view setFrame:self.view.bounds];
    [self.friendsListView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.friendsListView];
    [self.friendsListView didMoveToParentViewController:self];
    
    [self.view addSubview:self.friendsListView.view];
}

- (void)showUI{
    CGFloat width = self.view.bounds.size.width;
    
    self.signInBtn = [[UIButton alloc] init];
    [self.signInBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.signInBtn setTintColor:[UIColor whiteColor]];
    [self.signInBtn setTitle:@"Sign in" forState:UIControlStateNormal];
    [self.signInBtn setBackgroundColor:[UIColor grayColor]];
    [self.signInBtn setShowsTouchWhenHighlighted:YES];
    [self.signInBtn setFrame:CGRectMake(width-110, 80, 80, 40)];
    [self.signInBtn addTarget:self action:@selector(clickSignInBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.signInBtn];
    
    self.signOutBtn = [[UIButton alloc] init];
    [self.signOutBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.signOutBtn setTintColor:[UIColor whiteColor]];
    [self.signOutBtn setTitle:@"Sign out" forState:UIControlStateNormal];
    [self.signOutBtn setBackgroundColor:[UIColor grayColor]];
    [self.signOutBtn setShowsTouchWhenHighlighted:YES];
    [self.signOutBtn setFrame:CGRectMake(width-110, 130, 80, 40)];
    [self.signOutBtn addTarget:self action:@selector(clickSignOutBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.signOutBtn];
    
    self.loginBtn = [[UIButton alloc] init];
    [self.loginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.loginBtn setTintColor:[UIColor whiteColor]];
    [self.loginBtn setTitle:@"Login" forState:UIControlStateNormal];
    [self.loginBtn setBackgroundColor:[UIColor grayColor]];
    [self.loginBtn setShowsTouchWhenHighlighted:YES];
    [self.loginBtn setFrame:CGRectMake(width-110, 180, 80, 40)];
    [self.loginBtn addTarget:self action:@selector(clickLogInBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.loginBtn];
    
    self.testBtn = [[UIButton alloc] init];
    [self.testBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.testBtn setTintColor:[UIColor whiteColor]];
    [self.testBtn setTitle:@"Test" forState:UIControlStateNormal];
    [self.testBtn setBackgroundColor:[UIColor grayColor]];
    [self.testBtn setShowsTouchWhenHighlighted:YES];
    [self.testBtn.layer setCornerRadius:40];
    [self.testBtn.layer setBorderWidth:1];
    [self.testBtn setClipsToBounds:FALSE];
    [self.testBtn setFrame:CGRectMake(self.view.bounds.size.width/2-40,
                                       self.view.bounds.size.height-140,
                                       80,
                                       80)];
    [self.testBtn addTarget:self
                      action:@selector(clickTestBtn:)
            forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.testBtn];
    
//    self.apprtcBtn = [[UIButton alloc] init];
//    [self.apprtcBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//    [self.apprtcBtn setTintColor:[UIColor whiteColor]];
//    [self.apprtcBtn setTitle:@"Use Apprtc" forState:UIControlStateNormal];
//    [self.apprtcBtn setBackgroundColor:[UIColor grayColor]];
//    [self.apprtcBtn setShowsTouchWhenHighlighted:YES];
//    [self.apprtcBtn setFrame:CGRectMake(width-150, 330, 120, 40)];
//    [self.apprtcBtn addTarget:self action:@selector(clickApprtcBtn:) forControlEvents:UIControlEventTouchUpInside];
//    [self.view addSubview:self.apprtcBtn];
    
    self.applyCallBtn = [[UIButton alloc] init];
    [self.applyCallBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.applyCallBtn setTintColor:[UIColor whiteColor]];
    [self.applyCallBtn setTitle:@"Apply Call" forState:UIControlStateNormal];
    [self.applyCallBtn setBackgroundColor:[UIColor grayColor]];
    [self.applyCallBtn setShowsTouchWhenHighlighted:YES];
    [self.applyCallBtn setFrame:CGRectMake(width-150, 380, 120, 40)];
    [self.applyCallBtn addTarget:self action:@selector(clickIosrtcBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.applyCallBtn];
    
    self.addFriendBtn = [[UIButton alloc] init];
    [self.addFriendBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.addFriendBtn setTintColor:[UIColor whiteColor]];
    [self.addFriendBtn setTitle:@"Search Friend" forState:UIControlStateNormal];
    [self.addFriendBtn setBackgroundColor:[UIColor grayColor]];
    [self.addFriendBtn setShowsTouchWhenHighlighted:YES];
    [self.addFriendBtn setFrame:CGRectMake(width-150, 430, 120, 40)];
    [self.addFriendBtn addTarget:self action:@selector(clickAddFriendBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.addFriendBtn];
    
    self.applyAddBtn = [[UIButton alloc] init];
    [self.applyAddBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.applyAddBtn setTintColor:[UIColor whiteColor]];
    [self.applyAddBtn setTitle:@"ApplyAdd List" forState:UIControlStateNormal];
    [self.applyAddBtn setBackgroundColor:[UIColor grayColor]];
    [self.applyAddBtn setShowsTouchWhenHighlighted:YES];
    [self.applyAddBtn setFrame:CGRectMake(width-150, 480, 120, 40)];
    [self.applyAddBtn addTarget:self action:@selector(clickApplyAddBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.applyAddBtn];
    
    self.friendsListBtn = [[UIButton alloc] init];
    [self.friendsListBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.friendsListBtn setTintColor:[UIColor whiteColor]];
    [self.friendsListBtn setTitle:@"Friends List" forState:UIControlStateNormal];
    [self.friendsListBtn setBackgroundColor:[UIColor grayColor]];
    [self.friendsListBtn setShowsTouchWhenHighlighted:YES];
    [self.friendsListBtn setFrame:CGRectMake(width-150, 530, 120, 40)];
    [self.friendsListBtn addTarget:self action:@selector(clickFriendsListBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.friendsListBtn];
}
@end
