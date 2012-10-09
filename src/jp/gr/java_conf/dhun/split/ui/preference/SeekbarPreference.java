package jp.gr.java_conf.dhun.split.ui.preference;

import jp.gr.java_conf.dhun.split.R;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekbarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = SeekbarPreference.class.getSimpleName();

    // private static final String androidns = "http://schemas.android.com/apk/res/android";
    private static final String SPLIT_NS = "http://schemas.android.com/apk/res/jp.gr.java_conf.dhun.split";

    private TextView txtCurValue;
    private SeekBar seekBar;

    private int curValue;
    private final int minValue;
    private final int maxValue;
    private final int defValue;
    private final String unit;

    public SeekbarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor");

        // レイアウトの読込
        setDialogLayoutResource(R.layout.preference_dialog_seekbar);

        // XMLのアトリビュートセットから値を取得
        minValue = attrs.getAttributeIntValue(SPLIT_NS, "min", 0);
        maxValue = attrs.getAttributeIntValue(SPLIT_NS, "max", 100);
        defValue = attrs.getAttributeIntValue(SPLIT_NS, "default", 100);

        int resouceId = attrs.getAttributeResourceValue(SPLIT_NS, "unit", 0);
        if (0 != resouceId) {
            unit = context.getString(resouceId);
        } else {
            unit = attrs.getAttributeValue(SPLIT_NS, "unit");
        }
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

        // 許容できない値なら何もしない
        if (!shouldPersist() || !callChangeListener(Integer.valueOf(curValue))) {
            return;
        }

        // 変更された内容を書き込み
        persistInt(curValue);
    }

    /*
     * Preference が 呼び出されるときにデフォルト値が読み込まれる必要がある 異なる Preference 型は異なる 値型 は持つはずなので、サブクラスはそれにあわせた型を返す必要がある
     */
    @Override
    protected Integer onGetDefaultValue(TypedArray a, int index) {
        Log.d(TAG, "onGetDefaultValue. TypedArray=[" + a + "], index=[" + index + "]");
        return a.getInteger(index, defValue);
    }

    /*
     * Preference の初期値を設定する restorePersistedValue が true の場合、Preference 値を、SharedPreference からレストアすべき false の場合
     * Preference 値にデフォルト値をセット (SharedPreference の　shouldPersist() が true の場合、可能ならSharedPreferenceに値を格納)
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.d(TAG, "onSetInitialValue. restorePersistedValue=[" + restorePersistedValue + "], defaultValue=[" + defaultValue + "]");
        if (restorePersistedValue) {
            curValue = getPersistedInt(curValue);
        } else {
            curValue = (Integer) defaultValue;
            persistInt(curValue);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        Log.d(TAG, "onBindDialogView");
        super.onBindView(view);

        // 値の復元
        curValue = getPersistedInt(minValue);

        // 値
        txtCurValue = (TextView) view.findViewById(R.id.txt_cur_value);
        txtCurValue.setText(String.valueOf(curValue));

        // 単位
        TextView txtUnit = (TextView) view.findViewById(R.id.txt_unit);
        txtUnit.setText(unit);

        // シークバー
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(curValue - minValue);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekbar, int value, boolean fromTouch) {
        Log.d(TAG, "onProgressChanged");

        curValue = value + minValue;
        txtCurValue.setText(String.valueOf(curValue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekbar) {
        Log.d(TAG, "onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekbar) {
        Log.d(TAG, "onStopTrackingTouch");
    }
}
