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
@property (strong, nonatomic) NSMutableDictionary *friendsDic;

@property (strong, nonatomic) UIButton* leaveBtn;

@end

@implementation AddFriendViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
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

- (void)clickSearchBtn:(UIButton*) sender{
    NSLog(@"click search button!");
    
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    
    //NSString *urlStr = @"ws://192.168.11.123:9001/api/v1/rtc/contact/search";
    NSString *urlStr = @"ws://localhost:9001/api/v1/rtc/contact/search";
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
    NSNumber *cid = [[[NSUserDefaults standardUserDefaults] valueForKey:@"stClientID"] valueForKey:@"id"];
    NSString *sid = [cid stringValue];
    
    [manager.requestSerializer setValue:sid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];
    
    [parametersDic setObject:self.search.text forKey:@"keyword"];
    [parametersDic setObject:@"10" forKey:@"size"];
    [parametersDic setObject:@"1" forKey:@"page"];

    [manager POST:urlStr parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for searching");
        
        self.friendsDic = [NSMutableDictionary dictionary];
        
        NSMutableDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        NSMutableArray *resultArr = [resultDic valueForKey:@"contacts"];

        for (NSMutableDictionary *friendDic in resultArr){

            [self.friendsDic setValue:[[friendDic valueForKey:@"id"] valueForKey:@"id"] forKey:[friendDic valueForKey:@"name"]];
        }
        
        self.friendsArr = self.friendsDic.allKeys;
        NSLog(@"%@", self.friendsDic);
        [self.searchTableView reloadData];
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        [self showError:@"搜索失败，请检查网络"];
        NSLog(@"failure");
        NSLog(@"%@", error);
    }];
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
    
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    
    //NSString *urlStr = @"ws://192.168.11.123:9001/api/v1/rtc/contact/applyAdd";
    //NSString *urlStr = @"ws://localhost:9001/api/v1/rtc/contact/applyAdd";
    NSString *urlStr = @"ws://192.168.0.105:9001/api/v1/rtc/contact/applyAdd";
    //NSString *urlStr = @"ws://localhost:9001/api/v1/rtc/contact/Add";
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
    NSNumber *cid = [[[NSUserDefaults standardUserDefaults] valueForKey:@"stClientID"] valueForKey:@"id"];
    NSString *sid = [cid stringValue];
    
    NSNumber *ttid = self.friendsDic[self.friendsArr[indexPath.row]];
    NSString *tid = [ttid stringValue];
    
    [manager.requestSerializer setValue:sid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];
    
    [parametersDic setObject:sid forKey:@"uid"];
    [parametersDic setObject:sid forKey:@"sid"];
    [parametersDic setObject:tid forKey:@"tid"];
    [parametersDic setObject:@"0" forKey:@"type"];
    NSLog(@"%@", parametersDic);

//    NSError * error = nil;
//    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:parametersDic options:NSJSONWritingPrettyPrinted error:&error];
//    NSString * jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    NSLog(@"%@", jsonStr);
    
    [manager POST:urlStr parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for applyAdd");
        [self showError:@"请求发送成功"];
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        [self showError:@"请求失败，请检查网络并重新登陆"];
        NSLog(@"failure");
        NSLog(@"%@", error);
    }];
}

- (void) clickLeaveBtn:(UIButton*) sender {
    
    NSLog(@"Leave Sign in View Controller!");
    [self willMoveToParentViewController:nil];
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
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
