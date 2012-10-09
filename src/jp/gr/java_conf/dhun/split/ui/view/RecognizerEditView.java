package jp.gr.java_conf.dhun.split.ui.view;

import java.util.List;

import jp.gr.java_conf.dhun.split.R;
import android.app.Activity;
import android.app.ActivityResultDispatcher;
import android.app.ActivityResultDispatcher.IActivityResultObserver;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class RecognizerEditView extends FrameLayout implements IActivityResultObserver {
    private static final String TAG = RecognizerEditView.class.getSimpleName();
    private static final int RECOGNIZER_REQUEST_CODE = 1;

    private AutoCompleteTextView txtAutoComp;
    private ActivityResultDispatcher activityResultDispatcher;

    public RecognizerEditView(Context context) {
        super(context);
        initialize();
    }

    public RecognizerEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RecognizerEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        // XMLレイアウトのロード
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_recognizer_edit_view, this, true);

        // 自動補完テキスト
        txtAutoComp = (AutoCompleteTextView) findViewById(R.id.txtAutoComp);

        // 音声認識ボタン
        // ※ネットにつながっていないと「接続エラー」になる. Throwableでも補足できず例外は発生してないためどうしようもできなそう
        final ImageButton btnRecognizer = (ImageButton) findViewById(R.id.btnRecognizer);
        btnRecognizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getContext().getString(R.string.dialog_recognizer_msg));
                    activityResultDispatcher.requestStartAtivityForResult(intent, RECOGNIZER_REQUEST_CODE);

                } catch (ActivityNotFoundException e) {
                    btnRecognizer.setEnabled(false);
                    Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.dialog_recognizer_err), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public Editable getText() {
        return txtAutoComp.getText();
    }

    public void setText(CharSequence text) {
        txtAutoComp.setText(text);
    }

    @Override
    public void bindActivityResultDispatcher(ActivityResultDispatcher dispatcher) {
        dispatcher.addObserver(RECOGNIZER_REQUEST_CODE, this);
        activityResultDispatcher = dispatcher;
    }

    @Override
    public void unbindActivityResultDispatcher(ActivityResultDispatcher dispatcher) {
        dispatcher.removeObserver(RECOGNIZER_REQUEST_CODE);
        activityResultDispatcher = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RECOGNIZER_REQUEST_CODE != requestCode || Activity.RESULT_OK != resultCode) {
            return;
        }

        List<String> results = null;
        if (null == data) {
            Log.w(TAG, "音声認識アクティビティからのお返事インテントそのものがnullだった...");
        } else if (null == data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)) {
            Log.w(TAG, "音声認識アクティビティからのお返事インテントの中身がnullだった...");
        } else if (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).isEmpty()) {
            Log.w(TAG, "音声認識アクティビティからのお返事インテントの中身が空だった...");
        } else {
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }

        if (null == results) {
            txtAutoComp.setAdapter((ArrayAdapter<String>) null);
        } else {
            txtAutoComp.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.view_recognizer_edit_view_listitem, results));
            txtAutoComp.showDropDown();
        }
    }
}
