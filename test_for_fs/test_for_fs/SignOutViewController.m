//
//  UIViewController+SignOutViewController.m
//  test_for_fs
//
//  Created by qians on 2019/11/26.
//  Copyright © 2019 qians. All rights reserved.
//

#import "SignOutViewController.h"


@interface SignOutViewController()

@property (strong, nonatomic) UIButton* leaveBtn;
@property (strong, nonatomic) UIViewController* mainView;

@end

@implementation SignOutViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    self.leaveBtn = [[UIButton alloc] init];
    [self.leaveBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.leaveBtn setTintColor:[UIColor whiteColor]];
    [self.leaveBtn setTitle:@"leave" forState:UIControlStateNormal];
    [self.leaveBtn setBackgroundColor:[UIColor grayColor]];
    [self.leaveBtn setShowsTouchWhenHighlighted:YES];
    [self.leaveBtn.layer setCornerRadius:40];
    [self.leaveBtn.layer setBorderWidth:1];
    [self.leaveBtn setClipsToBounds:FALSE];
    [self.leaveBtn setFrame:CGRectMake(self.view.bounds.size.width/2-40,
                                       self.view.bounds.size.height-140,
                                       80,
                                       80)];
    [self.leaveBtn addTarget:self
                      action:@selector(clickLeaveBtn:)
            forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.leaveBtn];
}

- (void) clickLeaveBtn:(UIButton*) sender {
    [self.delegate signOutAFNet];
    //    NSLog(@"Leave Sign Out View Controller!");
    //    [self willMoveToParentViewController:nil];
    //    [self.view removeFromSuperview];
    //    [self removeFromParentViewController];
}

- (UIViewController *)getParentController:(UIView *)view{
    for (UIView* next = [view superview];next; next = next.superview) {
        UIResponder *nextResponder = [next nextResponder];
        if ([nextResponder isKindOfClass:[UIViewController class]]) {
            return (UIViewController *)nextResponder;
        }
    }
    
    return nil;
    
}
@end
