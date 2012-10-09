package jp.gr.java_conf.dhun.split.ui.preference;

import jp.gr.java_conf.dhun.split.ui.activity.ClipPictureActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BallImagePreference extends DialogPreference {
    private static final String TAG = BallImagePreference.class.getSimpleName();

    public BallImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "constructor");
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
    protected void showDialog(Bundle state) {
        Log.d(TAG, "showDialog");
        // super.showDialog(state); コレ消すとダイアログが出なくなるかな？⇒止まったー！！

        Intent intent = new Intent(getContext(), ClipPictureActivity.class);
        getContext().startActivity(intent);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.d(TAG, "onDialogClosed. positiveResult=[" + positiveResult + "]");
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected void onBindDialogView(View view) {
        Log.d(TAG, "onBindDialogView");
        super.onBindView(view);
    }
}
