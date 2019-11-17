package net.gamesofton.rnjwplayer;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.configuration.SkinConfig;
import com.longtailvideo.jwplayer.events.AudioTrackChangedEvent;
import com.longtailvideo.jwplayer.events.AudioTracksEvent;
import com.longtailvideo.jwplayer.events.BeforeCompleteEvent;
import com.longtailvideo.jwplayer.events.BeforePlayEvent;
import com.longtailvideo.jwplayer.events.BufferEvent;
import com.longtailvideo.jwplayer.events.CompleteEvent;
import com.longtailvideo.jwplayer.events.ControlBarVisibilityEvent;
import com.longtailvideo.jwplayer.events.ControlsEvent;
import com.longtailvideo.jwplayer.events.DisplayClickEvent;
import com.longtailvideo.jwplayer.events.ErrorEvent;
import com.longtailvideo.jwplayer.events.FullscreenEvent;
import com.longtailvideo.jwplayer.events.IdleEvent;
import com.longtailvideo.jwplayer.events.PauseEvent;
import com.longtailvideo.jwplayer.events.PlayEvent;
import com.longtailvideo.jwplayer.events.PlaylistCompleteEvent;
import com.longtailvideo.jwplayer.events.PlaylistEvent;
import com.longtailvideo.jwplayer.events.PlaylistItemEvent;
import com.longtailvideo.jwplayer.events.ReadyEvent;
import com.longtailvideo.jwplayer.events.SetupErrorEvent;
import com.longtailvideo.jwplayer.events.TimeEvent;
import com.longtailvideo.jwplayer.events.listeners.AdvertisingEvents;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.fullscreen.FullscreenHandler;

import java.util.concurrent.TimeUnit;

public class RNJWPlayerView extends JWPlayerView implements VideoPlayerEvents.OnFullscreenListener,
        VideoPlayerEvents.OnReadyListener,
        VideoPlayerEvents.OnPlayListener,
        VideoPlayerEvents.OnPauseListener,
        VideoPlayerEvents.OnCompleteListener,
        VideoPlayerEvents.OnIdleListener,
        VideoPlayerEvents.OnErrorListener,
        VideoPlayerEvents.OnSetupErrorListener,
        VideoPlayerEvents.OnBufferListener,
        VideoPlayerEvents.OnTimeListener,
        VideoPlayerEvents.OnPlaylistListener,
        VideoPlayerEvents.OnPlaylistItemListener,
        VideoPlayerEvents.OnPlaylistCompleteListener,
        VideoPlayerEvents.OnAudioTracksListener,
        VideoPlayerEvents.OnAudioTrackChangedListener,
        VideoPlayerEvents.OnControlsListener,
        VideoPlayerEvents.OnControlBarVisibilityListener,
        VideoPlayerEvents.OnDisplayClickListener,
        AdvertisingEvents.OnBeforePlayListener,
        AdvertisingEvents.OnBeforeCompleteListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "RNJWPlayerView";

    /**
     * The application window
     */
    Window mWindow;

    Activity mActivity;

    private Handler mHandler;

    public static AudioManager audioManager;

    private ThemedReactContext mThemedReactContext;

//    public static  String type="";

    private static boolean contextHasBug(Context context) {
        return context == null ||
                context.getResources() == null ||
                context.getResources().getConfiguration() == null;
    }


    private static Context getNonBuggyContext(ThemedReactContext reactContext,
                                              ReactApplicationContext appContext) {
        Context superContext = reactContext;
        if (!contextHasBug(appContext.getCurrentActivity())) {
            superContext = appContext.getCurrentActivity();
        } else if (contextHasBug(superContext)) {
            // we have the bug! let's try to find a better context to use
            if (!contextHasBug(reactContext.getCurrentActivity())) {
                superContext = reactContext.getCurrentActivity();
            } else if (!contextHasBug(reactContext.getApplicationContext())) {
                superContext = reactContext.getApplicationContext();
            } else {
                // ¯\_(ツ)_/¯
            }
        }
        return superContext;
    }

    public RNJWPlayerView(ThemedReactContext reactContext, ReactApplicationContext appContext, PlayerConfig config) {
        super(getNonBuggyContext(reactContext, appContext), config);
        mThemedReactContext = reactContext;
        init();
    }

    public ThemedReactContext getReactContext() {
        return mThemedReactContext;
    }

    public Activity getActivity() {
        return (Activity) getContext();
    }

    public void init() {
        mActivity = (Activity) getContext();
//        mActivity = getReactContext().getCurrentActivity();
        if (mActivity != null) {
            mWindow = mActivity.getWindow();
        }

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        addOnReadyListener(this);
        addOnPlayListener(this);
        addOnPauseListener(this);
        addOnCompleteListener(this);
        addOnIdleListener(this);
        addOnErrorListener(this);
        addOnSetupErrorListener(this);
        addOnBufferListener(this);
        addOnTimeListener(this);
        addOnPlaylistListener(this);
        addOnPlaylistItemListener(this);
        addOnPlaylistCompleteListener(this);
        addOnBeforePlayListener(this);
        addOnBeforeCompleteListener(this);
        addOnControlsListener(this);
        addOnControlBarVisibilityListener(this);
        addOnDisplayClickListener(this);
        addOnFullscreenListener(this);
        setFullscreenHandler(new FullscreenHandler() {

            @Override
            public void onFullscreenRequested() {
                WritableMap eventEnterFullscreen = Arguments.createMap();
                eventEnterFullscreen.putString("message", "onFullscreen");
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                        getId(),
                        "topFullScreen",
                        eventEnterFullscreen);
            }

            @Override
            public void onFullscreenExitRequested() {
                WritableMap eventExitFullscreen = Arguments.createMap();
                eventExitFullscreen.putString("message", "onFullscreenExit");
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                        getId(),
                        "topFullScreenExit",
                        eventExitFullscreen);
            }

            @Override
            public void onResume() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public void onAllowRotationChanged(boolean b) {
                Log.e(TAG, "onAllowRotationChanged: "+b );
            }

            @Override
            public void updateLayoutParams(ViewGroup.LayoutParams layoutParams) {

            }

            @Override
            public void setUseFullscreenLayoutFlags(boolean b) {

            }
        });

        setControls(true);
    }

    public void setCustomStyle(String name) {
        SkinConfig skinConfig = new SkinConfig.Builder()
                .name(name)
                .url(String.format("file:///android_asset/%s.css", name))
                .build();

        PlayerConfig config = getConfig();
        config.setSkinConfig(skinConfig);

        setup(config);
    }

    private void updateWakeLock(boolean enable) {
        if (mWindow != null) {
            if (enable) {
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();

        // The spinner relies on a measure + layout pass happening after it calls requestLayout().
        // Without this, the widget never actually changes the selection and doesn't call the
        // appropriate listeners. Since we override onLayout in our ViewGroups, a layout pass never
        // happens after a call to requestLayout, so we simulate one here.
        post(measureAndLayout);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.setFullscreen(true,true);
        } else if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT) {
            this.setFullscreen(false,false);
        }
    }

    @Override
    public void onDisplayClick(DisplayClickEvent displayClickEvent) {

    }

    @Override
    public void onAudioTracks(AudioTracksEvent audioTracksEvent) {

    }

    @Override
    public void onAudioTrackChanged(AudioTrackChangedEvent audioTrackChangedEvent) {

    }

    @Override
    public void onBeforePlay(BeforePlayEvent beforePlayEvent) {

    }


    @Override
    public void onBeforeComplete(BeforeCompleteEvent beforeCompleteEvent) {

    }

    @Override
    public void onIdle(IdleEvent idleEvent) {

    }

    @Override
    public void onPlaylist(PlaylistEvent playlistEvent) {

    }

    @Override
    public void onPlaylistItem(PlaylistItemEvent playlistItemEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlaylistItem");
        event.putInt("index",playlistItemEvent.getIndex());
        event.putString("playlistItem", playlistItemEvent.getPlaylistItem().toJson().toString());
        Log.i("playlistItem", playlistItemEvent.getPlaylistItem().toJson().toString());

//        try {
//            JSONObject jObj = new JSONObject(playlistItemEvent.getPlaylistItem().toJson().toString());
//            JSONArray array = jObj.getJSONArray("sources");
//            jObj = new JSONObject(String.valueOf(array.get(0)));
////            type = jObj.getString("type");
////            Log.e(TAG, "onPlaylistItem: TYPE : " + type);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topPlaylistItem",
                event);

    }

    @Override
    public void onPlaylistComplete(PlaylistCompleteEvent playlistCompleteEvent) {

    }

    @Override
    public void onBuffer(BufferEvent bufferEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onBuffer");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topBuffer",
                event);

        updateWakeLock(true);
    }

    @Override
    public void onPlay(PlayEvent playEvent) {
        int result = 0;
        if (audioManager != null) {
            result = audioManager.requestAudioFocus(this,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "onBeforePlay: " + result);
        }


        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlay");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topPlay",
                event);

        updateWakeLock(true);
    }

    @Override
    public void onReady(ReadyEvent readyEvent) {
        Log.e(TAG, "onReady triggered");
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPlayerReady");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topOnPlayerReady",
                event);

        updateWakeLock(true);
    }

    @Override
    public void onPause(PauseEvent pauseEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onPause");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topPause",
                event);

        updateWakeLock(false);
    }

    @Override
    public void onComplete(CompleteEvent completeEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onComplete");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topComplete",
                event);

        updateWakeLock(false);
    }

    @Override
    public void onFullscreen(FullscreenEvent fullscreenEvent) {

    }

    @Override
    public void onError(ErrorEvent errorEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onError");
        event.putString("error",errorEvent.getException().toString());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topPlayerError",
                event);

        updateWakeLock(false);
    }

    @Override
    public void onSetupError(SetupErrorEvent setupErrorEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onSetupError");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topSetupPlayerError",
                event);

        updateWakeLock(false);
    }

    @Override
    public void onTime(TimeEvent timeEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onTime");
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topTime",
                event);
    }

    @Override
    public void onControls(ControlsEvent controlsEvent) {

    }

    @Override
    public void onControlBarVisibilityChanged(ControlBarVisibilityEvent controlBarVisibilityEvent) {
        WritableMap event = Arguments.createMap();
        event.putString("message", "onControlBarVisible");
        event.putBoolean("controls", controlBarVisibilityEvent.isVisible());
        getReactContext().getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topControlBarVisible",
                event);

        updateWakeLock(true);
    }

    private Runnable mDelayedStopRunnable = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    @Override
    public void onAudioFocusChange(int i) {
        mHandler = new Handler();

        switch (i) {
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                mHandler.postDelayed(mDelayedStopRunnable, TimeUnit.SECONDS.toMillis(30));
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                Boolean autostart = getConfig().getAutostart();
                if (autostart) {
                    play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lower the volume, keep playing
                break;
        }
    }
}
