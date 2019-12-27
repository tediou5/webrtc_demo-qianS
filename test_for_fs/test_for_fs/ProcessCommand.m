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
    [applyAddDic setValue:src forKey:name];
    [applyAddDic addEntriesFromDictionary:localApplyAddDic];
    NSLog(@"src = %@", src);
    NSLog(@"name = %@", name);
    NSLog(@"applyAddDic = %@", applyAddDic);
    NSLog(@"*******************************************");
    [[NSUserDefaults standardUserDefaults] setObject:applyAddDic forKey:@"applyAddDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void) doCallCmd:(NSString* )friendID userID:(NSString* )user{
    [self.delegate joinCall:friendID userID:user];
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
