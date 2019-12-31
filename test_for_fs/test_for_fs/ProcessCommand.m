//
//  ProcessCommand.m
//  test_for_fs
//
//  Created by qians on 2019/12/24.
//  Copyright © 2019 qians. All rights reserved.
//

#import "ProcessCommand.h"

@interface ProcessCommand()

//@property (strong, nonatomic) CallViewController* callView;

@end

@implementation ProcessCommand
static ProcessCommand* m_instance = nil;

+ (ProcessCommand*) getInstance {
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        m_instance = [[self alloc]init];
    });
    
    return m_instance;
}

//- (void) creatCallView:(NSString* )friendId name:(NSString* )name sview:(UIViewController* )sview{
//    NSNumber* uid = [[NSUserDefaults standardUserDefaults] valueForKey:@"id"];
//    NSString* userID = [NSString stringWithFormat:@"%@", uid];
//    self.callView = [[CallViewController alloc] initWithId:friendId userID:userID];
//    [self.callView.view setFrame:sview.view.bounds];
//    [self.callView.view setBackgroundColor:[UIColor whiteColor]];
//    [sview addChildViewController:self.callView];
//    [self.callView didMoveToParentViewController:sview];
//}

//TODO:每当我收到一个request之后，首先回复一个Allow给对方，用来确认我收到了该请求
- (void) doApplyAddCmd: (NSString* )info{
    NSMutableDictionary* applyAddDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyAddDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyAddDic"];
    //NSLog(@"*******************************************");
    NSData* infoData = [info dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* infoDic = [NSJSONSerialization JSONObjectWithData:infoData options:NSJSONReadingMutableLeaves error:nil];
    NSDictionary* sourceInfo = [infoDic valueForKey:@"sourceInfo"];
    NSString* src = [[[sourceInfo valueForKey:@"id"] valueForKey:@"id"] stringValue];
    NSString* name = [sourceInfo valueForKey:@"name"];
    [applyAddDic setValue:name forKey:src];
    [applyAddDic addEntriesFromDictionary:localApplyAddDic];
    //NSLog(@"src = %@", src);
    //NSLog(@"name = %@", name);
    //NSLog(@"applyAddDic = %@", applyAddDic);
    //NSLog(@"*******************************************");
    [[NSUserDefaults standardUserDefaults] setObject:applyAddDic forKey:@"applyAddDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void) doMakeCallCmd:(ProcessCommand* )pCmd friendID:(NSString* )friendID{
    NSMutableDictionary* applyCallDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyCallDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* friendsDic = [NSMutableDictionary dictionary];
    friendsDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"friends"];
    localApplyCallDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyCallDic"];
    //NSString* name = [[NSString alloc] init];
    NSString* name = [friendsDic valueForKey:friendID];
    //NSLog(@"%@", name);
    [applyCallDic setValue:name forKey:friendID];
    [applyCallDic addEntriesFromDictionary:localApplyCallDic];
    //NSLog(@"*********applyCallDic*******%@", applyCallDic);
    [[NSUserDefaults standardUserDefaults] setObject:applyCallDic forKey:@"applyCallDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    //NSLog(@"*********applyCallDic*******");
    if (self.delegate) {
         [self.delegate join:friendID];
    }else{
        NSLog(@"not set delegate");
    }
}

- (void) doAcceptCallCmd:(ProcessCommand* )pCmd friendID:(NSString *)friendID userID:(NSString *)user{
    if (self.delegate) {
         [self.delegate otherjoin:friendID userID:user];
    }else{
        NSLog(@"not set delegate");
    }
}

- (void) doCallOfferCmd:(ProcessCommand* )pCmd sdp:(NSString* )sdp{
    if (self.delegate) {
         [self.delegate offer:sdp];
    }else{
        NSLog(@"not set delegate");
    }
}

- (void) doCallAnswerCmd:(ProcessCommand* )pCmd sdp:(NSString* )sdp{
    if (self.delegate) {
         [self.delegate answer:sdp];
    }else{
        NSLog(@"not set delegate");
    }
}

- (void) doCallCandidateCmd:(ProcessCommand* )pCmd info:(NSString* )info{
    NSData* infoData = [info dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* infoDict = [NSJSONSerialization JSONObjectWithData:infoData options:NSJSONReadingMutableLeaves error:nil];
    
    if (self.delegate) {
         [self.delegate candidate:infoDict];
    }else{
        NSLog(@"not set delegate");
    }
}

-(void) doFull{
    if (self.delegate) {
         [self.delegate full];
    }else{
        NSLog(@"not set delegate");
    }
}

@end
