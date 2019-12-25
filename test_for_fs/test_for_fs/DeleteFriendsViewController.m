//
//  DeleteFriendsViewController.m
//  test_for_fs
//
//  Created by qians on 2019/12/24.
//  Copyright © 2019 qians. All rights reserved.
//

#import "DeleteFriendsViewController.h"

#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height

@interface DeleteFriendsViewController() <UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>

@property (strong, nonatomic) UITableView* friendsTableView;
@property (strong, nonatomic) UIButton* refreshBtn;
@property (strong, nonatomic) UIButton* leaveBtn;

@property (strong, nonatomic) NSMutableDictionary* friendsDic;
@property (strong, nonatomic) NSMutableArray* friendsArr;

@property (strong, nonatomic) AFManager* AFNet;

@end

@implementation DeleteFriendsViewController

- (void) viewDidLoad{
    [super viewDidLoad];
    
    self.friendsDic = [NSMutableDictionary dictionary];
    [self refresh];
    
    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self refresh];
//    NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
//    NSString* uid = [NSString stringWithFormat:@"%@", uuid];
//
//    dispatch_group_t group = dispatch_group_create();
//    dispatch_group_enter(group);
//
//    [self.AFNet getContacts:uid group:group];
//
//    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
//        bool isSuccess = [self.AFNet getIsSuccess];
//        if (isSuccess == YES) {
//
//        }else{
//            [self showError:@"please login first"];
//        }
//    });
}

- (void) refresh{
    //i should do GET with webSocket but not get friendsDic from local
    
    NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* uid = [NSString stringWithFormat:@"%@", uuid];
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet getContacts:uid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            self.friendsDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"friends"];
            NSLog(@"%@", self.friendsDic);
            if ([self.friendsDic isKindOfClass:[NSDictionary class]] && self.friendsDic.count != 0) {
                self.friendsArr = self.friendsDic.allKeys;
                NSLog(@"%@", self.friendsArr);
                [self.friendsTableView reloadData];
            }else{
                 [self showError:@"YOU HAVE NO FRIENDS!!!"];
                 [self leave];
            }
        }else{
            [self showError:@"please login first"];
        }
    });
    


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
}

-(void)m_tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"click table view!");
    //NSLog(@"client id = %@", self.friendsDic[self.friendsArr[indexPath.row]]);
    NSString* name = self.friendsArr[indexPath.row];
    NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSNumber* ccid = self.friendsDic[self.friendsArr[indexPath.row]];
    NSString* uid = [NSString stringWithFormat:@"%@", uuid];
    NSString* cid = [NSString stringWithFormat:@"%@", ccid];
    
    UIAlertController *alertSheet = [UIAlertController alertControllerWithTitle:name message:@"是否确定删除该好友" preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction* comfirmAction = [UIAlertAction actionWithTitle:@"Confirm" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        NSLog(@"click Agree Action!");
        [self doDeleteFriends:indexPath uid:uid cid:cid];
    }];
    
    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        NSLog(@"click Cancel Action!");
    }];
    [alertSheet addAction:comfirmAction];
    [alertSheet addAction:cancelAction];
    [self presentViewController:alertSheet animated:YES completion:nil];
}

- (void)doDeleteFriends:(NSIndexPath *)indexPath uid:(NSString* )uid cid:(NSString* )cid{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet deleteDevice:uid cid:cid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self refresh];
            [self showError:@"成功删除好友！"];
        }else{
            [self showError:@"删除失败，请检查网络并重新申请"];
        }
    });
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
    [self refresh];
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
    return self.friendsArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *ide = @"Friends List";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ide];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ide];
    }
    cell.textLabel.text = [self.friendsArr objectAtIndex:indexPath.row];
    
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
