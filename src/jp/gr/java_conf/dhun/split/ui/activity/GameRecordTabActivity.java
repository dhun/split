package jp.gr.java_conf.dhun.split.ui.activity;

import java.util.Collections;
import java.util.List;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameMode;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.GameRecordDao;
import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GameRecordTabActivity extends Activity {
    private static final String TAG = GameRecordTabActivity.class.getSimpleName();
    private static final int DISPLAY_COUNT = 10;
    private static final int SORT_TOUCHED_COUNT = 0;
    private static final int SORT_MAXBALL_COUNT = 1;

    private List<GameRecord> gameRecords;
    private GameMode gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // XMLレイアウトのロード
        setContentView(R.layout.activity_game_record_tab);

        // インテントから表示するゲームモードを取得
        gameMode = GameMode.valueOf(getIntent().getStringExtra(GameMode.TAG));

        // 成績リストのアダプタ
        final BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return gameRecords.size() + 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (null == convertView) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.activity_game_record_tab_listitem, null);
                }

                if (0 == position) {
                    // タイトル行
                    ((TextView) convertView.findViewById(R.id.lblEntryName)).setText(R.string.lbl_entry_name);
                    ((TextView) convertView.findViewById(R.id.lblTouchedCount)).setText(R.string.lbl_touched_count);
                    ((TextView) convertView.findViewById(R.id.lblMaxBallCount)).setText(R.string.lbl_maxball_count);
                } else {
                    GameRecord gameRecord = gameRecords.get(position - 1);
                    ((TextView) convertView.findViewById(R.id.lblEntryName)).setText(gameRecord.getEntryName());
                    ((TextView) convertView.findViewById(R.id.lblTouchedCount)).setText(String.valueOf(gameRecord.getTouchedCount()));
                    ((TextView) convertView.findViewById(R.id.lblMaxBallCount)).setText(String.valueOf(gameRecord.getMaxBallCount()));
                }

                return convertView;
            }
        };

        // ラジオ：ソート順位
        RadioGroup radSort = (RadioGroup) findViewById(R.id.rad_sort);
        radSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {
                // 成績リストビューの内容を更新
                for (int i = 0; i < radiogroup.getChildCount(); i++) {
                    if (((RadioButton) radiogroup.getChildAt(i)).isChecked()) {
                        refreshListViewContent(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        // 成績リストビューの内容を更新
        refreshListViewContent(SORT_TOUCHED_COUNT);

        // 成績リストのアダプタ
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    private void refreshListViewContent(int sortIndex) {
        SQLiteDatabase db = new SplitDbOpenHelper(this).getReadableDatabase();
        try {
            GameRecordDao gameRecordDao = new GameRecordDao(db);
            switch (sortIndex) {
            case SORT_TOUCHED_COUNT:
                gameRecords = gameRecordDao.listTopTouchedCount(gameMode, DISPLAY_COUNT);
                break;
            case SORT_MAXBALL_COUNT:
                gameRecords = gameRecordDao.listTopMaxBallCount(gameMode, DISPLAY_COUNT);
                break;
            default:
                gameRecords = Collections.emptyList();
            }
        } finally {
            db.close();
        }

    }
}
