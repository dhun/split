package jp.gr.java_conf.dhun.split.manager;

import java.util.ArrayList;

import java.util.List;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.BallImageDao;
import jp.gr.java_conf.dhun.split.persistence.entity.BallImage;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class GameConfig {

    private int splitCount; // タッチしたときの分裂数
    private int ballRadius; // ボールの大きさ
    private BallFaceType ballFaceType; // ボールの表面
    private List<BallImage> ballImages; // ボール画像のリスト

    private boolean soundBgm;
    private boolean soundSe;

    private boolean showNameEntryDialog; // ハイスコア時のネームエントリーダイアログを表示するか
    private String lastEntryName; // 最後にエントリ画面で入力された名前

    public enum BallFaceType {
        RANDOM_COLOR, IMAGE_CLIP;
    }

    public GameConfig() {
        splitCount = 2;
        ballRadius = 50;
        ballFaceType = BallFaceType.RANDOM_COLOR;
        ballImages = new ArrayList<BallImage>();

        soundBgm = true;
        soundSe = true;

        lastEntryName = "anonymous";
        showNameEntryDialog = true;
    }

    public void load(Context context) {
        // プリファレンスから設定をロード
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        splitCount = pref.getInt(context.getString(R.string.pref_splitCount_key), splitCount);
        ballRadius = pref.getInt(context.getString(R.string.pref_ballSize_key), ballRadius);
        ballFaceType = BallFaceType.valueOf(pref.getString(context.getString(R.string.pref_ballFaceType_key), ballFaceType.name()));
        soundBgm = pref.getBoolean(context.getString(R.string.pref_soundBgm_key), soundBgm);
        soundSe = pref.getBoolean(context.getString(R.string.pref_soundSe_key), soundSe);
        lastEntryName = pref.getString(context.getString(R.string.pref_userName_key), lastEntryName);
        showNameEntryDialog = pref.getBoolean(context.getString(R.string.pref_showEntryDialog_key), showNameEntryDialog);

        // DBから設定をロード
        SQLiteDatabase db = new SplitDbOpenHelper(context).getReadableDatabase();
        try {
            BallImageDao ballImageDao = new BallImageDao(db);
            ballImages = ballImageDao.findAll();
        } finally {
            db.close();
        }
    }

    public void store(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = pref.edit();
        editor.putString(context.getString(R.string.pref_userName_key), lastEntryName);
        editor.putBoolean(context.getString(R.string.pref_showEntryDialog_key), showNameEntryDialog);
        editor.commit();
    }

    public int getSplitCount() {
        return splitCount;
    }

    public int getBallRadius() {
        return this.ballRadius;
    }

    public BallFaceType getBallFaceType() {
        return ballFaceType;
    }

    public void setBallFaceType(BallFaceType ballFaceType) {
        this.ballFaceType = ballFaceType;
    }

    public List<BallImage> getBallImages() {
        return this.ballImages;
    }

    public boolean isSoundBgm() {
        return this.soundBgm;
    }

    public boolean isSoundSe() {
        return this.soundSe;
    }

    public String getLastEntryName() {
        return lastEntryName;
    }

    public boolean isShowNameEntryDialog() {
        return showNameEntryDialog;
    }

    public void setLastEntryName(String lastEntryName) {
        this.lastEntryName = lastEntryName;
    }

    public void setShowNameEntryDialog(boolean showNameEntryDialog) {
        this.showNameEntryDialog = showNameEntryDialog;
    }
}
