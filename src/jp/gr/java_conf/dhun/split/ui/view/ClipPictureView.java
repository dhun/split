package jp.gr.java_conf.dhun.split.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.gr.java_conf.dhun.split.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class ClipPictureView extends FrameLayout {
    private static final String TAG = ClipPictureView.class.getSimpleName();
    private static final float DEFAULT_CLIP_AREA_SIZE = 50;

    private enum DRAG_MODE {
        NONE,
        /** クリップ領域の移動 */
        MOVE_CLIP_AREA,
        /** 背景画像の移動 */
        MOVE_BACKGROUND;
    }

    private ImageView mainPictureView;
    private ImageView ovalOverlayView;
    private ImageView faceOverlayView;
    private ImageButton btnPrevFace;
    private ImageButton btnNextFace;

    private float clipAreaSize = DEFAULT_CLIP_AREA_SIZE;

    private DRAG_MODE dragMode = DRAG_MODE.NONE;
    private final PointF lastPoint = new PointF();

    private final List<RectF> detecteFaceRects = new ArrayList<RectF>();
    private int detecteFaceRectIndex;

    public ClipPictureView(Context context) {
        super(context);
        initialize();
    }

    public ClipPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ClipPictureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        // XMLレイアウトのマージ
        // LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View view = inflater.inflate(view_clip_picture, this, true);
        View.inflate(getContext(), R.layout.view_clip_picture, this);

        // 背景画像用のイメージビュー
        mainPictureView = (ImageView) findViewById(R.id.mainPictureView);
        mainPictureView.setImageBitmap(null);

        // クリップ領域用のイメージビュー
        ovalOverlayView = (ImageView) findViewById(R.id.ovalOverlayImageView);

        // 顔認識用オーバレイのイメージビュー
        faceOverlayView = (ImageView) findViewById(R.id.faceOverlayImageView);

        // ズームコントローラ
        // 背景画像のサイズを１クリックにつき１割増減
        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = 1.1f;
                resizeImageView(mainPictureView, scale, true);
                resizeImageView(faceOverlayView, scale, true);
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = 0.9f;
                resizeImageView(mainPictureView, scale, true);
                resizeImageView(faceOverlayView, scale, true);
            }
        });

        btnPrevFace = (ImageButton) findViewById(R.id.btnPrevFace);
        btnPrevFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDetecteFace(-1);
            }
        });

        btnNextFace = (ImageButton) findViewById(R.id.btnNextFace);
        btnNextFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDetecteFace(+1);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout. changed=[" + changed + "]");
        Log.v(TAG, "" + getWidth());
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            prepareMainPicture();
            prepareOvalOverlayView();
            prepareFaceOverlayView();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure. widthMeasureSpec=[" + widthMeasureSpec + "], heightMeasureSpec=[" + heightMeasureSpec + "]");
        Log.v(TAG, "" + getWidth());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged. hasFocus=[" + hasFocus + "]");
        super.onWindowFocusChanged(hasFocus);

        // メニューやダイアログを表示するたびに呼び出されるため、ここじゃダメ
        // resetMainPicture();
        // prepareOvalOverlayView();
        // prepareFaceOverlayView();
    }

    private void prepareMainPicture() {
        // サイズ比を算出して、縦か横が目一杯になるほうを採用
        float scaleX = (float) getWidth() / mainPictureView.getDrawable().getBounds().width();
        float scaleY = (float) getHeight() / mainPictureView.getDrawable().getBounds().height();
        float scale = Math.min(scaleX, scaleY);

        // 背景画像のサイズを初期状態に戻す
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        matrix.postScale(scale, scale);
        mainPictureView.setImageMatrix(matrix);
    }

    private void prepareFaceOverlayView() {
        // 背景画像と同じ設定にする
        Bitmap srcBitmap = ((BitmapDrawable) mainPictureView.getDrawable()).getBitmap();
        Bitmap dstBitmap = null;
        if (null != srcBitmap) {
            dstBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        }
        faceOverlayView.setImageBitmap(dstBitmap);
        faceOverlayView.setImageMatrix(new Matrix(mainPictureView.getImageMatrix()));

        // 認識した顔の移動ボタンの表示設定
        if (detecteFaceRects.isEmpty()) {
            btnPrevFace.setVisibility(INVISIBLE);
            btnNextFace.setVisibility(INVISIBLE);
            return;
        } else {
            btnPrevFace.setVisibility(VISIBLE);
            btnNextFace.setVisibility(VISIBLE);
        }

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        Canvas canvas = new Canvas(dstBitmap);
        for (RectF rect : detecteFaceRects) {
            canvas.drawRect(rect, paint);
        }
    }

    private void prepareOvalOverlayView() {
        // クリップ領域に円を表示
        // ⇒ImageView.setImageDrawable(ShapeDrawable)だと何も表示されない. orz 処理方式変更
        //
        // ShapeDrawable clipAria = new ShapeDrawable(new OvalShape());
        // clipAria.setBounds(0, 0, 50, 50);
        // clipAria.getPaint().setColor(Color.RED);
        // clipAria.getPaint().setStyle(Style.STROKE);
        // clipAria.getPaint().setStrokeWidth(5); // 太さ＝5px
        // if (true)
        // ovalOverlayView.setImageDrawable(clipAria); // こっちだと何も表示されない
        // else
        // ovalOverlayView.setBackgroundDrawable(clipAria); // こっちだと表示できるけど位置を変更できない

        // クリップ領域に円を表示
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap((int) clipAreaSize, (int) clipAreaSize, Bitmap.Config.ARGB_8888);
        ovalOverlayView.setImageBitmap(bitmap);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(clipAreaSize / 2, clipAreaSize / 2, (clipAreaSize - 6) / 2, paint);

        // ビューの中央に配置
        float x = (getWidth() / 2) - (clipAreaSize / 2);
        float y = (getHeight() / 2) - (clipAreaSize / 2);
        Matrix matrix = new Matrix();
        matrix.postTranslate(x, y);
        ovalOverlayView.setImageMatrix(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String actionName;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            actionName = "ACTION_DOWN";
            break;
        case MotionEvent.ACTION_MOVE:
            actionName = "ACTION_MOVE";
            break;
        case MotionEvent.ACTION_UP:
            actionName = "ACTION_UP";
            break;
        case MotionEvent.ACTION_CANCEL:
            actionName = "ACTION_CANCEL";
            break;
        default:
            actionName = "unknown";
        }
        final String FORMAT = "onTouchEvent. action=[%5d], actionName=[%-15s], x=[%6.2f], y=[%6.2f]";
        Log.d(TAG, String.format(FORMAT, event.getAction(), actionName, event.getX(), event.getY()));

        // 現在のクリップ領域を取得
        RectF clipRect = new RectF();
        ovalOverlayView.getImageMatrix().mapRect(clipRect);
        clipRect.right += clipAreaSize;
        clipRect.bottom += clipAreaSize;

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (clipRect.contains((int) event.getX(), (int) event.getY())) {
                // クリップ領域内をタッチした場合
                // ⇒「クリップ領域の移動」をアクションにセット
                dragMode = DRAG_MODE.MOVE_CLIP_AREA;

            } else {
                // クリップ領域外をタッチした場合
                // ⇒「背景画像の移動」をアクションにセット
                dragMode = DRAG_MODE.MOVE_BACKGROUND;
            }
            lastPoint.set(event.getX(), event.getY());
            return true;

        case MotionEvent.ACTION_UP:
            // アクションにリセット
            dragMode = DRAG_MODE.NONE;
            return true;

        case MotionEvent.ACTION_MOVE:
            if (DRAG_MODE.MOVE_CLIP_AREA == dragMode) {
                // クリップ領域の移動
                float x = event.getX() - lastPoint.x;
                float y = event.getY() - lastPoint.y;
                moveImageView(ovalOverlayView, x, y);

            } else if (DRAG_MODE.MOVE_BACKGROUND == dragMode) {
                // 背景画像の表示位置を移動
                float x = event.getX() - lastPoint.x;
                float y = event.getY() - lastPoint.y;
                moveImageView(mainPictureView, x, y);
                moveImageView(faceOverlayView, x, y);
            }
            lastPoint.set(event.getX(), event.getY());
            return true;
        }

        return false;
    }

    private void resizeImageView(ImageView imageView, float scale, boolean pivotCenter) {
        float px = !pivotCenter ? 0 : getWidth() / 2;
        float py = !pivotCenter ? 0 : getHeight() / 2;
        Matrix matrix = new Matrix(imageView.getImageMatrix());
        matrix.postScale(scale, scale, px, py);
        imageView.setImageMatrix(matrix);
    }

    private void moveImageView(ImageView imageView, float x, float y) {
        Matrix matrix = new Matrix(imageView.getImageMatrix());
        matrix.postTranslate(x, y);
        imageView.setImageMatrix(matrix);
        // imageView.getImageMatrix().postTranslate(x, y); // これじゃダメだった
    }

    private void rotateImageView(ImageView imageView, float degrees) {
        // 顔認識機能は写真の向きが正しくないと正常に動作しない.
        // これを機能させるため、回転させた画像を作って設定している.
        // Matrix matrix = new Matrix(imageView.getImageMatrix());
        // matrix.postRotate(degrees, imageView.getWidth() / 2, imageView.getHeight() / 2);
        // imageView.setImageMatrix(matrix);

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap srcBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(dstBitmap);
    }

    public void setClipAreaSize(float size) {
        clipAreaSize = size;
        // resetOvalImage();
    }

    // 指定された画像を背景画像にセット
    public void setImage(Bitmap bitmap) {
        mainPictureView.setImageBitmap(bitmap);
        prepareMainPicture();

        detecteFaceRects.clear();
        prepareFaceOverlayView();
    }

    public void resetImageSize() {
        prepareMainPicture();

        detecteFaceRects.clear();
        prepareFaceOverlayView();
    }

    public void rotateImageLeft() {
        float degrees = -90f;
        rotateImageView(mainPictureView, degrees);
        rotateImageView(faceOverlayView, degrees);

        detecteFaceRects.clear();
        prepareFaceOverlayView();
    }

    public void rotateImageRight() {
        float degrees = +90f;
        rotateImageView(mainPictureView, degrees);
        rotateImageView(faceOverlayView, degrees);

        detecteFaceRects.clear();
        prepareFaceOverlayView();
    }

    public Bitmap getClipBitmap() {
        if (null == ((BitmapDrawable) this.mainPictureView.getDrawable()).getBitmap()) {
            return null;
        }

        RectF clipRect = new RectF();
        ovalOverlayView.getImageMatrix().mapRect(clipRect);

        // クリップ領域を矩形のまま取得
        int[] pixels = new int[(int) clipAreaSize * (int) clipAreaSize];

        // 描画中のビットマップを取得
        mainPictureView.setDrawingCacheEnabled(false);
        mainPictureView.setDrawingCacheEnabled(true);
        Bitmap srcBitmap = mainPictureView.getDrawingCache();

        srcBitmap.getPixels(pixels, //
                0, // offset
                (int) clipAreaSize, // stride
                (int) clipRect.left, // x
                (int) clipRect.top, // y
                (int) clipAreaSize, // width
                (int) clipAreaSize // height
                );

        // クリップ領域を円形にマスクして描画
        Path path = new Path();
        path.addCircle(clipAreaSize / 2, clipAreaSize / 2, clipAreaSize / 2, Direction.CCW);

        Bitmap dstBitmap = Bitmap.createBitmap((int) clipAreaSize, (int) clipAreaSize, srcBitmap.getConfig());
        Canvas dstCanvas = new Canvas(dstBitmap);
        dstCanvas.clipPath(path);
        dstCanvas.drawBitmap(pixels, //
                0, // offset
                (int) clipAreaSize, // stride
                0, // x
                0, // y
                (int) clipAreaSize, // width
                (int) clipAreaSize, // height
                true, // hasAlpha
                new Paint(Paint.ANTI_ALIAS_FLAG) // Paint
                );

        return dstBitmap;
    }

    private class FaceDetectorRunner extends Thread {
        private ProgressDialog dialog;
        private boolean canceled;

        private synchronized void cancel() {
            canceled = true;
            dialog.dismiss();
            List<FaceDetector.Face> detectedFaces = Collections.emptyList();
            detectedFaces(detectedFaces, false);
        }

        @Override
        public void run() {
            // 背景画像のビットマップを取得
            Bitmap bitmap32 = ((BitmapDrawable) mainPictureView.getDrawable()).getBitmap();

            // 読み込む際に16bitに減色 ※RBG_565, 透過ならARGB_444
            Bitmap bitmap16 = bitmap32.copy(Bitmap.Config.RGB_565, true);

            // 顔認識
            final FaceDetector.Face[] faces = new FaceDetector.Face[10];
            FaceDetector detector = new FaceDetector(bitmap16.getWidth(), bitmap16.getHeight(), faces.length);
            detector.findFaces(bitmap16, faces);
            final List<FaceDetector.Face> detectedFaces = new ArrayList<FaceDetector.Face>(faces.length);
            for (FaceDetector.Face face : faces) {
                if (null != face) {
                    detectedFaces.add(face);
                }
            }

            // 処理結果を通知
            synchronized (this) {
                if (!canceled) {
                    faceOverlayView.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            detectedFaces(detectedFaces, true);
                        }
                    });
                }
            }
        }
    }

    public void detecteFaces() {
        // 顔認識
        final FaceDetectorRunner faceDetectorRunner = new FaceDetectorRunner();

        // プログレスダイアログを表示
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getContext().getString(R.string.pref_clipBallImage_face_detecte_dialog));
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                faceDetectorRunner.cancel();
            }
        });

        // 認識処理開始＆ダイアログ表示
        faceDetectorRunner.dialog = dialog;
        faceDetectorRunner.start();
        dialog.show();
    }

    private void detectedFaces(List<FaceDetector.Face> detectedFaces, boolean ditected) {
        if (ditected && detectedFaces.isEmpty()) {
            Toast.makeText(getContext(), R.string.pref_clipBallImage_face_detecte_fail, Toast.LENGTH_LONG).show();
        }

        // Faceから矩形を算出
        detecteFaceRects.clear();
        for (Face face : detectedFaces) {
            if (null == face) {
                continue;
            }

            float eyedist = face.eyesDistance();
            PointF mid = new PointF();
            face.getMidPoint(mid);

            RectF rect = new RectF();
            rect.left = mid.x - eyedist;
            rect.top = mid.y - eyedist * 1.5f;
            rect.right = mid.x + eyedist;
            rect.bottom = mid.y + eyedist * 1.5f;
            detecteFaceRects.add(rect);

            String log = "信頼度=[%4.3f], 目の距離=[%3.1f], 顔の中央(x,y)=[%5.1f, %5.1f], 傾き(x,y,z)=[%2.1f,%2.1f,%2.1f]";
            Log.d(TAG, String.format(log, face.confidence(), face.eyesDistance(), mid.x, mid.y, face.pose(Face.EULER_X), face.pose(Face.EULER_Y), face.pose(Face.EULER_Z)));
        }

        prepareFaceOverlayView();

        detecteFaceRectIndex = 0;
        moveDetecteFace(0);
    }

    private void moveDetecteFace(int offset) {
        if (detecteFaceRects.isEmpty()) {
            return;
        }

        // 中央に表示する矩形を選択
        detecteFaceRectIndex = (detecteFaceRectIndex + offset) % detecteFaceRects.size();
        if (0 > detecteFaceRectIndex) {
            detecteFaceRectIndex = detecteFaceRects.size() - 1;
        }
        RectF faceRect = detecteFaceRects.get(detecteFaceRectIndex);

        // マトリクスを初期化
        mainPictureView.getImageMatrix().reset();
        faceOverlayView.getImageMatrix().reset();

        // クリップ領域に顔がおさまるように拡大／縮尺
        float scale = clipAreaSize / Math.max(faceRect.width(), faceRect.height());
        resizeImageView(mainPictureView, scale, false);
        resizeImageView(faceOverlayView, scale, false);

        // ビューの中央に移動
        float x = (getWidth() / 2) - faceRect.centerX() * scale;
        float y = (getHeight() / 2) - faceRect.centerY() * scale;
        moveImageView(mainPictureView, x, y);
        moveImageView(faceOverlayView, x, y);
    }
}
