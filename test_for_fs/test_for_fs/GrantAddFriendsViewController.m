//
//  GrantAddFriendsViewController.m
//  test_for_fs
//
//  Created by qianS on 2019/12/20.
//  Copyright © 2019 qians. All rights reserved.
//

#import "GrantAddFriendsViewController.h"

@interface GrantAddFriendsViewController() <UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>

@property (strong, nonatomic) UITableView* ApplyAddTableView;

@property (strong, nonatomic) NSMutableDictionary* applyAddDic;
@property (strong, nonatomic) NSMutableArray* applyAddArr;

@property (strong, nonatomic) AFManager* AFNet;

@end

@implementation GrantAddFriendsViewController

- (void) viewDidLoad{
    [super viewDidLoad];
    
    self.applyAddDic = [NSMutableDictionary dictionary];
    self.applyAddDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyAddDic"];

    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void) showUI{
    NSLog(@"for test");
}

-(void)m_tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSLog(@"click table view!");
    //NSLog(@"client id = %@", self.friendsDic[self.friendsArr[indexPath.row]]);
    
    NSNumber *cid = [[[NSUserDefaults standardUserDefaults] valueForKey:@"stClientID"] valueForKey:@"id"];
    NSString *sid = [cid stringValue];
    
    NSNumber *ttid = self.applyAddDic[self.applyAddArr[indexPath.row]];
    NSString *tid = [ttid stringValue];
    
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

- (void)showError:(NSString *)errorMsg {
    // 弹框提醒
    // 初始化对话框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:errorMsg preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
    // 弹出对话框
    [self presentViewController:alert animated:true completion:nil];
}

- (void) clickLeaveBtn:(UIButton*) sender {
    
    NSLog(@"Leave Sign in View Controller!");
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
    
    static NSString *ide = @"Search List";
    
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
