#import <xr_udid/XrUdidPlugin.h>
#import "SAMKeychain.h"
#import <sys/utsname.h>

@implementation XrUdidPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"xr_udid"
            binaryMessenger:[registrar messenger]];
  XrUdidPlugin* instance = [[XrUdidPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"xr_getUUID" isEqualToString:call.method]) {
      result([XrUdidPlugin deviceUUID]);
    }else if([@"xr_getDevice" isEqualToString:call.method]) {
        result([XrUdidPlugin getCurrentDeviceModel]);
    } else {
      result(FlutterMethodNotImplemented);
    }
}
+ (NSString *)getCurrentDeviceModel{
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString *deviceModel = [NSString stringWithCString:systemInfo.machine encoding:NSASCIIStringEncoding];
    return deviceModel;
}


+ (NSString *)deviceUUID {
    NSString *appInfoAccount = @"key_app_info_account";
    NSString *keyDeviceID = @"key_device_uuid";
    
    NSString *resultString = [SAMKeychain passwordForService:keyDeviceID account:appInfoAccount];
    if (resultString == nil) {
        CFUUIDRef puuid = CFUUIDCreate( nil );
        CFStringRef uuidStringRef = CFUUIDCreateString( nil, puuid );
        NSString *uuidString = CFBridgingRelease(uuidStringRef);
        CFRelease(puuid);
        
        if ([uuidString length] > 0) {
            [SAMKeychain setPassword:uuidString forService:keyDeviceID account:appInfoAccount];
            resultString = uuidString;
        }
    }
    return resultString;
}
@end
