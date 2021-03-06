package com.htc.my.mymusicplayer;

/**
 * Created by lidongzhou on 18-2-13.
 */

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.KeyEvent;

import com.htc.my.files.HtcSong;
import com.htc.my.files.PlayerConstants;
import com.htc.my.service.MyMusicPlayerService;


public class MediaSessionManager{
    private static final String TAG = "HTC-M MediaSession";
    private MediaSessionCompat mMediaSession = null;
    private MediaMetadata.Builder mbuilder = null;
    private Context mContext;
    private static MediaSessionManager instance = null;
    private AppCompatImageView play_pause_imageView = null;
    private ShadowImageView shadowImageView = null;
    private AppCompatImageView image_view_play_toggle = null;

    public MediaSessionManager(Context mContext) {
        this.mContext = mContext;
        setupMediaSession();
    }

    public static synchronized MediaSessionManager createInstance(Context ctx) {
       	if (instance == null) {
            	instance = new MediaSessionManager(ctx);
        }

        return instance;
    }

    public static MediaSessionManager getInstance() {
        return instance;
     }

    public  void MediaSessionInitMusicInfo(AppCompatImageView play_pause_imageView,ShadowImageView shadowImageView){
        this.play_pause_imageView = play_pause_imageView;
        this.shadowImageView = shadowImageView;
    }

    public  void MediaSessionInitMainActivity(AppCompatImageView image_view_play_toggle ){
        this.image_view_play_toggle = image_view_play_toggle;
    }


    /**
     * API 21 以上 耳机多媒体按钮监听 MediaSessionCompat.Callback
     */
    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {

//        接收到监听事件，可以有选择的进行重写相关方法

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            String action = mediaButtonEvent.getAction();
        //    Log.d(TAG, "onMediaButtonEvent");
            if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                KeyEvent event = (KeyEvent) mediaButtonEvent
                        .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                int keyAction = event.getAction();
                if (keyAction == KeyEvent.ACTION_DOWN) {
                    handleKeyEvent(event);
                } else if (keyAction == KeyEvent.ACTION_UP) {
                    handleKeyEvent(event);
                }
                return true;
            }

            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "onPlay");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause");
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Log.d(TAG, "onSkipToNext");
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.d(TAG, "onSkipToPrevious");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onStop");
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            Log.d(TAG, "onSeekTo");
        }
    };

    /**
     * 初始化并激活 MediaSession
     */
    private void setupMediaSession() {
//        第二个参数 tag: 这个是用于调试用的,随便填写即可
        mMediaSession = new MediaSessionCompat(mContext, TAG);
        //指明支持的按键信息类型
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );
        mMediaSession.setCallback(callback);
        mMediaSession.setActive(true);
    }
    /**
     * 释放MediaSession，退出播放器时调用
     */
    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

    /**
     * 更新正在播放的音乐信息，切换歌曲时调用
     */
    public void updateMetaData(HtcSong song) {

        Log.d(TAG, "updateMetaData");
        MediaMetadataCompat.Builder metaDta = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAuthor())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.getAlbum())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration());
        // .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getCoverBitmap(songInfo));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }
        mMediaSession.setMetadata(metaDta.build());

    }

    /**
     *
     */

    private void handleKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
       // Log.d(TAG,"Received ACTION_DOWN with keycode " + keyCode);
        Bundle msg = new Bundle();
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            Log.d(TAG,"KEYCODE_MEDIA_PLAY ");
            my_music_start_play();
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            Log.d(TAG,"KEYCODE_MEDIA_PAUSE ");
            my_music_pause();

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            Log.d(TAG,"KEYCODE_MEDIA_PLAY_PAUSE ");
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            Log.d(TAG,"KEYCODE_MEDIA_STOP ");

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            Log.d(TAG,"KEYCODE_MEDIA_NEXT ");

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            Log.d(TAG,"KEYCODE_MEDIA_PREVIOUS ");

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            Log.d(TAG,"KEYCODE_MEDIA_FAST_FORWARD ");
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
            Log.d(TAG,"KEYCODE_MEDIA_REWIND ");
        }

    }

    // 开始播放音乐
    private void my_music_start_play(){
        // 将音乐暂停功能交给后台
        Intent intent=new Intent(mContext, MyMusicPlayerService.class);
        intent.putExtra("MSG",PlayerConstants.MSG_CONTINUE);
        mContext.startService(intent);
        if (play_pause_imageView != null) play_pause_imageView.setImageResource(R.drawable.ic_pause);
        // 开始"大盘子"的运动
        if(shadowImageView!=null) shadowImageView.startRotateAnimation();
        if(image_view_play_toggle != null) image_view_play_toggle.setImageResource(R.drawable.ic_pause_1);
    }

    private  void my_music_pause(){
        //音乐暂停
        Intent intent=new Intent(mContext, MyMusicPlayerService.class);
        intent.putExtra("MSG", PlayerConstants.MSG_PAUSE);
        mContext.startService(intent);
        if (play_pause_imageView != null) play_pause_imageView.setImageResource(R.drawable.ic_play);
        // 暂停"大盘子"的活动
        if(shadowImageView!=null) shadowImageView.pauseRotateAnimation();
        if(image_view_play_toggle != null) image_view_play_toggle.setImageResource(R.drawable.ic_play_1);
    }

}
