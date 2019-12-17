//
//  AFManager.m
//  test_for_fs
//
//  Created by qianS on 2019/12/17.
//  Copyright © 2019 qians. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AFManager.h"

@interface AFManager()

@property bool isSuccess;

@property (strong, nonatomic) NSString* baseUrl;
@property (strong, nonatomic) NSString* loginApi;
@property (strong, nonatomic) NSString* logoutApi;
@property (strong, nonatomic) NSString* authcodeApi;
@property (strong, nonatomic) NSString* signInApi;
@property (strong, nonatomic) NSString* signOutApi;
@property (strong, nonatomic) NSString* searchApi;
@property (strong, nonatomic) NSString* applyAddApi;
@property (strong, nonatomic) NSString* grantAddApi;



@end

@implementation AFManager

- (instancetype) init{
    self = [super init];
    
    if(self){
        self.baseUrl = @"ws://localhost:9001";
        self.applyAddApi = @"/api/v1/rtc/contact/applyAdd";
        self.authcodeApi = @"/api/v1/rtc/common/authcode/";
        self.grantAddApi = @"/api/v1/rtc/contact/grantAdd";
        self.loginApi = @"/api/v1/rtc/user/login/name";
        self.logoutApi = @"";
        self.searchApi = @"/api/v1/rtc/contact/search";
        self.signInApi = @"/api/v1/rtc/user/signin";
        self.signOutApi = @"";
        self.isSuccess = false;
    }
    return self;
}

- (bool) login: (NSString* )name passwd:(NSString* )passwd{
    NSString *Url = [self.baseUrl stringByAppendingString: self.loginApi];
    NSMutableArray *friendsArr = [NSMutableArray array];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    [parametersDic setObject:name forKey:@"name"];
    [parametersDic setObject:passwd forKey:@"passwd"];
    [parametersDic setObject:@"1" forKey:@"type"];
    
    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        //self.logInBtn.enabled = NO;
        
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
        
        self.isSuccess = true;
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = false;
    }];
    return self.isSuccess;
}

- (bool) signIn: (NSString* )phoneNum authCode:(NSString* )authCode{
    NSString *Url = [self.baseUrl stringByAppendingString: self.signInApi];
    //[Url stringByAppendingString:phoneNum];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
       
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    [parametersDic setObject:phoneNum forKey:@"phone"];
    [parametersDic setObject:authCode forKey:@"authCode"];

    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"%@", resultDic);
        self.isSuccess = true;
           //NSLog(@"token = %@", [resultDic valueForKey:@"token"]);
           // 成功则返回登录界面
           //[self.navigationController popViewControllerAnimated:YES];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
           
        //self.signInBtn.enabled = YES;
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = false;
    }];
    return self.isSuccess;
}

- (bool) getAuthCode: (NSString* )phoneNum{
    NSString *Url = [self.baseUrl stringByAppendingString: self.authcodeApi];
    [Url stringByAppendingString:phoneNum];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    [manager GET:Url parameters:nil progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //self.signInBtn.enabled = YES;
        NSLog(@"success");
        self.isSuccess = true;
        // 成功则返回登录界面
        //[self.navigationController popViewControllerAnimated:YES];
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        //self.getAuthenticodeBtn.enabled = YES;
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = false;
    }];
    return self.isSuccess;
}

- (bool) applyAddDevice: (NSString* )cid sid:(NSString* )sid tid:(NSString* )tid{
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    NSString *Url = [self.baseUrl stringByAppendingString: self.applyAddApi];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
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
        
    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
            
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for applyAdd");
        self.isSuccess = true;
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = false;
    }];
    return self.isSuccess;
}

@end
