
package net.gamesofton.rnjwplayer;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

public class RNJWPlayerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext mReactContext;

  private static final String TAG = "RNJWPlayerModule";

  public RNJWPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);

    mReactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNJWPlayerModule";
  }

  @ReactMethod
  public void play(final int reactTag) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.play();
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void toggleSpeed(final int reactTag) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            float rate = playerView.getPlaybackRate();
            if (rate < 2) {
              playerView.setPlaybackRate(rate += 0.5);
            } else {
              playerView.setPlaybackRate((float) 0.5);
            }
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void setSpeed(final int reactTag, final float speed) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.setPlaybackRate(speed);
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void pause(final int reactTag) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.pause();
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void stop(final int reactTag) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.stop();
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void loadPlaylistItem(final int reactTag, final ReadableMap playlistItem) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playlistItem != null && playerView != null) {
            if (playlistItem.hasKey("file")) {
              String newFile = playlistItem.getString("file");

              PlaylistItem newPlayListItem = new PlaylistItem();

              newPlayListItem.setFile(newFile);

              if (playlistItem.hasKey("playerStyle")) {
                playerView.setCustomStyle(playlistItem.getString("playerStyle"));
              }

              if (playlistItem.hasKey("title")) {
                newPlayListItem.setTitle(playlistItem.getString("title"));
              }

              if (playlistItem.hasKey("desc")) {
                newPlayListItem.setDescription(playlistItem.getString("desc"));
              }

              if (playlistItem.hasKey("image")) {
                newPlayListItem.setImage(playlistItem.getString("image"));
              }

              if (playlistItem.hasKey("mediaId")) {
                newPlayListItem.setMediaId(playlistItem.getString("mediaId"));
              }

              Boolean autostart = true;
              Boolean controls = true;

              if (playlistItem.hasKey("autostart")) {
                autostart = playlistItem.getBoolean("autostart");
              }

              if (playlistItem.hasKey("controls")) {
                controls = playlistItem.getBoolean("controls");
              }

              playerView.getConfig().setAutostart(autostart);
              playerView.getConfig().setControls(controls);
              playerView.setControls(controls);

              playerView.load(newPlayListItem);
            }
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void seekTo(final int reactTag, final double time) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.seek(time);
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void setPlaylistIndex(final int reactTag, final int index) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.setCurrentAudioTrack(index);
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void setControls(final int reactTag, final boolean show) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            playerView.setControls(show);
            playerView.getConfig().setControls(show);
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void loadPlaylist(final int reactTag, final ReadableArray playlist) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute(NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playlist != null && playlist.size() > 0 && playerView != null) {

            List<PlaylistItem> mPlayList = new ArrayList<>();
            ReadableMap playlistItem;
            String file = "";
            String image = "";
            String title = "";
            String desc = "";
            String mediaId = "";
            Boolean autostart = true;
            Boolean controls = true;

            int j = 0;
            while (playlist.size() > j) {
              playlistItem = playlist.getMap(j);

              if (playlistItem != null) {

                if (playlistItem.hasKey("file")) {
                  file = playlistItem.getString("file");
                }

                if (playlistItem.hasKey("title")) {
                  title = playlistItem.getString("title");
                }

                if (playlistItem.hasKey("desc")) {
                  desc = playlistItem.getString("desc");
                }

                if (playlistItem.hasKey("image")) {
                  image = playlistItem.getString("image");
                }

                if (playlistItem.hasKey("mediaId")) {
                  mediaId = playlistItem.getString("mediaId");
                }

                if (playlistItem.hasKey("autostart")) {
                  autostart = playlistItem.getBoolean("autostart");
                }

                if (playlistItem.hasKey("controls")) {
                  controls = playlistItem.getBoolean("controls");
                }

                PlaylistItem newPlayListItem = new PlaylistItem.Builder()
                        .file(file)
                        .title(title)
                        .description(desc)
                        .image(image)
                        .mediaId(mediaId)
                        .build();

                mPlayList.add(newPlayListItem);
              }

              j++;
            }

            if (playlist.getMap(0).hasKey("playerStyle")) {
              playerView.setCustomStyle(playlist.getMap(0).getString("playerStyle"));
            }

            playerView.getConfig().setAutostart(autostart);
            playerView.getConfig().setControls(controls);
            playerView.setControls(controls);
            playerView.load(mPlayList);
            playerView.play();
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      throw e;
    }
  }

  @ReactMethod
  public void position(final int reactTag, final Promise promise) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            promise.resolve((Double.valueOf(playerView.getPosition()).intValue()));
          } else {
            promise.reject("RNJW Error", "Player is null");
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      promise.reject("RNJW Error", e);
    }
  }

  @ReactMethod
  public void state(final int reactTag, final Promise promise) {
    try {
      UIManagerModule uiManager = mReactContext.getNativeModule(UIManagerModule.class);
      uiManager.addUIBlock(new UIBlock() {
        public void execute (NativeViewHierarchyManager nvhm) {
          RNJWPlayerView playerView = (RNJWPlayerView) nvhm.resolveView(reactTag);

          if (playerView != null) {
            PlayerState playerState = playerView.getState();
            promise.resolve(stateToInt(playerState));
          } else {
            promise.reject("RNJW Error", "Player is null");
          }
        }
      });
    } catch (IllegalViewOperationException e) {
      promise.reject("RNJW Error", e);
    }
  }

  private int stateToInt(PlayerState playerState) {
    switch (playerState) {
      case IDLE:
        return 0;
      case BUFFERING:
        return 1;
      case PLAYING:
        return 2;
      case PAUSED:
        return 3;
      case COMPLETE:
        return 4;
      default:
        return 0;
    }
  }
}