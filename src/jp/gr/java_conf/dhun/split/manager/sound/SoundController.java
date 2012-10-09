package jp.gr.java_conf.dhun.split.manager.sound;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

// http://musmus.main.jp/music.html
// フリー音楽素材MusMus
public class SoundController {
    private final GameConfig config;
    private final MediaPlayer bgmPlayer;
    private final SoundPool soundPool;
    private final int seBallToucheId;
    private final int seBallBoundsId;
    private final int seBallLeavedId;
    private final int seTimeupId;
    private final int seGameoverId;
    private final int seHighScoreId;

    public SoundController(Context context, GameConfig config) {
        this.config = config;

        bgmPlayer = MediaPlayer.create(context, R.raw.bgm_game);
        bgmPlayer.setLooping(true);

        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        seBallToucheId = soundPool.load(context, R.raw.se_ball_touche, 0);
        seBallBoundsId = soundPool.load(context, R.raw.se_ball_bounds, 0);
        seBallLeavedId = soundPool.load(context, R.raw.se_ball_leaved, 0);
        seTimeupId = soundPool.load(context, R.raw.se_timeup, 0);
        seGameoverId = soundPool.load(context, R.raw.se_gameover, 0);
        seHighScoreId = soundPool.load(context, R.raw.se_high_score, 0);
    }

    public void terminate() {
        bgmPlayer.release();
        soundPool.release();
    }

    public void startGameBgm() {
        if (config.isSoundBgm()) {
            bgmPlayer.seekTo(0);
            bgmPlayer.start();
        }
    }

    public void pauseGameBgm() {
        if (config.isSoundBgm()) {
            bgmPlayer.pause();
        }
    }

    public void unpauseGameBgm() {
        if (config.isSoundBgm()) {
            bgmPlayer.start();
        }
    }

    public void stopGameBgm() {
        if (config.isSoundBgm()) {
            bgmPlayer.stop();
        }
    }

    public void soundBallTouche() {
        soundSe(seBallToucheId);
    }

    public void soundBallBounds() {
        soundSe(seBallBoundsId);
    }

    public void soundBallLeaved() {
        soundSe(seBallLeavedId);
    }

    public void soundTimeup() {
        soundSe(seTimeupId);
    }

    public void soundGameover() {
        soundSe(seGameoverId);
    }

    public void soundHighScore() {
        soundSe(seHighScoreId);
    }

    private void soundSe(int resourceId) {
        if (config.isSoundSe()) {
            soundPool.play(resourceId, 1f, 1f, 0, 0, 0);
        }
    }
}
