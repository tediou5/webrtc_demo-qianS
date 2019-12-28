//
//  GrantAddFriendsViewController.m
//  test_for_fs
//
//  Created by qianS on 2019/12/20.
//  Copyright © 2019 qians. All rights reserved.
//

#import "GrantAddFriendsViewController.h"

#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height

@interface GrantAddFriendsViewController() <UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>

@property (strong, nonatomic) UITableView* ApplyAddTableView;
@property (strong, nonatomic) UIButton* refreshBtn;
@property (strong, nonatomic) UIButton* leaveBtn;

//@property (strong, nonatomic) UIAlertController* applyAddAlert;

@property (strong, nonatomic) NSMutableDictionary* applyAddDic;
@property (strong, nonatomic) NSMutableArray* applyAddArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;

@property (strong, nonatomic) AFManager* AFNet;

@end

@implementation GrantAddFriendsViewController

- (void) viewDidLoad{
    [super viewDidLoad];
    
    self.applyAddDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyAddDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyAddDic"];
    [self.applyAddDic addEntriesFromDictionary:localApplyAddDic];
    
    self.applyAddArr = self.applyAddDic.allValues;
    self.IDsArr = self.applyAddDic.allKeys;
    [self.ApplyAddTableView reloadData];
    
    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void) showUI{
    CGFloat width = self.view.bounds.size.width;
    
    self.ApplyAddTableView = [[UITableView alloc] initWithFrame:CGRectMake(20, 80, ScreenWidth-130, ScreenHeight-100) style:UITableViewStylePlain];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(myTableViewClick:)];
    [self.ApplyAddTableView addGestureRecognizer:tapGesture];
    
    self.ApplyAddTableView.delegate = self;
    self.ApplyAddTableView.dataSource = self;
    [self.view addSubview:self.ApplyAddTableView];
    
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
    NSString* name = self.applyAddArr[indexPath.row];
    NSNumber* cid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* tid = self.IDsArr[indexPath.row];
    NSString* sid = [NSString stringWithFormat:@"%@", cid];
    //NSString* tid = [NSString stringWithFormat:@"%@", ttid];
    static NSString* type;
    type = [[NSString alloc] init];
    
    UIAlertController *alertSheet = [UIAlertController alertControllerWithTitle:name message:@"是否同意该好友的申请" preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction* agreeAction = [UIAlertAction actionWithTitle:@"Agree" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        NSLog(@"click Agree Action!");
        type = @"1";
        [self doGrantAddDevice:indexPath uid:sid sid:sid tid:tid type:type];
    }];
    
    UIAlertAction* rejectAction = [UIAlertAction actionWithTitle:@"Reject" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        NSLog(@"click Reject Action!");
        type = @"0";
        [self doGrantAddDevice:indexPath uid:sid sid:sid tid:tid type:type];
    }];
    
    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        NSLog(@"click Cancel Action!");
    }];
    [alertSheet addAction:agreeAction];
    [alertSheet addAction:rejectAction];
    [alertSheet addAction:cancelAction];
    [self presentViewController:alertSheet animated:YES completion:nil];
}

- (void)doGrantAddDevice:(NSIndexPath *)indexPath uid:(NSString* )uid sid:(NSString* )sid tid:(NSString* )tid type:(NSString* )type{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet grantAddDevice:sid sid:sid tid:tid type:type group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self.applyAddDic removeObjectForKey:self.IDsArr[indexPath.row]];
            [[NSUserDefaults standardUserDefaults] setObject:self.applyAddDic forKey:@"applyAddDic"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            self.applyAddArr = self.applyAddDic.allValues;
            [self.ApplyAddTableView reloadData];
            
            [self showError:@"好友申请已处理！"];
        }else{
            [self showError:@"请求发送失败，请检查网络并重新申请"];
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
    [self willMoveToParentViewController:nil];
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
}


#pragma mark - 点击事件
- (void)myTableViewClick:(UIGestureRecognizer *)gestureRecognizer {
    CGPoint point = [gestureRecognizer locationInView:self.ApplyAddTableView];
    NSIndexPath *indexpath = [self.ApplyAddTableView indexPathForRowAtPoint:point];
    if ([self respondsToSelector:@selector(m_tableView:didSelectRowAtIndexPath:)]) {
        [self m_tableView:self.ApplyAddTableView didSelectRowAtIndexPath:indexpath];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.applyAddArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *ide = @"Apply List";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ide];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ide];
    }
    cell.textLabel.text = [self.applyAddArr objectAtIndex:indexPath.row];
    
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
