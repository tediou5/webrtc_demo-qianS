//
//  SignInViewController.m
//  test_for_fs
//
//  Created by qians on 2019/11/26.
//  Copyright © 2019 qians. All rights reserved.
//

#import "SignInViewController.h"


@interface SignInViewController()

@property (strong, nonatomic) UILabel* phoneNumLabel;
@property (strong, nonatomic) UITextField* phoneNum;
@property (strong, nonatomic) UIButton* getAuthenticodeBtn;

@property (strong, nonatomic) UILabel* authenticodeLabel;
@property (strong, nonatomic) UITextField* authenticode;
@property (strong, nonatomic) UIButton* signInBtn;

@property (strong, nonatomic) UIButton* leaveBtn;

@end

@implementation SignInViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    CGFloat width = self.view.bounds.size.width;
    
    self.phoneNumLabel = [[UILabel alloc] init];
    [self.phoneNumLabel setText:@"PhoneNum:"];
    [self.phoneNumLabel setFrame:CGRectMake(20, 100, 110, 40)];
    [self.view addSubview:self.phoneNumLabel];
    
    self.phoneNum = [[UITextField alloc] init];
    [self.phoneNum setText:@"18552513141"];
    [self.phoneNum setFrame:CGRectMake(120, 100, width-160, 40)];
    [self.phoneNum setTextColor:[UIColor blackColor]];
    [self.phoneNum setBorderStyle:UITextBorderStyleRoundedRect];
    [self.phoneNum setEnabled:TRUE];
    [self.view addSubview:self.phoneNum];
    
    self.getAuthenticodeBtn = [[UIButton alloc] init];
    [self.getAuthenticodeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.getAuthenticodeBtn setTintColor:[UIColor whiteColor]];
    [self.getAuthenticodeBtn setTitle:@"Get Authenticode" forState:UIControlStateNormal];
    [self.getAuthenticodeBtn setBackgroundColor:[UIColor grayColor]];
    [self.getAuthenticodeBtn setShowsTouchWhenHighlighted:YES];
    [self.getAuthenticodeBtn setFrame:CGRectMake(20, 150, width-40, 40)];
    
    [self.getAuthenticodeBtn addTarget:self action:@selector(clickGetAuthCodeBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.getAuthenticodeBtn];
    
    self.authenticodeLabel = [[UILabel alloc] init];
    [self.authenticodeLabel setText:@"Authenticode:"];
    [self.authenticodeLabel setFrame:CGRectMake(20, 200, 110, 40)];
    [self.view addSubview:self.authenticodeLabel];
    
    self.authenticode = [[UITextField alloc] init];
    [self.authenticode setText:@""];
    [self.authenticode setFrame:CGRectMake(140, 200, width-240, 40)];
    [self.authenticode setTextColor:[UIColor blackColor]];
    [self.authenticode setBorderStyle:UITextBorderStyleRoundedRect];
    [self.authenticode setEnabled:TRUE];
    [self.view addSubview:self.authenticode];
    
    self.signInBtn = [[UIButton alloc] init];
    [self.signInBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.signInBtn setTintColor:[UIColor whiteColor]];
    [self.signInBtn setTitle:@"Sign In" forState:UIControlStateNormal];
    [self.signInBtn setBackgroundColor:[UIColor grayColor]];
    [self.signInBtn setShowsTouchWhenHighlighted:YES];
    [self.signInBtn setFrame:CGRectMake(width-90, 200, 70, 40)];
    self.signInBtn.enabled = NO;
    
    [self.signInBtn addTarget:self action:@selector(clickSignInBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.signInBtn];
    
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

- (void)clickGetAuthCodeBtn:(UIButton*) sender{
    NSLog(@"Getting Authenticode!");
    self.getAuthenticodeBtn.enabled = NO;
    //NSMutableString *urlStr = [[NSMutableString alloc]initWithString:@"ws://192.168.11.123:9001/api/v1/rtc/common/authcode/"];
    NSMutableString *urlStr = [[NSMutableString alloc]initWithString:@"ws://localhost:9001/api/v1/rtc/common/authcode/"];
    [urlStr appendString:_phoneNum.text];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    [manager GET:urlStr parameters:nil progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        self.signInBtn.enabled = YES;
        NSLog(@"success");
        // 成功则返回登录界面
        //[self.navigationController popViewControllerAnimated:YES];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        self.getAuthenticodeBtn.enabled = YES;
        NSLog(@"failure");
        NSLog(@"%@", error);
    }];
}

- (void)clickSignInBtn:(UIButton*) sender{
    
    self.signInBtn.enabled = NO;
    
    //NSString *urlStr = @"ws://192.168.11.123:9001/api/v1/rtc/user/signin";
    //NSString *urlStr = @"ws://192.168.0.105:9001/api/v1/rtc/user/signin";
    NSString *urlStr = @"ws://localhost:9001/api/v1/rtc/user/signin";

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    [parametersDic setObject:self.phoneNum.text forKey:@"phone"];
    [parametersDic setObject:self.authenticode.text forKey:@"authCode"];

    [manager POST:urlStr parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"%@", resultDic);
        //NSLog(@"token = %@", [resultDic valueForKey:@"token"]);
        // 成功则返回登录界面
        //[self.navigationController popViewControllerAnimated:YES];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        self.signInBtn.enabled = YES;
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

@end

