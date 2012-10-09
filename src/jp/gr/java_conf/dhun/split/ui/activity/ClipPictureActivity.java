package jp.gr.java_conf.dhun.split.ui.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.persistence.SplitDbOpenHelper;
import jp.gr.java_conf.dhun.split.persistence.dao.BallImageDao;
import jp.gr.java_conf.dhun.split.persistence.entity.BallImage;
import jp.gr.java_conf.dhun.split.ui.view.ClipPictureView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ClipPictureActivity extends Activity {
    private static final String TAG = ClipPictureActivity.class.getSimpleName();
    private static final int REQUEST_CODE_GALLERY = 0;
    private static final int IMAGE_SIZE = 100;

    private final List<BallImage> activeBallImages = new ArrayList<BallImage>(); // 有効なボール
    private final List<BallImage> removeBallImages = new ArrayList<BallImage>(); // 削除されたボール

    private Toast toast;

    private ClipPictureView clipPictureView;
    private Gallery gallery;
    private BaseAdapter galleryAdapter;
    private int galleryItemBackgroundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onBindDialogView");
        super.onCreate(savedInstanceState);

        // TODO おまじない. 後で意味合いを調べる
        // See res/values/attrs.xml for the <declare-styleable> that defines Gallery1.
        TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
        galleryItemBackgroundId = typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
        typedArray.recycle();

        // 登録済みのボールを読込
        SQLiteDatabase db = new SplitDbOpenHelper(this).getWritableDatabase();
        BallImageDao ballImageDao = new BallImageDao(db);
        try {
            for (BallImage ballImage : ballImageDao.findAll()) {
                activeBallImages.add(ballImage);
            }
        } finally {
            db.close();
        }

        // XMLレイアウトの読み込み
        setContentView(R.layout.activity_clip_picture);

        // 画像クリップビュー
        clipPictureView = (ClipPictureView) findViewById(R.id.clipImageView);
        clipPictureView.setClipAreaSize(IMAGE_SIZE);

        // ギャラリー
        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                // クリックされた画像を選択
                gallery.setSelection(position);
                return true;
            }
        });

        // ギャラリーのアダプタ
        galleryAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return activeBallImages.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(ClipPictureActivity.this);
                imageView.setImageBitmap(activeBallImages.get(position).getBallBitmap());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new Gallery.LayoutParams(IMAGE_SIZE, IMAGE_SIZE));

                // TODO おまじない. 後で意味合いを調べる
                // The preferred Gallery item background
                imageView.setBackgroundResource(galleryItemBackgroundId);

                return imageView;
            }
        };
        gallery.setAdapter(galleryAdapter);

        // 追加ボタン
        ImageButton btnBallAppend = (ImageButton) findViewById(R.id.btn_ball_append);
        btnBallAppend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = clipPictureView.getClipBitmap();
                if (null == bitmap) {
                    showToast(R.string.pref_clipBallImage_err_no_image_on_append, Toast.LENGTH_SHORT);
                    ClipPictureActivity.this.openOptionsMenu();
                    return;
                }

                BallImage ballImage = new BallImage();
                ballImage.setBallBitmap(bitmap);
                activeBallImages.add(ballImage);
                galleryAdapter.notifyDataSetChanged();
                gallery.setSelection(gallery.getCount());

                showToast(R.string.pref_clipBallImage_appended, Toast.LENGTH_SHORT);
            }
        });

        // 削除ボタン
        ImageButton btnBallRemove = (ImageButton) findViewById(R.id.btn_ball_remove);
        btnBallRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = gallery.getSelectedItemPosition();
                if (position == Gallery.INVALID_POSITION) {
                    showToast(R.string.pref_clipBallImage_err_no_image_on_delete, Toast.LENGTH_SHORT);
                    return;
                }

                BallImage ballImage = activeBallImages.remove(gallery.getSelectedItemPosition());
                if (null != ballImage.getId()) {
                    removeBallImages.add(ballImage);
                }
                galleryAdapter.notifyDataSetChanged();
                showToast(R.string.pref_clipBallImage_deleted, Toast.LENGTH_SHORT);
            }
        });

        // OKボタン
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 確認ダイアログがうるさいので削除
                // AlertDialog.Builder builder = new AlertDialog.Builder(ClipPictureActivity.this);
                // builder.setMessage(R.string.pref_clipBallImage_confirm_on_ok);
                // builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // // 変更内容を書き込み
                // updateSettings();
                // finish();
                // }
                // });
                // builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();
                // }
                // });
                // builder.create().show();

                // 変更内容を書き込み
                updateSettings();
                finish();
            }
        });

        // キャンセルボタン
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 確認ダイアログがうるさいので削除
                // boolean isChange = false;
                // for (BallImage ballImage : activeBallImages) {
                // if (null == ballImage.getId()) {
                // isChange = true;
                // break;
                // }
                // }
                // if (!isChange) {
                // finish();
                // return;
                // }
                //
                // AlertDialog.Builder builder = new AlertDialog.Builder(ClipPictureActivity.this);
                // builder.setMessage(R.string.pref_clipBallImage_confirm_on_cancel);
                // builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // finish();
                // }
                // });
                // builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                // @Override
                // public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();
                // }
                // });
                // builder.create().show();

                finish();
            }
        });

        // ギャラリー呼び出し
        // callGallery();

        showToast(R.string.pref_clipBallImage_start_hint, Toast.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        menu.add(0, 0, 0, R.string.pref_clipBallImage_menu_change_image);
        menu.add(0, 1, 0, R.string.pref_clipBallImage_menu_reset_size);
        menu.add(0, 2, 0, R.string.pref_clipBallImage_menu_rotate_left);
        menu.add(0, 3, 0, R.string.pref_clipBallImage_menu_rotate_right);
        menu.add(0, 4, 0, R.string.pref_clipBallImage_menu_detecte_face);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean clipSelected = (null != clipPictureView.getClipBitmap());
        menu.getItem(1).setEnabled(clipSelected);
        menu.getItem(2).setEnabled(clipSelected);
        menu.getItem(3).setEnabled(clipSelected);
        menu.getItem(4).setEnabled(clipSelected);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.d(TAG, "onMenuItemSelected. featureId=[" + featureId + "]");

        // メニュークローズ
        // これがないと連打されたときに応答しちゃう
        closeOptionsMenu();

        switch (item.getItemId()) {
        case 0:
            // 画像変更(ギャラリー呼び出し)
            callGallery();
            break;

        case 1:
            // 画像サイズのリセット
            clipPictureView.resetImageSize();
            break;

        case 2:
            // 画像を左に回転
            clipPictureView.rotateImageLeft();
            break;

        case 3:
            // 画像を右に回転
            clipPictureView.rotateImageRight();
            break;

        case 4:
            // 顔認識
            clipPictureView.detecteFaces();
            break;

        default:
            return super.onMenuItemSelected(featureId, item);
        }

        return true;
    }

    private void callGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_GALLERY == requestCode && RESULT_OK == resultCode) {
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                Uri imageUri = data.getData();
                is = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                Log.e(TAG, "画像ファイルの読込に失敗しました.", e);
                Toast.makeText(this, "画像ファイルの読込に失敗しました." + e.getMessage(), Toast.LENGTH_LONG);
                finish();

            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.w(TAG, "画像ファイルのクローズに失敗しました. 無視して処理を継続します.", e);
                    }
                }
            }

            clipPictureView.setImage(bitmap);
            openOptionsMenu();
        }
    }

    private void updateSettings() {
        // 変更された内容をプリファレンスに書き込み
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = prefs.edit();
        editor.putInt(getString(R.string.pref_clipBallImage_key), activeBallImages.size());
        editor.commit();

        // 変更されたクリップ画像をDBに書き込み
        SQLiteDatabase db = new SplitDbOpenHelper(this).getWritableDatabase();
        BallImageDao ballImageDao = new BallImageDao(db);
        try {
            db.beginTransaction();
            for (BallImage ballImage : removeBallImages) {
                ballImageDao.delete(ballImage);
            }
            for (BallImage ballImage : activeBallImages) {
                if (null == ballImage.getId()) {
                    ballImageDao.insert(ballImage);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * トーストを表示します.<br/>
     * 前回のメッセージを今回のメッセージで上書きするよう、同一インスタンスを使いまわしています.
     * 
     * @param resId {@link Toast#setText(int)}
     * @param duration {@link Toast#setDuration(int)}
     */
    private void showToast(int resId, int duration) {
        if (null != toast) {
            toast.cancel();
            toast.setText(resId);
            toast.setDuration(duration);
        } else {
            toast = Toast.makeText(this, resId, duration);
        }
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 110);
        toast.show();
    }
}
