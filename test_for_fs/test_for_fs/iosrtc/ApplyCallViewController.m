//
//  ViewController.m
//  webRTC
//
//  Created by qianS on 2019/11/2.
//  Copyright © 2019年 qianS. All rights reserved.
//

#import "ApplyCallViewController.h"
#import "CallViewController.h"
#import "AFManager.h"

#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height

@interface ApplyCallViewController () <UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>

@property (strong, nonatomic) UITableView* friendsTableView;
@property (strong, nonatomic) UIButton* refreshBtn;
@property (strong, nonatomic) UIButton* leaveBtn;
@property (strong, nonatomic) UIButton* joinBtn;

@property (strong, nonatomic) NSMutableDictionary* applyCallDic;
@property (strong, nonatomic) NSMutableArray* applyCallArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;

@property (strong, nonatomic) AFManager* AFNet;

@property (strong, nonatomic) CallViewController* callView;

@end

@implementation ApplyCallViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.applyCallDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyCallDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyCallDic"];
    [self.applyCallDic addEntriesFromDictionary:localApplyCallDic];
    
    self.applyCallArr = self.applyCallDic.allValues;
    self.IDsArr = self.applyCallDic.allKeys;
    [self.friendsTableView reloadData];
    
    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void) showUI{
    CGFloat width = self.view.bounds.size.width;
    
    self.friendsTableView = [[UITableView alloc] initWithFrame:CGRectMake(20, 80, ScreenWidth-130, ScreenHeight-100) style:UITableViewStylePlain];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(myTableViewClick:)];
    [self.friendsTableView addGestureRecognizer:tapGesture];
    
    self.friendsTableView.delegate = self;
    self.friendsTableView.dataSource = self;
    [self.view addSubview:self.friendsTableView];
    
    self.refreshBtn = [[UIButton alloc] init];
    [self.refreshBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.refreshBtn setTintColor:[UIColor whiteColor]];
    [self.refreshBtn setTitle:@"Refresh" forState:UIControlStateNormal];
    [self.refreshBtn setBackgroundColor:[UIColor grayColor]];
    [self.refreshBtn setShowsTouchWhenHighlighted:YES];
    [self.refreshBtn setFrame:CGRectMake(width-100, 80, 80, 40)];
    [self.refreshBtn addTarget:self action:@selector(clickRefreshBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.refreshBtn];
    
    self.leaveBtn = [[UIButton alloc] init];
    [self.leaveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.leaveBtn setTintColor:[UIColor whiteColor]];
    [self.leaveBtn setTitle:@"Leave" forState:UIControlStateNormal];
    [self.leaveBtn setBackgroundColor:[UIColor grayColor]];
    [self.leaveBtn setShowsTouchWhenHighlighted:YES];
    [self.leaveBtn setFrame:CGRectMake(width-100, 130, 80, 40)];
    [self.leaveBtn addTarget:self action:@selector(clickLeaveBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.leaveBtn];
    
    self.joinBtn= [[UIButton alloc] init];
    [self.joinBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.joinBtn setTintColor:[UIColor whiteColor]];
    [self.joinBtn setTitle:@"join" forState:UIControlStateNormal];
    [self.joinBtn setBackgroundColor:[UIColor grayColor]];
    [self.joinBtn setShowsTouchWhenHighlighted:YES];
    [self.joinBtn setFrame:CGRectMake(width-100, 180, 80, 40)];
    [self.joinBtn addTarget:self action:@selector(clickJoinBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.joinBtn];
}

-(void)m_tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"click table view!");
    //NSLog(@"client id = %@", self.friendsDic[self.friendsArr[indexPath.row]]);
    NSString* name = self.applyCallArr[indexPath.row];
    NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* cid = self.IDsArr[indexPath.row];
    NSString* uid = [NSString stringWithFormat:@"%@", uuid];
    //NSString* cid = [NSString stringWithFormat:@"%@", ccid];
    
    UIAlertController *alertSheet = [UIAlertController alertControllerWithTitle:name message:@"是否接受通话请求" preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction* acceptAction = [UIAlertAction actionWithTitle:@"Accept" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        NSLog(@"click Comfirm Action!");
        [self doAcceptCall:indexPath friendID:cid userID:uid];
    }];
    
    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        NSLog(@"click Cancel Action!");
    }];
    [alertSheet addAction:acceptAction];
    [alertSheet addAction:cancelAction];
    [self presentViewController:alertSheet animated:YES completion:nil];
}

- (void)doAcceptCall:(NSIndexPath *)indexPath friendID:(NSString* )friendId userID:(NSString* )userId{
    NSLog(@"Do Call");
    [self.AFNet doAcceptCall:friendId];
    //[[NSUserDefaults standardUserDefaults] setObject:@false forKey:@"isCouldCall"];
    self.callView = [[CallViewController alloc] initWithId:friendId userID:userId];
    [self.callView.view setFrame:self.view.bounds];
    [self.callView.view setBackgroundColor:[UIColor whiteColor]];
    [self addChildViewController:self.callView];
    [self.callView didMoveToParentViewController:self];
    
    [self.view addSubview:self.callView.view];
}

- (void)showError:(NSString *)errorMsg {
    // 弹框提醒
    // 初始化对话框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:errorMsg preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
    // 弹出对话框
    [self presentViewController:alert animated:true completion:nil];
}

- (void) clickLeaveBtn:(UIButton*) sender {
    
    NSLog(@"Leave Apply Call View Controller!");
    [self leave];
}

- (void) clickJoinBtn:(UIButton*) sender {
    self.callView = [[CallViewController alloc] initWithId:@"2565" userID:@"2839"];
    [self.callView.view setFrame:self.view.bounds];
    [self.callView.view setBackgroundColor:[UIColor whiteColor]];
    
    [self addChildViewController:self.callView];
    [self.callView didMoveToParentViewController:self];
    
    [self.view addSubview:self.callView.view];
    NSLog(@"Leave GrantAdd View Controller!");
    [self leave];
}

- (void) leave{
    [self willMoveToParentViewController:nil];
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
}

- (void) clickRefreshBtn:(UIButton*) sender {
    NSLog(@"Click Refresh Button!");
}

#pragma mark - 点击事件
- (void)myTableViewClick:(UIGestureRecognizer *)gestureRecognizer {
    CGPoint point = [gestureRecognizer locationInView:self.friendsTableView];
    NSIndexPath *indexpath = [self.friendsTableView indexPathForRowAtPoint:point];
    if ([self respondsToSelector:@selector(m_tableView:didSelectRowAtIndexPath:)]) {
        [self m_tableView:self.friendsTableView didSelectRowAtIndexPath:indexpath];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.applyCallArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *ide = @"Friends List";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ide];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ide];
    }
    cell.textLabel.text = [self.applyCallArr objectAtIndex:indexPath.row];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 50;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
@end
