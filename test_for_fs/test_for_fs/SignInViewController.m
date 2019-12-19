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

@property (strong, nonatomic) AFManager* AFNet;

@end

@implementation SignInViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    self.AFNet = [[AFManager alloc] init];
    [self showUI];
}

- (void)clickGetAuthCodeBtn:(UIButton*) sender{
    NSLog(@"Getting Authenticode!");
    self.getAuthenticodeBtn.enabled = NO;
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet getAuthCode:self.phoneNum.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self showError:@"验证码已发送！"];
            self.signInBtn.enabled = YES;
        }else{
            [self showError:@"发送失败，请检查网络并重新申请！"];
            self.getAuthenticodeBtn.enabled = YES;
        }
    });
}

- (void)clickSignInBtn:(UIButton*) sender{
    
    self.signInBtn.enabled = NO;

    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet signIn:self.phoneNum.text authCode:self.authenticode.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self showError:@"注册成功！"];
        }else{
            [self showError:@"注册失败，请检查验证码并重新输入！"];
            self.getAuthenticodeBtn.enabled = YES;
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
    //self.signInBtn.enabled = NO;
    
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

- (void)showError:(NSString *)errorMsg {
    // 弹框提醒
    // 初始化对话框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"提示" message:errorMsg preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil]];
    // 弹出对话框
    [self presentViewController:alert animated:true completion:nil];
}
@end

