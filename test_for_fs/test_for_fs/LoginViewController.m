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

@property (strong, nonatomic) AFManager* AFNet;

@end

@implementation LoginViewController


- (void)viewDidLoad{
    [super viewDidLoad];
    
    self.AFNet = [[AFManager alloc] init];
    
    [self showUI];
}

- (void)clickLoginBtn:(UIButton*) sender{
    NSLog(@"Login!");

    self.logInBtn.enabled = NO;
    
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    
    [self.AFNet login:self.name.text passwd:self.passwd.text group:group];
    
    dispatch_group_notify(group, dispatch_get_main_queue(), ^(){
        bool isSuccess = [self.AFNet getIsSuccess];
        if (isSuccess == YES) {
            [self showError:@"登陆成功！"];
            [[NSUserDefaults standardUserDefaults] setObject:self.passwd.text forKey:@"passwd"];
            [NSThread detachNewThreadSelector:@selector(threadMoth) toTarget:self withObject:nil];
            self.logInBtn.enabled = YES;
        }else{
            [self showError:@"登录失败，请检查用户名和密码是否正确"];
            NSLog(@"failure 2");
            self.logInBtn.enabled = YES;
        }
    });
}

- (void)threadMoth {
    while (1) {
        [self.AFNet echo];
        //NSLog(@"i can do a loop over here");
        NSNumber* uuid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
        NSString* uid = [NSString stringWithFormat:@"%@", uuid];
        [self.AFNet getContacts:uid];
        [NSThread sleepForTimeInterval:10];
    }
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

- (void) showUI{
    CGFloat width = self.view.bounds.size.width;
    
    self.nameLabel = [[UILabel alloc] init];
    [self.nameLabel setText:@"name:"];
    [self.nameLabel setFrame:CGRectMake(20, 100, 90, 40)];
    [self.view addSubview:self.nameLabel];
    
    self.name = [[UITextField alloc] init];
    [self.name setText:[[NSUserDefaults standardUserDefaults] valueForKey:@"name"]];
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
    [self.passwd setText:[[NSUserDefaults standardUserDefaults] valueForKey:@"passwd"]];
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
@end
