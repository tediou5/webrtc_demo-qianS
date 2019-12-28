//
//  AddFriendViewController.m
//  test_for_fs
//
//  Created by qians on 2019/12/11.
//  Copyright © 2019 qians. All rights reserved.
//

//#import <Foundation/Foundation.h>
#import "AddFriendViewController.h"

#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height

@interface AddFriendViewController()<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>


@property (strong, nonatomic) UILabel* searchLabel;
@property (strong, nonatomic) UIButton* searchBtn;
@property (strong, nonatomic) UITextField* search;

@property (strong, nonatomic) UITableView* searchTableView;

@property (strong, nonatomic) NSArray *friendsArr;
@property (strong, nonatomic) NSMutableArray* IDsArr;
@property (strong, nonatomic) NSMutableDictionary *friendsDic;

@property (strong, nonatomic) UIButton* leaveBtn;

@property (strong, nonatomic) AFManager* AFNet;
@end

@implementation AddFriendViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void)clickSearchBtn:(UIButton*) sender{
    NSLog(@"click search button!");
    self.friendsDic = [NSMutableDictionary dictionary];
    
    NSNumber *cid = [[[NSUserDefaults standardUserDefaults] valueForKey:@"stClientID"] valueForKey:@"id"];
    NSString *sid = [cid stringValue];
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    self.friendsDic = [self.AFNet search:sid keyword:self.search.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            self.friendsArr = self.friendsDic.allValues;
            self.IDsArr = self.friendsDic.allKeys;
            [self.searchTableView reloadData];
        }else{
            [self showError:@"搜索失败，请检查网络并重新搜索！"];
        }
    });
    
    self.friendsArr = self.friendsDic.allValues;
    [self.searchTableView reloadData];
}

- (void)showError:(NSString *)errorMsg {
    // 弹框提醒
    // 初始化对话框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:errorMsg preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
    // 弹出对话框
    [self presentViewController:alert animated:true completion:nil];
}

-(void)m_tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"click table view!");
    NSLog(@"client id = %@", self.friendsDic[self.friendsArr[indexPath.row]]);
    NSString* name = self.friendsArr[indexPath.row];
    NSNumber* cid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
    NSString* tid = self.IDsArr[indexPath.row];
    NSString* sid = [NSString stringWithFormat:@"%@", cid];
    //NSString* tid = [NSString stringWithFormat:@"%@", ttid];
    
    UIAlertController *alertSheet = [UIAlertController alertControllerWithTitle:name message:@"是否发送好友申请" preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction* sendAction = [UIAlertAction actionWithTitle:@"Send" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        NSLog(@"click Agree Action!");
        [self doApplyAddDevice:indexPath uid:sid sid:sid tid:tid];
    }];
    
    UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        NSLog(@"click Cancel Action!");
    }];
    [alertSheet addAction:sendAction];
    [alertSheet addAction:cancelAction];
    [self presentViewController:alertSheet animated:YES completion:nil];
}

- (void)doApplyAddDevice:(NSIndexPath *)indexPath uid:(NSString* )uid sid:(NSString* )sid tid:(NSString* )tid{
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet applyAddDevice:sid sid:sid tid:tid group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self showError:@"好友申请发送成功！"];
        }else{
            [self showError:@"发送失败，请检查网络并重新申请"];
        }
    });
}

- (void) clickLeaveBtn:(UIButton*) sender {
    
    NSLog(@"Leave Sign in View Controller!");
    [self willMoveToParentViewController:nil];
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
}

- (void) showUI{
    CGFloat width = self.view.bounds.size.width;
    
    self.search = [[UITextField alloc] init];
    [self.search setText:@"1"];
    [self.search setFrame:CGRectMake(20, 100, width-130, 40)];
    [self.search setTextColor:[UIColor blackColor]];
    [self.search setBorderStyle:UITextBorderStyleRoundedRect];
    [self.search setEnabled:TRUE];
    [self.view addSubview:self.search];
    
    self.searchBtn = [[UIButton alloc] init];
    [self.searchBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.searchBtn setTintColor:[UIColor whiteColor]];
    [self.searchBtn setTitle:@"Search" forState:UIControlStateNormal];
    [self.searchBtn setBackgroundColor:[UIColor grayColor]];
    [self.searchBtn setShowsTouchWhenHighlighted:YES];
    [self.searchBtn setFrame:CGRectMake(width-90, 100, 70, 40)];
    //self.searchBtn.enabled = NO;
    
    [self.searchBtn addTarget:self action:@selector(clickSearchBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.searchBtn];
    
    self.searchTableView = [[UITableView alloc] initWithFrame:CGRectMake(20, 160, ScreenWidth, ScreenHeight-80) style:UITableViewStylePlain];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(myTableViewClick:)];
    [self.searchTableView addGestureRecognizer:tapGesture];
    
    self.searchTableView.delegate = self;
    self.searchTableView.dataSource = self;
    [self.view addSubview:self.searchTableView];
    
    self.leaveBtn = [[UIButton alloc] init];
    [self.leaveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.leaveBtn setTintColor:[UIColor whiteColor]];
    [self.leaveBtn setTitle:@"leave" forState:UIControlStateNormal];
    [self.leaveBtn setBackgroundColor:[UIColor grayColor]];
    [self.leaveBtn setShowsTouchWhenHighlighted:YES];
    [self.leaveBtn setClipsToBounds:FALSE];
    [self.leaveBtn setFrame:CGRectMake(width-90, 150, 70, 40)];
    [self.leaveBtn addTarget:self
                      action:@selector(clickLeaveBtn:)
            forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.leaveBtn];
}

#pragma mark - 点击事件
- (void)myTableViewClick:(UIGestureRecognizer *)gestureRecognizer {
    CGPoint point = [gestureRecognizer locationInView:self.searchTableView];
    NSIndexPath *indexpath = [self.searchTableView indexPathForRowAtPoint:point];
    if ([self respondsToSelector:@selector(m_tableView:didSelectRowAtIndexPath:)]) {
        [self m_tableView:self.searchTableView didSelectRowAtIndexPath:indexpath];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.friendsArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *ide = @"Search List";
    
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
