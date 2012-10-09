package jp.gr.java_conf.dhun.split.ui.activity;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameMode;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class GameRecordActivity extends TabActivity {
    private static final String TAG = GameRecordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // XMLレイアウトのロード
        setContentView(R.layout.activity_game_record);

        TabHost tabHost = getTabHost();
        GameMode gameMode;
        Intent intent;

        // タブ：ノーマルモード
        gameMode = GameMode.NORMAL_MODE;
        intent = new Intent(this, GameRecordTabActivity.class);
        intent.putExtra(GameMode.TAG, gameMode.name());

        TabHost.TabSpec tabSpec = tabHost.newTabSpec(gameMode.name());
        tabSpec.setIndicator(getString(R.string.lbl_normal_mode), getResources().getDrawable(R.drawable.ic_tab_normal));
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        // タブ：エンドレスモード
        gameMode = GameMode.ENDRESS_MODE;
        intent = new Intent(this, GameRecordTabActivity.class);
        intent.putExtra(GameMode.TAG, gameMode.name());

        tabSpec = tabHost.newTabSpec(gameMode.name());
        tabSpec.setIndicator(getString(R.string.lbl_endress_mode), getResources().getDrawable(R.drawable.ic_tab_endress));
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        // 初期表示するタブを指定
        tabHost.setCurrentTab(0);
    }
}
