#import "RNJWPlayerNativeView.h"
#import "RNJWPlayerDelegateProxy.h"
#import <AVFoundation/AVFoundation.h>

NSString* const AudioInterruptionsStarted = @"AudioInterruptionsStarted";
NSString* const AudioInterruptionsEnded = @"AudioInterruptionsEnded";

@implementation RNJWPlayerNativeView

- (id)init {
    self = [super init];
    
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioInterruptionsStarted:) name:AudioInterruptionsStarted object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioInterruptionsEnded:) name:AudioInterruptionsEnded object:nil];
        self.isNextUpBtnVisible = false;
    }
    
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:AudioInterruptionsStarted];
    [[NSNotificationCenter defaultCenter] removeObserver:AudioInterruptionsEnded];
}

-(void)customStyle: (JWConfig*)config :(NSString*)name
{
    config.stretching = JWStretchingUniform;
    
    JWSkinStyling *skinStyling = [JWSkinStyling new];
    config.skin = skinStyling;
    
    skinStyling.url = [NSString stringWithFormat:@"file://%@", [[NSBundle mainBundle] pathForResource:name ofType:@"css"]];
    skinStyling.name = name;
}

-(UIColor*)colorWithHexString:(NSString*)hex
{
    NSString *cString = [[hex stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    // String should be 6 or 8 characters
    if ([cString length] < 6) return [UIColor grayColor];
    
    // strip 0X if it appears
    if ([cString hasPrefix:@"0X"]) cString = [cString substringFromIndex:2];
    
    if ([cString length] != 6) return  [UIColor grayColor];
    
    // Separate into r, g, b substrings
    NSRange range;
    range.location = 0;
    range.length = 2;
    NSString *rString = [cString substringWithRange:range];
    
    range.location = 2;
    NSString *gString = [cString substringWithRange:range];
    
    range.location = 4;
    NSString *bString = [cString substringWithRange:range];
    
    // Scan values
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r / 255.0f)
                           green:((float) g / 255.0f)
                            blue:((float) b / 255.0f)
                           alpha:1.0f];
}

-(void)setNativeFullScreen:(BOOL)nativeFullScreen
{
    _nativeFullScreen = nativeFullScreen;
}

-(void)setFullScreenOnLandscape:(BOOL)fullScreenOnLandscape
{
    _fullScreenOnLandscape = fullScreenOnLandscape;
    
    if (_player) {
        _player.forceFullScreenOnLandscape = fullScreenOnLandscape;
    }
}

-(void)setLandscapeOnFullScreen:(BOOL)landscapeOnFullScreen
{
    _landscapeOnFullScreen = landscapeOnFullScreen;
    
    if (_player) {
        _player.forceLandscapeOnFullScreen = landscapeOnFullScreen;
    }
}

-(void)setColors: (NSDictionary *)colors
{
    if (colors != nil) {
        _playerColors = colors;
        
        if (_player != nil) {
            [self setupColors:_player.config];
        }
    }
}

-(void)setupColors: (JWConfig *)config
{
    if (_playerColors != nil) {
        config.stretching = JWStretchingUniform;
        
        JWSkinStyling *skinStyling = [JWSkinStyling new];
        config.skin = skinStyling;
        
        if ([_playerColors objectForKey:@"icons"] != nil) {
            id icons = [_playerColors objectForKey:@"icons"];
            
            JWControlbarStyling *controlbarStyling = [JWControlbarStyling new];
            controlbarStyling.icons = [self colorWithHexString:icons];
            skinStyling.controlbar = controlbarStyling;
        }
        
        if ([_playerColors objectForKey:@"timeslider"] != nil) {
            JWTimesliderStyling *timesliderStyling = [JWTimesliderStyling new];
            
            id timeslider = [_playerColors objectForKey:@"timeslider"];
            
            if ([timeslider objectForKey:@"progress"] != nil) {
                id progress = [timeslider objectForKey:@"progress"];
                timesliderStyling.progress = [self colorWithHexString:progress];
            }
            
            if ([timeslider objectForKey:@"rail"] != nil) {
                id rail = [timeslider objectForKey:@"rail"];
                timesliderStyling.rail = [self colorWithHexString:rail];
            }
            
            skinStyling.timeslider = timesliderStyling;
        }
    }
}

-(JWConfig*)setupConfig
{
    JWConfig *config = [JWConfig new];
    
    config.controls = YES;
    config.repeat = NO;
    config.displayDescription = YES;
    config.displayTitle = YES;
    
    return config;
}

-(void)setPlayerStyle:(NSString *)playerStyle
{
    if (playerStyle != nil) {
        _playerStyle = playerStyle;
    }
}

-(void)setFile:(NSString *)file
{
    NSString* encodedUrl = [file stringByAddingPercentEscapesUsingEncoding:
                            NSUTF8StringEncoding];
    if (file != nil && file.length > 0 && ![encodedUrl isEqualToString:_player.config.file]) {
        self.player.config.file = encodedUrl;
    }
}

-(NSString *)file
{
    return self.player.config.file;
}

-(void)setMediaId:(NSString *)mediaId
{
    if(mediaId != nil && mediaId.length > 0 && ![mediaId isEqualToString:_player.config.mediaId]) {
        self.player.config.mediaId = mediaId;
    }
}

-(NSString *)mediaId
{
    return self.player.config.mediaId;
}

-(void)setTitle:(NSString *)title
{
    if(title != nil && title.length > 0 && ![title isEqualToString:_player.config.title]) {
        self.player.config.title = title;
    }
}

-(NSString *)title
{
    return self.player.config.title;
}

-(void)setImage:(NSString *)image
{
    if(image != nil && image.length > 0 && ![image isEqualToString:_player.config.image]) {
        self.player.config.image = image;
    }
}

-(NSString *)image
{
    return self.player.config.image;
}

-(void)setDesc:(NSString *)desc
{
    if(desc != nil && desc.length > 0 && ![desc isEqualToString:_player.config.desc]) {
        self.player.config.desc = desc;
    }
}

-(NSString *)desc
{
    return self.player.config.desc;
}

-(void)setAutostart:(BOOL)autostart
{
    if(autostart != self.player.config.autostart) {
        self.player.config.autostart = autostart;
    }
}

-(BOOL)autostart
{
    return self.player.config.autostart;
}

-(void)setControls:(BOOL)controls
{
    if(controls != self.player.controls) {
        self.player.config.controls = controls;
        self.player.controls = controls;
    }
}

-(BOOL)controls
{
    return self.player.controls;
}

-(void)setRepeat:(BOOL)repeat
{
    if(repeat != self.player.config.repeat) {
        self.player.config.repeat = repeat;
    }
}

-(BOOL)repeat
{
    return self.player.config.repeat;
}

-(void)setDisplayDesc:(BOOL)displayDesc
{
    if(displayDesc != self.player.config.displayDescription) {
        self.player.config.displayDescription = displayDesc;
    }
}

-(BOOL)displayDesc
{
    return self.player.config.displayDescription;
}

-(void)setDisplayTitle:(BOOL)displayTitle
{
    if(displayTitle != self.player.config.displayTitle) {
        self.player.config.displayTitle = displayTitle;
    }
}

-(BOOL)displayTitle
{
    return self.player.config.displayTitle;
}

-(void)setNextUpDisplay:(BOOL)nextUpDisplay
{
    self.player.config.nextUpDisplay = nextUpDisplay;
}

-(BOOL)nextUpDisplay
{
    return self.player.config.nextUpDisplay;
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    
    if (self.player != nil) {
        self.player.view.frame = self.frame;
    }
    
    if (_initFrame.size.height == 0) {
        _initFrame = self.frame;
    }
}

- (void)removeFromSuperview {
    [self reset];
    [super removeFromSuperview];
}

-(BOOL)shouldAutorotate {
    return NO;
}

-(void)addObserevers
{
    [[NSNotificationCenter defaultCenter] removeObserver:AudioInterruptionsStarted];
    [[NSNotificationCenter defaultCenter] removeObserver:AudioInterruptionsEnded];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioInterruptionsStarted:) name:AudioInterruptionsStarted object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioInterruptionsEnded:) name:AudioInterruptionsEnded object:nil];
}

-(void)setPlaylistItem:(NSDictionary *)playlistItem
{
    NSString *newFile = [playlistItem objectForKey:@"file"];
    
    if (newFile != nil && newFile.length > 0) {
        [self reset];
        
        JWConfig *config = [self setupConfig];
        
        if (_playerStyle) {
            [self customStyle:config :_playerStyle];
        } else if ([playlistItem objectForKey:@"playerStyle"]) {
            [self customStyle:config :[playlistItem objectForKey:@"playerStyle"]];
        } else if (_playerColors != nil) {
             [self setupColors:config];
        }
        
        NSURL* url = [NSURL URLWithString:newFile];
        if (url && url.scheme && url.host) {
            config.file = newFile;
        } else {
            NSString* encodedUrl = [newFile stringByAddingPercentEscapesUsingEncoding:
            NSUTF8StringEncoding];
            config.file = encodedUrl;
        }
        
        id mediaId = playlistItem[@"mediaId"];
        if ((mediaId != nil) && (mediaId != (id)[NSNull null])) {
            config.mediaId = mediaId;
        }
        
        id title = playlistItem[@"title"];
        if ((title != nil) && (title != (id)[NSNull null])) {
            config.title = title;
        }
        
        id desc = playlistItem[@"desc"];
        if ((desc != nil) && (desc != (id)[NSNull null])) {
            config.desc = desc;
        }
        
        id image = playlistItem[@"image"];
        if ((image != nil) && (image != (id)[NSNull null])) {
            config.image = image;
        }
        
        id autostart = playlistItem[@"autostart"];
        if ((autostart != nil) && (autostart != (id)[NSNull null])) {
            config.autostart = [autostart boolValue];
        }
        
        id controls = playlistItem[@"controls"];
        if ((controls != nil) && (controls != (id)[NSNull null])) {
            config.controls = [controls boolValue];
        }

        NSMutableArray <JWAdBreak *> *adsArray = [[NSMutableArray alloc] init];
        id ads = playlistItem[@"schedule"];
        if(ads != nil) {
            NSArray* adsAr = (NSArray*)ads;
            if(adsAr.count > 0) {
                for (id item in adsAr) {
                    NSString *offset = [item objectForKey:@"offset"];
                    NSString *tag = [item objectForKey:@"tag"];
                    JWAdBreak *adBreak = [JWAdBreak adBreakWithTag:tag offset:offset];
                    [adsArray addObject:adBreak];
                }
            }
        }

        if(adsArray.count > 0) {
            JWAdConfig* advertising = [JWAdConfig new];
            advertising.client = JWAdClientGoogima;

            advertising.schedule = adsArray;
            config.advertising = advertising;
        }
        
        _proxy = [RNJWPlayerDelegateProxy new];
        _proxy.delegate = self;
        
        _player = [[JWPlayerController alloc] initWithConfig:config delegate:_proxy];
        _player.controls = [[playlistItem objectForKey:@"controls"] boolValue];
    
        [_player setFullscreen:true];
        
        [self setFullScreenOnLandscape:_fullScreenOnLandscape];
        [self setLandscapeOnFullScreen:_landscapeOnFullScreen];
        
        [self addSubview:self.player.view];
        
        if (config.title != nil) {
            UILabel *titleLabel = [self setupTitleLabel:config.title];
            UIView *jwPlayerInternalView = self.player.view.subviews[0];
            self.jwPlayerInternalView = jwPlayerInternalView;
            [jwPlayerInternalView addSubview:titleLabel];
        }
    }
}

-(UILabel *)setupTitleLabel:(NSString *)title
{
    // create the label
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 10, 10, 10)];
    titleLabel.backgroundColor = [UIColor blackColor];
    titleLabel.textColor = [UIColor whiteColor];
    [titleLabel setText:title];
    [titleLabel setNumberOfLines:1];
    titleLabel.textAlignment = NSTextAlignmentCenter;

    // calculate size of the text to be rendered
    CGSize expectedLabelSize = [[titleLabel text] sizeWithAttributes:@{NSFontAttributeName:titleLabel.font}];
    CGRect frame = titleLabel.frame;
    
    // set the correct size of the label
    // added integers add a little bit of padding
    frame.size.height = expectedLabelSize.height + 6;
    frame.size.width = expectedLabelSize.width + 8;
    titleLabel.frame = frame;
    
    self.titleLabel = titleLabel;
    
    return titleLabel;
}

-(UIButton *)setupNextUpBtn
{
    // create the button
    int btnWidth = 100;
    int btnHeight = 50;
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(100, 100, btnWidth, btnHeight)];
    btn.backgroundColor = [UIColor blackColor];
    btn.tintColor = [UIColor whiteColor];
    [btn setTitle:@"NEXT UP" forState:UIControlStateNormal];
    
    // get screen dimensions
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat screenWidth = screenRect.size.width;
    CGFloat screenHeight = screenRect.size.height;
    // calculate the position of the button so it's bottom-right on the screen
    CGRect frame = CGRectMake(screenWidth - (btnWidth + 20), screenHeight - (btnHeight + 60), btnWidth, btnHeight);
    btn.frame = frame;
    
    return btn;
}

-(void)resetPlaylistItem
{
    self.playlistItem = nil;
}

-(void)reset
{
    _player = nil;
    _proxy = nil;
}

-(void)setPlaylist:(NSArray *)playlist
{
    if (playlist != nil && playlist.count > 0) {
        [self reset];
        
        self.originalPlaylist = playlist;
        
        NSMutableArray <JWPlaylistItem *> *playlistArray = [[NSMutableArray alloc] init];
        for (id item in playlist) {
            JWPlaylistItem *playListItem = [JWPlaylistItem new];
            
            NSString *newFile = [item objectForKey:@"file"];
            
            NSURL* url = [NSURL URLWithString:newFile];
            if (url && url.scheme && url.host) {
                playListItem.file = newFile;
            } else {
                NSString* encodedUrl = [newFile stringByAddingPercentEscapesUsingEncoding:
                NSUTF8StringEncoding];
                playListItem.file = encodedUrl;
            }
            
            id mediaId = item[@"mediaId"];
            if ((mediaId != nil) && (mediaId != (id)[NSNull null])) {
                playListItem.mediaId = mediaId;
            }
            
            id title = item[@"title"];
            if ((title != nil) && (title != (id)[NSNull null])) {
                playListItem.title = title;
            }
            
            id desc = item[@"desc"];
            if ((desc != nil) && (desc != (id)[NSNull null])) {
                playListItem.desc = desc;
            }
            
            id image = item[@"image"];
            if ((image != nil) && (image != (id)[NSNull null])) {
                playListItem.image = image;
            }
            
            [playlistArray addObject:playListItem];
        }
        
        JWConfig *config = [self setupConfig];
        
        if (_playerStyle != nil) {
            [self customStyle:config :_playerStyle];
        } else if (_playerColors != nil) {
            [self setupColors:config];
        }
        
        config.autostart = [[playlist[0] objectForKey:@"autostart"] boolValue];
        config.nextupOffset = [[playlist[0] objectForKey:@"nextUpOffset"] intValue];
        config.playlist = playlistArray;
        
        _proxy = [RNJWPlayerDelegateProxy new];
        _proxy.delegate = self;
        
        _player = [[JWPlayerController alloc] initWithConfig:config delegate:_proxy];
        
        _player.controls = YES;

        [_player setFullscreen:true];
        
        [self setFullScreenOnLandscape:_fullScreenOnLandscape];
        [self setLandscapeOnFullScreen:_landscapeOnFullScreen];
                
        [self addSubview:self.player.view];
    }
}

#pragma mark - RNJWPlayer Delegate

-(void)onRNJWReady
{
    if (self.onPlayerReady) {
        self.onPlayerReady(@{});
    }
}

-(void)onRNJWPlaylist
{
    if (self.onPlaylist) {
        self.onPlaylist(@{});
    }
}

-(void)onRNJWPlayerBeforePlay
{
    if (self.onBeforePlay) {
        self.onBeforePlay(@{});
    }
}

-(void)onRNJWPlayerPlay
{
    if (self.onPlay) {
        self.onPlay(@{});
    }
}

-(void)onRNJWPlayerPause
{
    if (self.onPause) {
        self.onPause(@{});
    }
}

-(void)onRNJWPlayerBuffer
{
    if (self.onBuffer) {
        self.onBuffer(@{});
    }
}

-(void)onRNJWPlayerIdle
{
    if (self.onIdle) {
        self.onIdle(@{});
    }
}

-(void)onRNJWPlayerPlaylistItem:(JWEvent<JWPlaylistItemEvent> *)event
{
    NSNumber *index;
    index = [NSNumber numberWithInteger: event.index];
    NSNumber *nextUpOffset;
    nextUpOffset = [[self.originalPlaylist objectAtIndex:event.index] valueForKey:@"nextUpOffset"];
            
    if (nextUpOffset != nil) {
        self.player.config.nextupOffset = [nextUpOffset intValue];
    }

    if (self.onPlaylistItem) {
        NSError *error;
        NSString *file = @"";
        NSString *mediaId = @"";
        NSString *title = @"";
        NSString *desc = @"";
        
        if (event.item.file != nil) {
            file = event.item.file;
        }
        
        if (event.item.mediaId != nil) {
            mediaId = event.item.mediaId;
        }
        
        if (event.item.title != nil) {
            title = event.item.title;
        }
        
        if (event.item.desc != nil) {
            desc = event.item.desc;
        }
        
        NSMutableDictionary *playListItemDict = [[NSMutableDictionary alloc] init];
        [playListItemDict setObject:file forKey:@"file"];
        [playListItemDict setObject:mediaId forKey:@"mediaId"];
        [playListItemDict setObject:title forKey:@"title"];
        [playListItemDict setObject:desc forKey:@"desc"];
        [playListItemDict setObject:index forKey:@"index"];
        
        NSData *data = [NSJSONSerialization dataWithJSONObject:playListItemDict options:NSJSONWritingPrettyPrinted error: &error];
        
        self.onPlaylistItem(@{@"playlistItem": [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding]});
    }
}

-(void)onRNJWSetupPlayerError:(JWEvent<JWErrorEvent> *)event
{
    if (self.onSetupPlayerError) {
        self.onSetupPlayerError(@{@"error": event.error});
    }
}

-(void)onRNJWPlayerError:(JWEvent<JWErrorEvent> *)event
{
    if (self.onPlayerError) {
        self.onPlayerError(@{@"error": event.error});
    }
}

-(void)onRNJWPlayerTime:(JWEvent<JWTimeEvent> *)event
{
    // prevent zero division
    if (event.duration > 1) {
        if (event.position / event.duration * 100 > 90 && !self.isNextUpBtnVisible) {
            self.isNextUpBtnVisible = true;
            UIButton *btn = [self setupNextUpBtn];
            [self.jwPlayerInternalView addSubview:btn];
        }
    }

    if (self.onTime) {
        NSLog(@"position: %@",@(event.position));
        self.onTime(@{@"position": @(event.position), @"duration": @(event.duration)});
        //        [[NSUserDefaults standardUserDefaults] setObject:@(event.position) forKey:@"PlayerTime"];
    }
}

-(void)onRNJWFullScreen:(JWEvent<JWFullscreenEvent> *)event
{
    if(event && [[event valueForKey:@"_fullscreen"] boolValue]){
        if (self.onFullScreen) {
            self.onFullScreen(@{});
        }
    }else{
        if (self.onFullScreenExit){
            if (_nativeFullScreen) {
                [[UIDevice currentDevice] setValue: [NSNumber numberWithInteger: UIInterfaceOrientationPortrait] forKey:@"orientation"];
            }
            self.onFullScreenExit(@{});
        }
    }
    
}

-(void)explode
{
    CGRect rect = CGRectMake(0, 0, [[UIScreen mainScreen] applicationFrame].size.width, [[UIScreen mainScreen] applicationFrame].size.height);
    
    self.frame = rect;
    
    [self setBackgroundColor:[UIColor blackColor]];
}

-(void)shrink
{
    self.frame = _initFrame;
    
    [self setBackgroundColor:[UIColor whiteColor]];
}

-(void)onRNJWFullScreenRequested:(JWEvent<JWFullscreenEvent> *)event
{
    if ([[event valueForKey:@"_fullscreen"] boolValue]) {
        if (self.onFullScreenRequested) {
            self.onFullScreenRequested(@{});
        }
        
        if (_nativeFullScreen) {
            [self explode];
        }
    } else {
        if (self.onFullScreenExitRequested) {
            self.onFullScreenExitRequested(@{});
        }
        
        if (_nativeFullScreen) {
            [self shrink];
        }
    }
}

-(void)onRNJWFullScreenExitRequested:(JWEvent<JWFullscreenEvent> *)event
{
    if (self.onFullScreenExitRequested) {
        self.onFullScreenExitRequested(@{});
    }
}

-(void)onRNJWFullScreenExit:(JWEvent<JWFullscreenEvent> *)event
{
    if (self.onFullScreenExit){
        self.onFullScreenExit(@{});
    }
}

-(void)onRNJWPlayerSeek:(JWEvent<JWSeekEvent> *)event
{
    if (self.onSeek) {
        self.onSeek(@{});
    }
}

-(void)onRNJWControlBarVisible:(JWEvent<JWControlsEvent> *)event
{
    if (self.titleLabel != nil) {
        self.titleLabel.alpha = event.controls ? 1.0 : 0.0;
    }
    if (self.onControlBarVisible) {
        self.onControlBarVisible(@{@"controls": @(event.controls)});
    }
}

-(void)onRNJWPlayerBeforeComplete
{
    if (self.onBeforeComplete) {
        self.onBeforeComplete(@{});
    }
}

-(void)onRNJWPlayerComplete
{
    if (self.onComplete) {
        self.onComplete(@{});
    }
}

-(void)onRNJWPlayerAdPlay:(JWAdEvent<JWAdStateChangeEvent>*)event
{
    if (self.onAdPlay) {
        self.onAdPlay(@{});
    }
}


//-(void)onRN

#pragma mark - RNJWPlayer Interruption handling

-(void)audioInterruptionsStarted:(NSNotification *)note {
    if (self.onPause) {
        self.onPause(@{});
    }
    
    [self.player pause];
}

-(void)audioInterruptionsEnded:(NSNotification *)note {
    if (self.onPlay) {
        self.onPlay(@{});
    }
    
    [self.player play];
}

@end
