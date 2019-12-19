//
//  AFManager.m
//  test_for_fs
//
//  Created by qianS on 2019/12/17.
//  Copyright © 2019 qians. All rights reserved.
//

#import "AFManager.h"

@interface AFManager()

@property (strong, nonatomic) NSString* baseUrl;
@property (strong, nonatomic) NSString* loginApi;
@property (strong, nonatomic) NSString* logoutApi;
@property (strong, nonatomic) NSString* authcodeApi;
@property (strong, nonatomic) NSString* signInApi;
@property (strong, nonatomic) NSString* signOutApi;
@property (strong, nonatomic) NSString* searchApi;
@property (strong, nonatomic) NSString* applyAddApi;
@property (strong, nonatomic) NSString* grantAddApi;
@property (strong, nonatomic) NSString* deleteDeviceApi;
@property bool isSuccess;


@end

@implementation AFManager

- (instancetype) init{
    self = [super init];
    
    if(self){
        //self.baseUrl = @"ws://localhost:9001";
        self.baseUrl = @"ws://192.168.0.116:9001";
        self.applyAddApi = @"/api/v1/rtc/contact/applyAdd";
        self.authcodeApi = @"/api/v1/rtc/common/authcode/";
        self.grantAddApi = @"/api/v1/rtc/contact/grantAdd";
        self.loginApi = @"/api/v1/rtc/user/login/name";
        self.logoutApi = @"";
        self.searchApi = @"/api/v1/rtc/contact/search";
        self.signInApi = @"/api/v1/rtc/user/signin";
        self.signOutApi = @"";
        self.deleteDeviceApi = @"/api/v1/rtc/contact/delete";
        //self.isSuccess = YES;
    }
    return self;
}

- (void) login: (NSString* )name passwd:(NSString* )passwd group:(dispatch_group_t)group{
    NSString *Url = [self.baseUrl stringByAppendingString: self.loginApi];
    NSMutableArray *friendsArr = [NSMutableArray array];
    
    NSLog(@"start Login!");
    
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
        NSLog(@"success for login");

        NSMutableDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        
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
        }
        
        [[NSUserDefaults standardUserDefaults] setObject:[resultDic valueForKey:@"token"] forKey:@"token"];
        [[NSUserDefaults standardUserDefaults] setObject:[resultDic valueForKey:@"name"] forKey:@"name"];
        [[NSUserDefaults standardUserDefaults] setObject:[[resultDic valueForKey:@"client"] valueForKey:@"id"] forKey:@"stClientID"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        self.isSuccess = NO;
        NSLog(@"%@", error);
        dispatch_group_leave(group);
    }];
}

- (void) logout: (NSString* )uid group:(dispatch_group_t)group{
    NSString *Url = [self.baseUrl stringByAppendingString: self.logoutApi];

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
       
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:uid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];

    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    [parametersDic setObject:uid forKey:@"uid"];

    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"%@", resultDic);
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (void) signIn: (NSString* )phoneNum authCode:(NSString* )authCode group:(dispatch_group_t)group{
    NSString *Url = [self.baseUrl stringByAppendingString: self.signInApi];
    NSLog(@"start sign in");
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
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (void) getAuthCode: (NSString* )phoneNum group:(dispatch_group_t)group{
    NSLog(@"start get AuthCode");
    NSString* phoneAuth = [self.authcodeApi stringByAppendingString: phoneNum];
    NSString *Url = [self.baseUrl stringByAppendingString: phoneAuth];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];

    [manager GET:Url parameters:nil progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success");
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (NSMutableDictionary* ) search:(NSString* )sid keyword:(NSString* )keyword group:(dispatch_group_t)group{
    NSLog(@"start search!");
    NSString *Url = [self.baseUrl stringByAppendingString: self.searchApi];
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* friendsDic = [NSMutableDictionary dictionary];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
    [manager.requestSerializer setValue:sid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];
    
    [parametersDic setObject:keyword forKey:@"keyword"];
    [parametersDic setObject:@"10" forKey:@"size"];
    [parametersDic setObject:@"1" forKey:@"page"];

    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for searching");
        
        NSMutableDictionary* resultDic = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
        NSMutableArray* resultArr = [resultDic valueForKey:@"contacts"];

        for (NSMutableDictionary *friendDic in resultArr){
            [friendsDic setValue:[[friendDic valueForKey:@"id"] valueForKey:@"id"] forKey:[friendDic valueForKey:@"name"]];
        }
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        self.isSuccess = NO;
        NSLog(@"failure");
        NSLog(@"%@", error);
        dispatch_group_leave(group);
    }];
    return friendsDic;
}

- (void) applyAddDevice: (NSString* )cid sid:(NSString* )sid tid:(NSString* )tid group:(dispatch_group_t)group{
    NSLog(@"start apply add device!");
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
        
    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
            
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for applyAdd");
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (void) grantAddDevice: (NSString* )uid sid:(NSString* )sid tid:(NSString* )tid type:(NSString* )type group:(dispatch_group_t)group{
    NSLog(@"start grant add device!");
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    NSString *Url = [self.baseUrl stringByAppendingString: self.grantAddApi];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
    [manager.requestSerializer setValue:sid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];
    
    [parametersDic setObject:sid forKey:@"uid"];
    [parametersDic setObject:sid forKey:@"sid"];
    [parametersDic setObject:tid forKey:@"tid"];
    [parametersDic setObject:type forKey:@"type"];
    NSLog(@"%@", parametersDic);

    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
            
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for grantAdd");
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (void) deleteDevice: (NSString* )uid cid:(NSString* )cid group:(dispatch_group_t)group{
    NSLog(@"start delete device!");
    NSMutableDictionary *parametersDic = [NSMutableDictionary dictionary];
    NSString *Url = [self.baseUrl stringByAppendingString: self.deleteDeviceApi];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    
    [manager.requestSerializer setValue:uid forHTTPHeaderField:@"client-id"];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"content-type"];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] valueForKey:@"token"] forHTTPHeaderField:@"token"];
    
    [parametersDic setObject:uid forKey:@"uid"];
    [parametersDic setObject:cid forKey:@"cid"];
    NSLog(@"%@", parametersDic);

    [manager POST:Url parameters:parametersDic progress:^(NSProgress * _Nonnull uploadProgress) {
            
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"success for Delete Devide");
        self.isSuccess = YES;
        dispatch_group_leave(group);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
        NSLog(@"%@", error);
        self.isSuccess = NO;
        dispatch_group_leave(group);
    }];
}

- (bool) getIsSuccess{
    return self.isSuccess;
}

@end
