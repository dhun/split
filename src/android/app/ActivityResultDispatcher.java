package android.app;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

/**
 * アクティビティ処理結果のディスパッチャ.<br/>
 * <br/>
 * Dialogなど、Activity以外から{@link Activity#startActivityForResult(Intent, int)}を呼び出して、 その処理結果である
 * {@link Activity#onActivityResult(int, int, Intent)}を呼出元にディスパッチします.<br/>
 * Activity#startActivityForResult(Intent, int)はprotectedスコープであるため、外部から呼び出すためにActivityと同じパッケージにしています.<br/>
 * 他にいいやりかたがあるのかしら...
 * 
 * @author j.hosoya
 */
public class ActivityResultDispatcher {
    private static final String TAG = ActivityResultDispatcher.class.getSimpleName();

    private final Activity parent;
    private final SparseArray<ActivityResultDispatcher.IActivityResultObserver> observers;

    /**
     * コンストラクタ
     * 
     * @param parent オブザーバの親要素となるActivity
     */
    public ActivityResultDispatcher(Activity parent) {
        this.parent = parent;
        this.observers = new SparseArray<ActivityResultDispatcher.IActivityResultObserver>();
    }

    /**
     * オブザーバを追加します.
     * 
     * @param requestCode Activity#startActivityForResultのパラメータとなるリクエストコード
     * @param observer オブザーバ
     */
    public void addObserver(int requestCode, IActivityResultObserver observer) {
        if (0 <= observers.indexOfKey(requestCode)) {
            String msg = "リクエストコード[" + requestCode + "]には[" + observer.getClass().getSimpleName() + "]が登録されています.";
            throw new IllegalStateException(msg);
        }
        observers.put(requestCode, observer);
    }

    public void removeObserver(int requestCode) {
        observers.remove(requestCode);
    }

    /**
     * Activity#startActivityForResultの呼び出しを要求します.
     * 
     * @param intent Activity#startActivityForResultのパラメータとなるインテント
     * @param requestCode Activity#startActivityForResultのパラメータとなるリクエストコード
     */
    public void requestStartAtivityForResult(Intent intent, int requestCode) {
        Log.d(TAG, "requestStartAtivityForResult. requestCode=[" + requestCode + "]");

        parent.startActivityForResult(intent, requestCode);
    }

    /**
     * Activity#onActivityResultをオブザーバにディスパッチします.
     * 
     * @param requestCode Activity#onActivityResultで受け取ったパラメータ
     * @param resultCode Activity#onActivityResultで受け取ったパラメータ
     * @param data Activity#onActivityResultで受け取ったパラメータ
     */
    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "dispatchActivityResult. requestCode=[" + requestCode + "], resultCode=[" + resultCode + "]");

        if (0 > observers.indexOfKey(requestCode)) {
            String msg = "リクエストコード[%d]に対するオブザーバは登録されていません.";
            throw new IllegalStateException(String.format(msg, requestCode));
        }
        observers.get(requestCode).onActivityResult(requestCode, resultCode, data);

    }

    public static interface IActivityResultObserver {
        void bindActivityResultDispatcher(ActivityResultDispatcher dispatcher);

        void unbindActivityResultDispatcher(ActivityResultDispatcher dispatcher);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
