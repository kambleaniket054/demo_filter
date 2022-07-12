#import "DemoFilterPlugin.h"
#if __has_include(<demo_filter/demo_filter-Swift.h>)
#import <demo_filter/demo_filter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "demo_filter-Swift.h"
#endif

@implementation DemoFilterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftDemoFilterPlugin registerWithRegistrar:registrar];
}
@end
