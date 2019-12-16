//
//  LogInViewController.m
//  test_for_fs
//
//  Created by qians on 2019/11/27.
//  Copyright © 2019 qians. All rights reserved.
//

#import "LoginViewController.h"


@interface LoginViewController()

@property (strong, nonatomic) UILabel* nameLabel;
@property (strong, nonatomic) UITextField* name;

@property (strong, nonatomic) UILabel* passwdLabel;
@property (strong, nonatomic) UITextField* passwd;

@property (strong, nonatomic) UIButton* logInBtn;

@property (strong, nonatomic) UIButton* leaveBtn;

@end

@implementation LoginViewController


- (void)viewDidLoad{
    [super viewDidLoad];
    
    CGFloat width = self.view.bounds.size.width;
    
    self.nameLabel = [[UILabel alloc] init];
    [self.nameLabel setText:@"name:"];
    [self.nameLabel setFrame:CGRectMake(20, 100, 90, 40)];
    [self.view addSubview:self.nameLabel];
    
    self.name = [[UITextField alloc] init];
    [self.name setText:@"user05"];
    [self.name setFrame:CGRectMake(100, 100, width-120, 40)];
    [self.name setTextColor:[UIColor blackColor]];
    [self.name setBorderStyle:UITextBorderStyleRoundedRect];
    [self.name setEnabled:TRUE];
    [self.view addSubview:self.name];
    
    self.passwdLabel = [[UILabel alloc] init];
    [self.passwdLabel setText:@"password:"];
    [self.passwdLabel setFrame:CGRectMake(20, 150, 90, 40)];
    [self.view addSubview:self.passwdLabel];
    
    self.passwd = [[UITextField alloc] init];
    [self.passwd setText:@"abcd1234"];
    [self.passwd setFrame:CGRectMake(100, 150, width-120, 40)];
    [self.passwd setTextColor:[UIColor blackColor]];
    [self.passwd setBorderStyle:UITextBorderStyleRoundedRect];
    [self.passwd setEnabled:TRUE];
    [self.view addSubview:self.passwd];
    
    self.logInBtn = [[UIButton alloc] init];
    [self.logInBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.logInBtn setTintColor:[UIColor whiteColor]];
    [self.logInBtn setTitle:@"Login" forState:UIControlStateNormal];
    [self.logInBtn setBackgroundColor:[UIColor grayColor]];
    [self.logInBtn setShowsTouchWhenHighlighted:YES];
    [self.logInBtn setFrame:CGRectMake(width-90, 200, 70, 40)];
    
    [self.logInBtn addTarget:self action:@selector(clickLoginBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.logInBtn];
    
    self.leaveBtn = [[UIButton alloc] init];
    [self.leaveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.leaveBtn setTintColor:[UIColor whiteColor]];
    [self.leaveBtn setTitle:@"leave" forState:UIControlStateNormal];
    [self.leaveBtn setBackgroundColor:[UIColor grayColor]];
    [self.leaveBtn setShowsTouchWhenHighlighted:YES];
    [self.leaveBtn setClipsToBounds:FALSE];
    [self.leaveBtn setFrame:CGRectMake(width-90, 250, 70, 40)];
    [self.leaveBtn addTarget:self
                      action:@selector(clickLeaveBtn:)
            forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.leaveBtn];
}

- (void)clickLoginBtn:(UIButton*) sender{
    NSLog(@"Login!");
    self.logInBtn.enabled = NO;
    //NSString *urlStr = @"ws://192.168.11.123:9001/api/v1/rtc/user/login/name";
    NSString *urlStr = @"ws://192.168.0.105:9001/api/v1/rtc/user/login/name";
    //NSString *urlStr = @"ws://localhost:9001/api/v1/rtc/user/login/name";
    
    NSMutableArray *friendsArr = [NSMutableArray array];
    

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    [parametersDic setObject:self.name.text forKey:@"name"];
    [parametersDic setObject:self.passwd.text forKey:@"passwd"];
    [parametersDic setObject:@"1" forKey:@"type"];
    
    [manager POST:urlStr parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        self.logInBtn.enabled = NO;
        
        NSMutableDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        
        //NSLog(@"%@", resultDic);
        NSLog(@"token = %@", [resultDic valueForKey:@"token"]);
        NSLog(@"friends = %@", [[resultDic valueForKey:@"client"] valueForKey:@"friends"]);
        NSLog(@"client.id = %@", [[resultDic valueForKey:@"client"] valueForKey:@"id"]);

        NSArray *friends = [[resultDic valueForKey:@"client"] valueForKey:@"friends"];
        if ([friends isKindOfClass:[NSArray class]] && friends.count != 0){
            for (NSDictionary *friend in friends){
                NSMutableDictionary *friendDic = [NSMutableDictionary dictionary];
                [friendDic setValue:[friend valueForKey:@"name"] forKey:@"name"];
                [friendDic setValue:[[friend valueForKey:@"id"] valueForKey:@"id"] forKey:@"id"];
                [friendsArr addObject:friendDic];
                NSLog(@"%@", friendsArr);
            }
            [[NSUserDefaults standardUserDefaults] setObject:friendsArr forKey:@"friends"];
        }else{
            NSLog(@"friends list is emmpty");
            [friendsArr addObject:@"you have no friend!"];
            [[NSUserDefaults standardUserDefaults] setObject:friendsArr forKey:@"friends"];
            //[self showError:@"没有好友"];
        }
        //NSLog(@"%@", friendsArr);
        
        [[NSUserDefaults standardUserDefaults] setObject:[resultDic valueForKey:@"token"] forKey:@"token"];
        [[NSUserDefaults standardUserDefaults] setObject:[resultDic valueForKey:@"name"] forKey:@"name"];
        [[NSUserDefaults standardUserDefaults] setObject:[[resultDic valueForKey:@"client"] valueForKey:@"id"] forKey:@"stClientID"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        [self showError:@"登录成功"];
        
        // 成功则返回登录界面
        //[self willMoveToParentViewController:nil];
        //[self.view removeFromSuperview];
        //[self removeFromParentViewController];
        //[self.navigationController popViewControllerAnimated:YES];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [self showError:@"登录失败，请检查用户名和密码是否正确"];
        
        self.logInBtn.enabled = YES;
        NSLog(@"failure");
        NSLog(@"%@", error);
    }];
}


- (void) clickLeaveBtn:(UIButton*) sender {
    
    NSLog(@"Leave login View Controller!");
    [self willMoveToParentViewController:nil];
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
}

- (void)showError:(NSString *)errorMsg {
    // 弹框提醒
    // 初始化对话框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:errorMsg preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
    // 弹出对话框
    [self presentViewController:alert animated:true completion:nil];
}

@end
