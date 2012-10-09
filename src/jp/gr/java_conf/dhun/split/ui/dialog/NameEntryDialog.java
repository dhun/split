package jp.gr.java_conf.dhun.split.ui.dialog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.GameConfig;
import jp.gr.java_conf.dhun.split.manager.info.PlayRecord;
import jp.gr.java_conf.dhun.split.ui.view.RecognizerEditView;
import android.app.ActivityResultDispatcher;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class NameEntryDialog extends Dialog {
    // private static final String TAG = NameEntryDialog.class.getSimpleName();

    private final Context context;
    private final GameConfig config;
    private final PlayRecord highScore;

    private ActivityResultDispatcher dispatcher;
    private RecognizerEditView txtUserName;
    private CheckBox chkDontShowThisDialog;
    private Button btnEntry;
    private Button btnCancel;

    public NameEntryDialog(Context context, GameConfig config, PlayRecord highScore) {
        super(context);
        this.context = context;
        this.config = config;
        this.highScore = highScore;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.dialog_title);
        setContentView(R.layout.dialog_name_entry);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        // ユーザ名
        // 前回入力された名前があればセット
        txtUserName = (RecognizerEditView) findViewById(R.id.txtUserName);
        txtUserName.bindActivityResultDispatcher(dispatcher);

        // ダイアログを表示しないぞチェック
        chkDontShowThisDialog = (CheckBox) findViewById(R.id.chkDontShowThisDialog);

        // エントリボタン
        btnEntry = (Button) findViewById(R.id.btnEntry);
        btnEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtUserName.getText().toString().trim();

                // 入力チェック
                if (0 == userName.length()) {
                    txtUserName.requestFocus();
                    Toast toast = Toast.makeText(context, context.getString(R.string.dialog_validation_err_username), Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                // 入力内容を設定クラスで保存
                config.setLastEntryName(userName);
                config.setShowNameEntryDialog(!chkDontShowThisDialog.isChecked());
                config.store(context);

                // スコアを保存
                highScore.setEntryName(userName);
                highScore.store(context);

                dismiss();
            }
        });

        // キャンセルボタン
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        // メッセージ
        String rankTouchedCount = getContext().getString(R.string.dialog_rank, highScore.getTouchedCountRank());
        String rankMaxBallCount = getContext().getString(R.string.dialog_rank, highScore.getMaxBallCountRank());
        String outOfRank = getContext().getString(R.string.dialog_out_of_rank);
        String msgTouchedRank = (highScore.getTouchedCountRank() > 0 ? rankTouchedCount : outOfRank);
        String msgMaxBallRank = (highScore.getMaxBallCountRank() > 0 ? rankMaxBallCount : outOfRank);
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setText(getContext().getString(R.string.dialog_message, msgTouchedRank, msgMaxBallRank));

        // 初期値のセット
        txtUserName.setText(config.getLastEntryName());
        chkDontShowThisDialog.setChecked(!config.isShowNameEntryDialog());

        // 画面を連打してるとダイアログが閉じちゃうので
        // 最初は非活性にして、１秒後に活性化させている.
        // UI操作なのでメインスレッドに処理させているけどこんな面倒が必要？？
        btnEntry.setEnabled(false);
        btnCancel.setEnabled(false);

        final Handler callbackHandler = new Handler();
        final Runnable callbackCommand = new Runnable() {
            @Override
            public void run() {
                btnEntry.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        };

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                callbackHandler.post(callbackCommand);
            }
        }, 1200, TimeUnit.MILLISECONDS);
    }

    public void setActivityResultDispatcher(ActivityResultDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
