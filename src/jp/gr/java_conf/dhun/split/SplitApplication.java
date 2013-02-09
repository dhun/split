package jp.gr.java_conf.dhun.split;

import jp.gr.java_conf.dhun.split.manager.GameConfig.BallFaceType;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.BallImageDao;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class SplitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 設定の初期化
        // ⇒ 初期化SQLが存在すれば実行する. 最後に付け加えたので無理やり感が否めない
        if (0 == this.databaseList().length) {
            // for DB
            SQLiteDatabase db = new SplitDbOpenHelper(this).getReadableDatabase();

            // for preference
            BallImageDao ballImageDao = new BallImageDao(db);
            int defaultBallImageCount = ballImageDao.findAll().size();
            BallFaceType defaultBallFaceType = (0 == defaultBallImageCount) ? BallFaceType.RANDOM_COLOR : BallFaceType.IMAGE_CLIP;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = prefs.edit();
            editor.putInt(getString(R.string.pref_clipBallImage_key), defaultBallImageCount);
            editor.putString(getString(R.string.pref_ballFaceType_key), defaultBallFaceType.name());
            editor.commit();
        }
    }
}
