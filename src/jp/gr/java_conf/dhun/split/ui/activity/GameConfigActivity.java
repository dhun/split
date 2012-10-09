package jp.gr.java_conf.dhun.split.ui.activity;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameConfig.BallFaceType;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;
import jp.gr.java_conf.dhun.split.ui.preference.RecognizerEditTextPreference;
import android.app.ActivityResultDispatcher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class GameConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = GameConfigActivity.class.getSimpleName();

    ActivityResultDispatcher dispatcher = new ActivityResultDispatcher(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // XMLレイアウトの読み込み
        addPreferencesFromResource(R.xml.pref_game_config);

        // ボールの表面
        Preference prefBallFaceType = findPreference(getString(R.string.pref_ballFaceType_key));
        prefBallFaceType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GameConfigActivity.this);
                int imageBallCount = prefs.getInt(getString(R.string.pref_clipBallImage_key), 0);

                if (BallFaceType.IMAGE_CLIP.name().equals(newValue) && 0 == imageBallCount) {
                    String msg = getString(R.string.pref_info_clipImage);
                    Toast.makeText(GameConfigActivity.this, msg, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        // お名前
        RecognizerEditTextPreference prefUserName = (RecognizerEditTextPreference) findPreference(getString(R.string.pref_userName_key));
        prefUserName.setActivityResultDispatcher(dispatcher);

        // ハイスコアのリセット
        Preference prefResetHighScore = findPreference(getString(R.string.pref_resetHighScore_key));
        prefResetHighScore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GameConfigActivity.this);
                builder.setMessage(R.string.pref_resetHighScore_summary);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = new SplitDbOpenHelper(GameConfigActivity.this).getWritableDatabase();
                        try {
                            db.beginTransaction();
                            GameRecordDao gameRecordDao = new GameRecordDao(db);
                            for (GameRecord gameRecord : gameRecordDao.findAll()) {
                                gameRecordDao.delete(gameRecord);
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                            db.close();
                        }
                        Toast.makeText(GameConfigActivity.this, R.string.pref_resetHighScore_deleted, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        refreshSummaryText();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged. key=[" + key + "]");

        refreshSummaryText();
    }

    private void refreshSummaryText() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Preference pref;

        // ボールの分裂数
        pref = findPreference(getString(R.string.pref_splitCount_key));
        pref.setSummary(getString(R.string.pref_splitCount_summary, prefs.getInt(pref.getKey(), 1)));

        // ボールの大きさ
        pref = findPreference(getString(R.string.pref_ballSize_key));
        pref.setSummary(getString(R.string.pref_ballSize_summary, prefs.getInt(pref.getKey(), 1)));

        // ボールの表面
        pref = findPreference(getString(R.string.pref_ballFaceType_key));
        String[] values = getResources().getStringArray(R.array.pref_ballFaceType_entryValues);
        String curLabel = "";
        String curValue = prefs.getString(pref.getKey(), "");
        for (int i = 0; i < values.length; i++) {
            if (curValue.equals(values[i])) {
                curLabel = getResources().getStringArray(R.array.pref_ballFaceType_entries)[i];
            }
        }
        pref.setSummary(getString(R.string.pref_ballFaceType_summary, curLabel));

        // クリップボールの画像
        pref = findPreference(getString(R.string.pref_clipBallImage_key));
        pref.setSummary(getString(R.string.pref_clipBallImage_summary, prefs.getInt(pref.getKey(), 0)));

        // BGM
        // 特になし

        // SE
        // 特になし

        // エントリダイアログの表示
        // 特になし

        // ユーザ名
        pref = findPreference(getString(R.string.pref_userName_key));
        pref.setSummary(getString(R.string.pref_userName_summary, prefs.getString(pref.getKey(), "")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dispatcher.dispatchActivityResult(requestCode, resultCode, data);
    }

}
