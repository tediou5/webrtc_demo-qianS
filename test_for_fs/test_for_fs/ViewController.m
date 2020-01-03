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
#import "AFManager.h"

//#import "test_for_fs-Swift.h"
//#import "test_for_fs-Bridging-Header.h"

//OpenStomp* stomp;

@interface ViewController ()<SignOutDelegate, SignInDelegate, LoginDelegate, AddFriendDelegate, GrantAddDelegate, FriendsListDelegate, ApplyCallDelegate, CallViewDelegate>

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

@property (strong, nonatomic) AFManager* AFNet;
@property (strong, nonatomic) ProcessCommand* pCmd;



@end

@implementation ViewController

- (instancetype) init{
    self = [super init];
    NSLog(@"init!");
    
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.AFNet = [[AFManager alloc] init];
    //self.pCmd = [ProcessCommand getInstance];
    self.pCmd = [self.AFNet getPCmd];
    [self.AFNet registerSocket];
    [self isFirstRun];
    [self showUI];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)testForGetParentControll{
    NSLog(@"i can do it!!!");
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
    }
}

- (void)clickSignInBtn:(UIButton*) sender{
    NSLog(@"Click Sign In Button");
    self.signInView = [[SignInViewController alloc] init];
    [self.signInView.view setFrame:self.view.bounds];
    [self.signInView.view setBackgroundColor:[UIColor whiteColor]];
    self.signInView.delegate = self;
    
    [self addChildViewController:self.signInView];
    [self.signInView didMoveToParentViewController:self];
    [self.view addSubview:self.signInView.view];
}

- (void)clickSignOutBtn:(UIButton*) sender{
    NSLog(@"Click Sign out Button");
    self.signOutView = [[SignOutViewController alloc] init];
    [self.signOutView.view setFrame:self.view.bounds];
    [self.signOutView.view setBackgroundColor:[UIColor whiteColor]];
    self.signOutView.delegate = self;
    
    [self addChildViewController:self.signOutView];
    [self.signOutView didMoveToParentViewController:self];
    [self.view addSubview:self.signOutView.view];
}

- (void)clickLogInBtn:(UIButton*) sender{
    NSLog(@"Click Login Button");
    self.loginView = [[LoginViewController alloc] init];
    [self.loginView.view setFrame:self.view.bounds];
    [self.loginView.view setBackgroundColor:[UIColor whiteColor]];
    self.loginView.delegate = self;
    
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
    self.applyCallView.delegate = self;
    
    [self addChildViewController:self.applyCallView];
    [self.applyCallView didMoveToParentViewController:self];
    [self.view addSubview:self.applyCallView.view];
}

- (void)clickAddFriendBtn:(UIButton*) sender{
    NSLog(@"Click Add Friend Button");
    self.addFriendView = [[AddFriendViewController alloc] init];
    [self.addFriendView.view setFrame:self.view.bounds];
    [self.addFriendView.view setBackgroundColor:[UIColor whiteColor]];
    self.addFriendView.delegate = self;
    
    [self addChildViewController:self.addFriendView];
    [self.addFriendView didMoveToParentViewController:self];
    [self.view addSubview:self.addFriendView.view];
}

- (void)clickApplyAddBtn:(UIButton*) sender{
    NSLog(@"Click Add Friend Button");
    self.applyAddView = [[GrantAddFriendsViewController alloc] init];
    [self.applyAddView.view setFrame:self.view.bounds];
    [self.applyAddView.view setBackgroundColor:[UIColor whiteColor]];
    self.applyAddView.delegate = self;
    
    [self addChildViewController:self.applyAddView];
    [self.applyAddView didMoveToParentViewController:self];
    [self.view addSubview:self.applyAddView.view];
}

- (void)clickFriendsListBtn:(UIButton*) sender{
    NSLog(@"Click Delete Friend Button");
    self.friendsListView = [[FriendsListViewController alloc] init];
    [self.friendsListView.view setFrame:self.view.bounds];
    [self.friendsListView.view setBackgroundColor:[UIColor whiteColor]];
    self.friendsListView.delegate = self;
    
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
    [self.applyCallBtn setFrame:CGRectMake(width-150, 260, 120, 40)];
    [self.applyCallBtn addTarget:self action:@selector(clickIosrtcBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.applyCallBtn];
    
    self.addFriendBtn = [[UIButton alloc] init];
    [self.addFriendBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.addFriendBtn setTintColor:[UIColor whiteColor]];
    [self.addFriendBtn setTitle:@"Search Friend" forState:UIControlStateNormal];
    [self.addFriendBtn setBackgroundColor:[UIColor grayColor]];
    [self.addFriendBtn setShowsTouchWhenHighlighted:YES];
    [self.addFriendBtn setFrame:CGRectMake(width-150, 310, 120, 40)];
    [self.addFriendBtn addTarget:self action:@selector(clickAddFriendBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.addFriendBtn];
    
    self.applyAddBtn = [[UIButton alloc] init];
    [self.applyAddBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.applyAddBtn setTintColor:[UIColor whiteColor]];
    [self.applyAddBtn setTitle:@"ApplyAdd List" forState:UIControlStateNormal];
    [self.applyAddBtn setBackgroundColor:[UIColor grayColor]];
    [self.applyAddBtn setShowsTouchWhenHighlighted:YES];
    [self.applyAddBtn setFrame:CGRectMake(width-150, 360, 120, 40)];
    [self.applyAddBtn addTarget:self action:@selector(clickApplyAddBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.applyAddBtn];
    
    self.friendsListBtn = [[UIButton alloc] init];
    [self.friendsListBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.friendsListBtn setTintColor:[UIColor whiteColor]];
    [self.friendsListBtn setTitle:@"Friends List" forState:UIControlStateNormal];
    [self.friendsListBtn setBackgroundColor:[UIColor grayColor]];
    [self.friendsListBtn setShowsTouchWhenHighlighted:YES];
    [self.friendsListBtn setFrame:CGRectMake(width-150, 410, 120, 40)];
    [self.friendsListBtn addTarget:self action:@selector(clickFriendsListBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.friendsListBtn];
}

#pragma RTC媒体协商

-(void)MakeCallAFNet:(NSString *)friendId name:(NSString *)name{
    [self.AFNet doMakeCall:friendId name:name];
    
    NSNumber* uid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* userID = [NSString stringWithFormat:@"%@", uid];
    self.callView = [[CallViewController alloc] initWithId:self.pCmd friendID:friendId userID:userID];
    [self.callView.view setFrame:self.view.bounds];
    [self.callView.view setBackgroundColor:[UIColor whiteColor]];
    self.callView.delegate = self;
    
    [self addChildViewController:self.callView];
    [self.callView didMoveToParentViewController:self];
    [self.view addSubview:self.callView.view];
    [self.pCmd.delegate join:friendId];
}

-(void)doAcceptCall:(NSString *)friendId userID:(NSString *)userId{
    self.callView = [[CallViewController alloc] initWithId:self.pCmd friendID:friendId userID:userId];
    [self.callView.view setFrame:self.view.bounds];
    [self.callView.view setBackgroundColor:[UIColor whiteColor]];
    self.callView.delegate = self;
    [self addChildViewController:self.callView];
    [self.callView didMoveToParentViewController:self];
    [self.view addSubview:self.callView.view];
    
    if (self.pCmd.delegate) {
        [self.pCmd.delegate join:friendId];
    }else{
        NSLog(@"not set delegate");
    }
    
    [self.AFNet doAcceptCall:friendId];
}

-(void)sendOfferAFNet:(NSString *)sdp friendId:(NSString *)friendId{
    [self.AFNet sendOffer:sdp friendId:friendId];
}

-(void)sendCandidateAFNet:(NSDictionary *)sdp friendId:(NSString *)friendId{
    [self.AFNet sendCandidate:sdp friendId:friendId];
}

-(void)sendAnswerAFNet:(NSString *)sdp friendId:(NSString *)friendId{
    NSLog(@"=====arrived sendAnswerAFNet!=====");
    NSLog(@"friendId = %@", friendId);
    [self.AFNet sendAnswer:sdp friendId:friendId];
}

#pragma mark - subView delegate
- (void)signOutAFNet {
    [self testForGetParentControll];
}

- (void)loginAFNet:(NSString *)name passwd:(NSString *)passwd{
    NSLog(@"Login!");
    [self.AFNet registerSocket];
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet login:name passwd:passwd group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.loginView showError:@"登陆成功！"];
            [[NSUserDefaults standardUserDefaults] setObject:passwd forKey:@"passwd"];
            [NSThread detachNewThreadSelector:@selector(threadECHO) toTarget:self withObject:nil];
        }else{
            [self.loginView showError:@"登录失败，请检查用户名和密码是否正确"];
        }
    });
}

- (void)AddFriendAFNet:(NSIndexPath *)indexPath uid:(NSString *)uid sid:(NSString *)sid tid:(NSString *)tid{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet applyAddDevice:sid sid:sid tid:tid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.addFriendView showError:@"好友申请发送成功！"];
        }else{
            [self.addFriendView showError:@"发送失败，请检查网络并重新申请"];
        }
    });
}

-(void)GrantAddAFNet:(NSIndexPath *)indexPath uid:(NSString *)uid sid:(NSString *)sid tid:(NSString *)tid type:(NSString *)type{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    [self.AFNet grantAddDevice:sid sid:sid tid:tid type:type group:group];
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.applyAddView.applyAddDic removeObjectForKey:self.applyAddView.IDsArr[indexPath.row]];
            [[NSUserDefaults standardUserDefaults] setObject:self.applyAddView.applyAddDic forKey:@"applyAddDic"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            self.applyAddView.applyAddArr = [self.applyAddView.applyAddDic.allValues mutableCopy];
            [self.applyAddView.ApplyAddTableView reloadData];
            
            [self.applyAddView showError:@"好友申请已处理！"];
        }else{
            [self.applyAddView showError:@"请求发送失败，请检查网络并重新申请"];
        }
    });
}

-(void)GetContactsAFNet{
    NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* uid = [NSString stringWithFormat:@"%@", uuid];
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet getContacts:uid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            self.friendsListView.friendsDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"friends"];
            NSLog(@"friends = %@", self.friendsListView.friendsDic);
            if ([self.friendsListView.friendsDic isKindOfClass:[NSDictionary class]] && self.friendsListView.friendsDic.count != 0) {
                self.friendsListView.friendsArr = [self.friendsListView.friendsDic.allValues mutableCopy];
                self.friendsListView.IDsArr = [self.friendsListView.friendsDic.allKeys mutableCopy];
                NSLog(@"refresh %@", self.friendsListView.friendsArr);
                [self.friendsListView.friendsTableView reloadData];
            }else{
                 [self.friendsListView showError:@"YOU HAVE NO FRIENDS!!!"];
                 [self.friendsListView leave];
            }
        }else{
            [self.friendsListView showError:@"please login first"];
        }
    });
}

-(void)DeleteAFNet:(NSString *)uid cid:(NSString *)cid{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet deleteDevice:uid cid:cid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.friendsListView showError:@"成功删除好友！"];
            [self.friendsListView refresh];
        }else{
            [self.friendsListView showError:@"删除失败，请检查网络并重新申请"];
        }
    });
}

-(void)SearchAFNet:(NSString *)sid{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    self.addFriendView.friendsDic = [self.AFNet search:sid keyword:self.addFriendView.search.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            self.addFriendView.friendsArr = [self.addFriendView.friendsDic.allValues mutableCopy];
            self.addFriendView.IDsArr = [self.addFriendView.friendsDic.allKeys mutableCopy];
            [self.addFriendView.searchTableView reloadData];
        }else{
            [self.addFriendView showError:@"搜索失败，请检查网络并重新搜索！"];
        }
    });
    
    self.addFriendView.friendsArr = [self.addFriendView.friendsDic.allValues mutableCopy];
    [self.addFriendView.searchTableView reloadData];
}

- (void)getAuthCodeAFNet{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet getAuthCode:self.signInView.phoneNum.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.signInView showError:@"验证码已发送！"];
            self.signInBtn.enabled = YES;
        }else{
            [self.signInView showError:@"发送失败，请检查网络并重新申请！"];
            self.signInView.getAuthenticodeBtn.enabled = YES;
        }
    });
}

- (void)signInAFNet{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet signIn:self.signInView.phoneNum.text authCode:self.signInView.authenticode.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.signInView showError:@"注册成功！"];
        }else{
            [self.signInView showError:@"注册失败，请检查验证码并重新输入！"];
        }
    });
}

- (void)threadECHO {
    while (1) {
        [self.AFNet echo];
        NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
        NSString* uid = [NSString stringWithFormat:@"%@", uuid];
        [self.AFNet getContacts:uid];
        [NSThread sleepForTimeInterval:5];
    }
}
@end
