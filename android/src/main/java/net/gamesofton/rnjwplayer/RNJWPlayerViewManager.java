
package net.gamesofton.rnjwplayer;

import androidx.annotation.Nullable;
import android.view.View;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.configuration.SkinConfig;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longtailvideo.jwplayer.configuration.PlayerConfig.STRETCHING_UNIFORM;

public class RNJWPlayerViewManager extends SimpleViewManager<RNJWPlayerView> {

  public static final String REACT_CLASS = "RNJWPlayer";

  public static final int COMMAND_PLAY = 101;
  public static final int COMMAND_PAUSE = 102;
  public static final int COMMAND_STOP = 103;
  public static final int COMMAND_TOGGLE_SPEED = 104;

//  PlaylistItem mPlayListItem = null;
  List<PlaylistItem> mPlayList = null;

  //Props
  String file = "";
  String image = "";
  String title = "";
  String desc = "";
  String mediaId = "";
//  Number time;

  Boolean autostart = true;
  Boolean controls = true;
  Boolean repeat = false;
  Boolean displayTitle = false;
  Boolean displayDesc = false;
  Boolean nextUpDisplay = false;

  ReadableMap playlistItem; // PlaylistItem
  ReadableArray playlist; // List <PlaylistItem>

  private static final String TAG = "RNJWPlayerViewManager";

  private final ReactApplicationContext mAppContext;

  @Override
  public String getName() {
    // Tell React the name of the module
    // https://facebook.github.io/react-native/docs/native-components-android.html#1-create-the-viewmanager-subclass
    return REACT_CLASS;
  }

  public RNJWPlayerViewManager(ReactApplicationContext context) {
    mAppContext = context;
  }

  @Override
  public RNJWPlayerView createViewInstance(ThemedReactContext context) {
    PlayerConfig playerConfig = new PlayerConfig.Builder()
            .skinConfig(new SkinConfig.Builder().build())
            .repeat(false)
            .controls(true)
            .autostart(false)
            .displayTitle(true)
            .displayDescription(true)
            .nextUpDisplay(true)
            .stretching(STRETCHING_UNIFORM)
            .build();

    return new RNJWPlayerView(context, mAppContext, playerConfig);
  }

  @ReactProp(name = "file")
  public void setFile(View view, String prop) {
    if (file!=prop) {
      file = prop;
    }
  }

  @ReactProp(name = "mediaId")
  public void setMediaId(View view, String prop) {
    if (mediaId!=prop) {
      mediaId = prop;
    }
  }

  @ReactProp(name = "image")
  public void setImage(View view, String prop) {
    if(image!=prop) {
      image = prop;
    }
  }

  @ReactProp(name = "title")
  public void setTitle(View view, String prop) {
    if(title!=prop) {
      title = prop;
    }
  }

  @ReactProp(name = "desc")
  public void setDescription(View view, String prop) {
    if(desc!=prop) {
      desc = prop;
    }
  }

  @ReactProp(name = "displayTitle")
  public void setDisplayTitle(RNJWPlayerView view, Boolean prop) {
    if(displayTitle!=prop) {
      displayTitle = prop;

      view.getConfig().setDisplayTitle(displayTitle);
    }
  }

  @ReactProp(name = "displayDesc")
  public void setDisplayDescription(RNJWPlayerView view, Boolean prop) {
    if(displayDesc!=prop) {
      displayDesc = prop;

      view.getConfig().setDisplayDescription(displayDesc);
    }
  }

  @ReactProp(name = "autostart")
  public void setAutostart(RNJWPlayerView view, Boolean prop) {
    if(autostart!=prop) {
      autostart = prop;

      view.getConfig().setAutostart(autostart);
    }
  }

  @ReactProp(name = "controls")
  public void setControls(RNJWPlayerView view, Boolean prop) {
    if(controls!=prop) {
      controls = prop;

      view.getConfig().setControls(controls);
      view.setControls(controls);
    }
  }

  @ReactProp(name = "repeat")
  public void setRepeat(RNJWPlayerView view, Boolean prop) {
    if(repeat!=prop) {
      repeat = prop;

      view.getConfig().setRepeat(repeat);
    }
  }

  @ReactProp(name = "colors")
  public void setColors(RNJWPlayerView view, ReadableMap prop) {
    if (prop != null) {
      if (prop.hasKey("icons")) {
        view.getConfig().getSkinConfig().setControlBarIcons("#" + prop.getString("icons"));
      }

      if (prop.hasKey("timeslider")) {
        ReadableMap timeslider = prop.getMap("timeslider");

        if (timeslider.hasKey("progress")) {
          view.getConfig().getSkinConfig().setTimeSliderProgress("#" + timeslider.getString("progress"));
        }

        if (timeslider.hasKey("rail")) {
          view.getConfig().getSkinConfig().setTimeSliderRail("#" + timeslider.getString("rail"));
        }
      }

      view.setup(view.getConfig());
    }
  }

  @ReactProp(name = "playerStyle")
  public void setPlayerStyle(RNJWPlayerView view, String prop) {
    if (prop != null) {
      view.setCustomStyle(prop);
    }
  }

  @ReactProp(name = "nextUpDisplay")
  public void setNextUpDisplay(RNJWPlayerView view, Boolean prop) {
    if(nextUpDisplay!=prop) {
      nextUpDisplay = prop;

      view.getConfig().setNextUpDisplay(nextUpDisplay);
    }
  }

  @ReactProp(name = "playlistItem")
  public void setPlaylistItem(RNJWPlayerView view, ReadableMap prop) {
    if(playlistItem != prop) {
      playlistItem = prop;

      if (playlistItem != null) {
        if (playlistItem.hasKey("file")) {
          String newFile = playlistItem.getString("file");

          if (view.getPlaylistItem() == null) {

            PlaylistItem newPlayListItem = new PlaylistItem();

            newPlayListItem.setFile(newFile);

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

            if (playlistItem.hasKey("autostart")) {
              view.getConfig().setAutostart(playlistItem.getBoolean("autostart"));
            }

            if (playlistItem.hasKey("playerStyle")) {
              view.setCustomStyle(playlistItem.getString("playerStyle"));
            }

            view.load(newPlayListItem);
          } else {
            view.play();
          }
        }
      }
    }
  }

  @ReactProp(name = "playlist")
  public void setPlaylist(RNJWPlayerView view, ReadableArray prop) {
    if(playlist != prop) {
      playlist = prop;

      if (playlist != null && playlist.size() > 0) {
        mPlayList = new ArrayList<>();

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
              view.getConfig().setAutostart(playlistItem.getBoolean("autostart"));
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
          view.setCustomStyle(playlist.getMap(0).getString("playerStyle"));
        }

        view.load(mPlayList);
        view.play();
      }
    }
  }

  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder()
            .put(
                    "topPlayerError",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerError")))
            .put("topSetupPlayerError",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onSetupPlayerError")))
            .put("topTime",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onTime")))
            .put("topBuffer",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onBuffer")))
            .put("topFullScreen",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreen")))
            .put("topFullScreenExit",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onFullScreenExit")))
            .put("topPause",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPause")))
            .put("topPlay",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlay")))
            .put("topComplete",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onComplete")))
            .put("topPlaylistItem",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlaylistItem")))
            .put("topControlBarVisible",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onControlBarVisible")))
            .put("topOnPlayerReady",
                    MapBuilder.of(
                            "phasedRegistrationNames",
                            MapBuilder.of("bubbled", "onPlayerReady")))
            .build();
  }

  public static <K, V> Map<K, V> CreateMap(
          K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    Map map = new HashMap<K, V>();
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    return map;
  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
            "play", COMMAND_PLAY,
            "pause", COMMAND_PAUSE,
            "stop", COMMAND_STOP,
            "toggleSpeed", COMMAND_TOGGLE_SPEED
    );
  }

  @Override
  public void receiveCommand(RNJWPlayerView root, int commandId, @Nullable ReadableArray args) {
    super.receiveCommand(root, commandId, args);

    switch (commandId) {
      case COMMAND_PLAY:
        play(root);
        break;
      case COMMAND_PAUSE:
        pause(root);
        break;
      case COMMAND_STOP:
        stop(root);
        break;
      case COMMAND_TOGGLE_SPEED:
        toggleSpeed(root);
        break;
      default:
        //do nothing!!!!
    }
  }

  public void play(RNJWPlayerView root) {
    root.play();
  }

  public void pause(RNJWPlayerView root) {
    root.pause();
  }

  public void stop(RNJWPlayerView root) {
    root.stop();
  }

  public void toggleSpeed(RNJWPlayerView root) {
    float rate = root.getPlaybackRate();
    if (rate < 2) {
      root.setPlaybackRate(rate += 0.5);
    } else {
      root.setPlaybackRate((float) 0.5);
    }
  }
}