package jp.gr.java_conf.dhun.split.ui.preference;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.ui.view.RecognizerEditView;
import android.app.ActivityResultDispatcher;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecognizerEditTextPreference extends DialogPreference {
    private static final String TAG = RecognizerEditTextPreference.class.getSimpleName();

    // private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    // private static final String SPLIT_NS = "http://schemas.android.com/apk/res/jp.gr.java_conf.dhun.split";

    private ActivityResultDispatcher dispatcher;
    private RecognizerEditView txtRecogEditView;

    private String curValue;

    public RecognizerEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor");

        // レイアウトの読込
        setDialogLayoutResource(R.layout.preference_dialog_recognizer_edit_text);
    }

    @Override
    protected void showDialog(Bundle state) {
        Log.d(TAG, "showDialog");
        super.showDialog(state);
    }

    @Override
    protected View onCreateDialogView() {
        Log.d(TAG, "onCreateDialogView");
        return super.onCreateDialogView();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        Log.d(TAG, "onPrepareDialogBuilder");
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.d(TAG, "onDialogClosed");
        super.onDialogClosed(positiveResult);

        // キャンセルボタンが押されたら何もしない
        if (positiveResult == false) {
            return;
        }

        curValue = txtRecogEditView.getText().toString();

        // 許容できない値なら何もしない
        if (!shouldPersist() || !callChangeListener(curValue)) {
            return;
        }

        // 変更された内容を書き込み
        persistString(curValue);
    }

    /*
     * Preference が 呼び出されるときにデフォルト値が読み込まれる必要がある 異なる Preference 型は異なる 値型 は持つはずなので、サブクラスはそれにあわせた型を返す必要がある
     */
    @Override
    protected String onGetDefaultValue(TypedArray a, int index) {
        Log.d(TAG, "onGetDefaultValue. TypedArray=[" + a + "], index=[" + index + "]");
        return a.getString(index);
    }

    /*
     * Preference の初期値を設定する restorePersistedValue が true の場合、Preference 値を、SharedPreference からレストアすべき false の場合
     * Preference 値にデフォルト値をセット (SharedPreference の　shouldPersist() が true の場合、可能ならSharedPreferenceに値を格納)
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.d(TAG, "onSetInitialValue. restorePersistedValue=[" + restorePersistedValue + "], defaultValue=[" + defaultValue + "]");
        if (restorePersistedValue) {
            curValue = getPersistedString((String) defaultValue);
        } else {
            curValue = (String) defaultValue;
            persistString(curValue);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        Log.d(TAG, "onBindDialogView");
        super.onBindView(view);

        // 音声認識エディットビュー
        txtRecogEditView = (RecognizerEditView) view.findViewById(R.id.txtRecognizerEditView);
        txtRecogEditView.setText(String.valueOf(curValue));
        txtRecogEditView.bindActivityResultDispatcher(dispatcher);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        txtRecogEditView.unbindActivityResultDispatcher(dispatcher);
        super.onDismiss(dialog);
    }

    public void setActivityResultDispatcher(ActivityResultDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
