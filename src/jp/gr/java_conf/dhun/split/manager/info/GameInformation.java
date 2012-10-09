package jp.gr.java_conf.dhun.split.manager.info;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Collections;
import java.util.List;

import jp.gr.java_conf.dhun.split.manager.GameConfig;
import jp.gr.java_conf.dhun.split.manager.GameManager;
import jp.gr.java_conf.dhun.split.manager.GameMode;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameInformation {
    private static final Format FPS_FORMAT = new DecimalFormat("00.0");

    private final GameMode gameMode; // ゲームモード
    private final PlayRecord playRecord; // プレイ中のスコア
    private final GameRecord highRecord; // ハイスコア
    private List<GameRecord> touchedCountTop10;
    private List<GameRecord> maxBallCountTop10;

    private GameStates gameStates; // ゲームの状態
    private long restMills; // 残り時間
    private float estimateFps; // FPS(推定)

    private boolean updateHighTouchedCount;
    private boolean updateHighMaxBallCount;

    public GameInformation(GameConfig config, GameMode gameMode) {
        this.gameMode = gameMode;
        this.playRecord = new PlayRecord();
        this.highRecord = new GameRecord();
    }

    public void reset() {
        playRecord.reset();
        highRecord.reset();
        touchedCountTop10 = Collections.emptyList();
        maxBallCountTop10 = Collections.emptyList();

        playRecord.setGameMode(gameMode);

        gameStates = null;
        restMills = 0;
        estimateFps = 0;
        updateHighTouchedCount = false;
        updateHighMaxBallCount = false;
    }

    public void loadHighRecord(Context context) {
        SQLiteDatabase db = new SplitDbOpenHelper(context).getReadableDatabase();
        try {
            GameRecordDao gameRecordDao = new GameRecordDao(db);
            GameRecord topRecord = gameRecordDao.findTop(gameMode);

            highRecord.setTouchedCount(topRecord.getTouchedCount());
            highRecord.setMaxBallCount(topRecord.getMaxBallCount());
            if (0 == highRecord.getMaxBallCount()) {
                highRecord.setMaxBallCount(1);
            }

            touchedCountTop10 = gameRecordDao.listTopTouchedCount(gameMode, 10);
            maxBallCountTop10 = gameRecordDao.listTopMaxBallCount(gameMode, 10);
        } finally {
            db.close();
        }
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameStates(GameStates gameStates) {
        Log.d("setGameStates", gameStates.toString());
        this.gameStates = gameStates;
    }

    public GameStates getGameStates() {
        return gameStates;
    }

    public PlayRecord getPlayRecord() {
        return this.playRecord;
    }

    public GameRecord getHighRecord() {
        return this.highRecord;
    }

    public void addScore(int score) {
        playRecord.addTouchedCount();
        if (highRecord.getTouchedCount() < playRecord.getTouchedCount()) {
            highRecord.setTouchedCount(playRecord.getTouchedCount());
            updateHighTouchedCount = true;
        }
    }

    public void setBallCount(int ballCount) {
        playRecord.setCurBallCount(ballCount);

        if (playRecord.getMaxBallCount() < ballCount) {
            playRecord.setMaxBallCount(ballCount);
            if (highRecord.getMaxBallCount() < ballCount) {
                highRecord.setMaxBallCount(ballCount);
                updateHighMaxBallCount = true;
            }
        }
    }

    public int getRestSeconds() {
        return (int) Math.ceil(restMills / 1000);
    }

    public long getRestMills() {
        return this.restMills;
    }

    public void setRestMills(long restMills) {
        this.restMills = restMills;
    }

    public void setElapseMills(long elapseMills) {
        if (0 == elapseMills) {
            estimateFps = 0;
        } else {
            estimateFps = 1000f / (elapseMills);
            if (GameManager.FPS < estimateFps) {
                estimateFps = GameManager.FPS;
            }
        }
    }

    public String getFormattedEstimateFps() {
        return FPS_FORMAT.format(estimateFps);
    }

    public float getEstimateFps() {
        return estimateFps;
    }

    public boolean isUpdateHighTouchedCount() {
        return updateHighTouchedCount;
    }

    public boolean isUpdateHighMaxBallCount() {
        return updateHighMaxBallCount;
    }

    public boolean isUpdateTop10() {
        boolean result = false;
        if (playRecord.getTouchedCount() > 0 && 10 > touchedCountTop10.size()) {
            playRecord.setTouchedCountRank(touchedCountTop10.size() + 1);
            playRecord.setMaxBallCountRank(touchedCountTop10.size() + 1);
            result = true;
        }
        for (int i = 0; i < touchedCountTop10.size(); i++) {
            if (playRecord.getTouchedCount() > touchedCountTop10.get(i).getTouchedCount()) {
                playRecord.setTouchedCountRank(i + 1);
                result = true;
                break;
            }
        }
        for (int i = 0; i < maxBallCountTop10.size(); i++) {
            if (playRecord.getMaxBallCount() > maxBallCountTop10.get(i).getMaxBallCount()) {
                playRecord.setMaxBallCountRank(i + 1);
                result = true;
                break;
            }
        }
        return result;
    }
}
