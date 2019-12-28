//
//  ProcessCommand.m
//  test_for_fs
//
//  Created by qians on 2019/12/24.
//  Copyright © 2019 qians. All rights reserved.
//

#import "ProcessCommand.h"

@interface ProcessCommand()



@end

@implementation ProcessCommand

- (void) doApplyAddCmd: (NSString* )info{
    NSMutableDictionary* applyAddDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyAddDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyAddDic"];
    NSLog(@"*******************************************");
    NSData* infoData = [info dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* infoDic = [NSJSONSerialization JSONObjectWithData:infoData options:NSJSONReadingMutableLeaves error:nil];
    NSDictionary* sourceInfo = [infoDic valueForKey:@"sourceInfo"];
    NSString* src = [[[sourceInfo valueForKey:@"id"] valueForKey:@"id"] stringValue];
    NSString* name = [sourceInfo valueForKey:@"name"];
    [applyAddDic setValue:name forKey:src];
    [applyAddDic addEntriesFromDictionary:localApplyAddDic];
    NSLog(@"src = %@", src);
    NSLog(@"name = %@", name);
    NSLog(@"applyAddDic = %@", applyAddDic);
    NSLog(@"*******************************************");
    [[NSUserDefaults standardUserDefaults] setObject:applyAddDic forKey:@"applyAddDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void) doMakeCallCmd: (NSString* )friendID{
    NSMutableDictionary* applyCallDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* localApplyCallDic = [NSMutableDictionary dictionary];
    NSMutableDictionary* friendsDic = [NSMutableDictionary dictionary];
    friendsDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"friends"];
    localApplyCallDic = [[NSUserDefaults standardUserDefaults] valueForKey:@"applyCallDic"];
    //NSString* name = [[NSString alloc] init];
    
    NSString* name = [friendsDic valueForKey:friendID];
    NSLog(@"%@", name);

    [applyCallDic setValue:name forKey:friendID];
    [applyCallDic addEntriesFromDictionary:localApplyCallDic];
    NSLog(@"*********applyCallDic*******%@", applyCallDic);
    [[NSUserDefaults standardUserDefaults] setObject:applyCallDic forKey:@"applyCallDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    NSLog(@"*********applyCallDic*******");
    [self.delegate join:friendID];
}

- (void) processMakeCallCmd: (NSString* )friendID{
    [self.delegate join:friendID];
}

- (void) doAcceptCallCmd:(NSString* )friendID userID:(NSString* )user{
    [self.delegate otherjoin:friendID userID:user];
}

- (void) doCallOfferCmd: (NSString* )sdp{
    [self.delegate offer:sdp];
}

- (void) doCallAnswerCmd: (NSString* )sdp{
    [self.delegate answer:sdp];
}

- (void) doCallCandidateCmd: (NSString* )info{
    NSData* infoData = [info dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary* infoDict = [NSJSONSerialization JSONObjectWithData:infoData options:NSJSONReadingMutableLeaves error:nil];
    
    [self.delegate candidate:infoDict];
}

-(void) doFull{
    [self.delegate full];
}

- (void) doJoin: (NSString* )friendId{
    [self.delegate join:friendId];
}
@end
