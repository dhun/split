package jp.gr.java_conf.dhun.split.character;

import java.util.HashMap;

import java.util.Map;

import jp.gr.java_conf.dhun.split.R;
import jp.gr.java_conf.dhun.split.manager.info.GameInformation;
import jp.gr.java_conf.dhun.split.manager.info.GameStates;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;

public class GameInformationArea {
    // private final GameConfig config;
    private final GameInformation info;

    private final Rect gameRect;
    private final Rect infoRect;
    private final Paint infoRectPaint;
    private final TextLabel playScoreLabel;
    private final TextLabel highScoreLabel;
    private final TextLabel curBallCountLabel;
    private final TextLabel maxBallCountLabel;
    private final TextLabel touchedCountLabel;

    private final TextLabel restSecondsLabel;
    private final TextLabel estimateFpsLabel;

    private final TextLabel playCurBallCount;
    private final TextLabel playMaxBallCount;
    private final TextLabel playTouchedCount;

    private final TextLabel highMaxBallCount;
    private final TextLabel highTouchedCount;

    private final TextLabel restSeconds;
    private final TextLabel estimateFps;

    private final TextLabel updateHighTouchedCount;
    private final TextLabel updateHighMaxBallCount;

    private final Map<GameStates, String> gameStateLabels1;
    private final Map<GameStates, String> gameStateLabels2;

    public GameInformationArea(Context context, GameInformation info, Rect gameRect, Rect infoRect) {
        // this.config = config;
        this.info = info;
        this.gameRect = gameRect;
        this.infoRect = infoRect;

        infoRectPaint = new Paint();
        infoRectPaint.setColor(Color.BLACK);

        playScoreLabel = new TextLabel(context.getString(R.string.game_lbl_store));
        highScoreLabel = new TextLabel(context.getString(R.string.game_lbl_high_score));
        curBallCountLabel = new TextLabel(context.getString(R.string.game_lbl_curball_count));
        maxBallCountLabel = new TextLabel(context.getString(R.string.game_lbl_maxball_count));
        touchedCountLabel = new TextLabel(context.getString(R.string.game_lbl_touched_count));

        restSecondsLabel = new TextLabel(context.getString(R.string.game_lbl_rest_seconds));
        estimateFpsLabel = new TextLabel(context.getString(R.string.game_lbl_estimate_fps));

        playCurBallCount = new TextLabel();
        playMaxBallCount = new TextLabel();
        playTouchedCount = new TextLabel();

        highMaxBallCount = new TextLabel();
        highTouchedCount = new TextLabel();

        restSeconds = new TextLabel();
        estimateFps = new TextLabel();

        updateHighTouchedCount = new TextLabel(context.getString(R.string.game_lbl_updated));
        updateHighMaxBallCount = new TextLabel(context.getString(R.string.game_lbl_updated));

        updateHighTouchedCount.setVisible(false);
        updateHighMaxBallCount.setVisible(false);

        // ゲームの状態１：ready, game overなど
        gameStateLabels1 = new HashMap<GameStates, String>();
        gameStateLabels1.put(GameStates.READY, context.getString(R.string.game_lbl_gamestate1_ready));
        gameStateLabels1.put(GameStates.PAUSING, context.getString(R.string.game_lbl_gamestate1_pausing));
        gameStateLabels1.put(GameStates.GAMEOVER_TIMEUP, context.getString(R.string.game_lbl_gamestate1_timeup));
        gameStateLabels1.put(GameStates.GAMEOVER_LEAVED, context.getString(R.string.game_lbl_gamestate1_leaved));
        gameStateLabels1.put(GameStates.READY_REPLAY_TIMEUP, context.getString(R.string.game_lbl_gamestate1_timeup));
        gameStateLabels1.put(GameStates.READY_REPLAY_LEAVED, context.getString(R.string.game_lbl_gamestate1_leaved));

        // ゲームの状態２：touch to startなど
        gameStateLabels2 = new HashMap<GameStates, String>();
        gameStateLabels2.put(GameStates.READY, context.getString(R.string.game_lbl_gamestate2_ready));
        gameStateLabels2.put(GameStates.PAUSING, context.getString(R.string.game_lbl_gamestate2_pausing));
        gameStateLabels2.put(GameStates.READY_REPLAY_TIMEUP, context.getString(R.string.game_lbl_gamestate2_timeup));
        gameStateLabels2.put(GameStates.READY_REPLAY_LEAVED, context.getString(R.string.game_lbl_gamestate2_leaved));

        setupLocation();
    }

    /**
     * ひたすら座標計算
     */
    private void setupLocation() {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Align.RIGHT);
        textPaint.setColor(Color.WHITE);

        // ４行格納できるフォントサイズを算出
        int textSize = 30;
        int textHeight;
        do {
            textPaint.setTextSize(textSize--);
            FontMetrics fontMetrics = textPaint.getFontMetrics();
            textHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        } while (infoRect.height() < textHeight * 4);

        // 文字列の幅を算出
        int padding2Width = (int) textPaint.measureText("  ");
        int padding4Width = (int) textPaint.measureText("    ");
        int labelWidth = (int) textPaint.measureText(curBallCountLabel.getText()) + padding2Width;
        int playScoreWidth = (int) textPaint.measureText(playScoreLabel.getText()) + padding2Width;
        int highScoreWidth = (int) textPaint.measureText(highScoreLabel.getText()) + padding2Width;
        int restSecondsLabelWidth = (int) textPaint.measureText(restSecondsLabel.getText()) + padding4Width;
        int restSecondsValueWidth = (int) textPaint.measureText("000") + padding2Width;

        int offsetY = textHeight;
        int currX = labelWidth;
        int currY = infoRect.top + textHeight;
        curBallCountLabel.setLocation(currX, (currY += offsetY));
        maxBallCountLabel.setLocation(currX, (currY += offsetY));
        touchedCountLabel.setLocation(currX, (currY += offsetY));

        offsetY = textHeight;
        currX = labelWidth + playScoreWidth;
        currY = infoRect.top;
        playScoreLabel.setLocation(currX, (currY += offsetY));
        playCurBallCount.setLocation(currX, (currY += offsetY));
        playMaxBallCount.setLocation(currX, (currY += offsetY));
        playTouchedCount.setLocation(currX, (currY += offsetY));

        offsetY = textHeight;
        currX = labelWidth + playScoreWidth + highScoreWidth;
        currY = infoRect.top;
        highScoreLabel.setLocation(currX, (currY += offsetY));
        currY += offsetY; // １行飛ばして
        highMaxBallCount.setLocation(currX, (currY += offsetY));
        highTouchedCount.setLocation(currX, (currY += offsetY));

        offsetY = textHeight;
        currX = labelWidth + playScoreWidth + highScoreWidth + padding2Width;
        currY = infoRect.top + textHeight + textHeight;
        updateHighMaxBallCount.setLocation(currX, (currY += offsetY));
        updateHighTouchedCount.setLocation(currX, (currY += offsetY));

        offsetY = textHeight;
        currX = labelWidth + playScoreWidth + highScoreWidth + restSecondsLabelWidth;
        currY = infoRect.top;
        restSecondsLabel.setLocation(currX, (currY += offsetY));
        estimateFpsLabel.setLocation(currX, (currY += offsetY));

        offsetY = textHeight;
        currX = labelWidth + playScoreWidth + highScoreWidth + restSecondsLabelWidth + restSecondsValueWidth;
        currY = infoRect.top;
        restSeconds.setLocation(currX, (currY += offsetY));
        estimateFps.setLocation(currX, (currY += offsetY));

        // 最後に計算で使ったペイントをセット
        playScoreLabel.setTextPaint(textPaint);
        highScoreLabel.setTextPaint(textPaint);
        curBallCountLabel.setTextPaint(textPaint);
        maxBallCountLabel.setTextPaint(textPaint);
        touchedCountLabel.setTextPaint(textPaint);
        playCurBallCount.setTextPaint(textPaint);
        playMaxBallCount.setTextPaint(textPaint);
        playTouchedCount.setTextPaint(textPaint);
        highMaxBallCount.setTextPaint(textPaint);
        highTouchedCount.setTextPaint(textPaint);

        restSecondsLabel.setTextPaint(textPaint);
        estimateFpsLabel.setTextPaint(textPaint);
        restSeconds.setTextPaint(textPaint);
        estimateFps.setTextPaint(textPaint);

        Paint updatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        updatePaint.setTextAlign(Align.LEFT);
        updatePaint.setColor(Color.MAGENTA);
        updatePaint.setTextSize(textPaint.getTextSize());

        updateHighMaxBallCount.setTextPaint(updatePaint);
        updateHighTouchedCount.setTextPaint(updatePaint);
    }

    public void draw(Canvas canvas) {
        // 変動する値を設定
        playCurBallCount.setText(info.getPlayRecord().getCurBallCount());
        playMaxBallCount.setText(info.getPlayRecord().getMaxBallCount());
        playTouchedCount.setText(info.getPlayRecord().getTouchedCount());

        highMaxBallCount.setText(info.getHighRecord().getMaxBallCount());
        highTouchedCount.setText(info.getHighRecord().getTouchedCount());

        if (info.getGameMode().isEndressMode()) {
            restSeconds.setText("-");
        } else {
            restSeconds.setText(info.getRestSeconds());
        }
        estimateFps.setText(info.getFormattedEstimateFps());

        updateHighTouchedCount.setVisible(info.isUpdateHighTouchedCount());
        updateHighMaxBallCount.setVisible(info.isUpdateHighMaxBallCount());

        // 描画
        canvas.drawRect(infoRect, infoRectPaint);

        playScoreLabel.draw(canvas);
        highScoreLabel.draw(canvas);
        touchedCountLabel.draw(canvas);
        curBallCountLabel.draw(canvas);
        maxBallCountLabel.draw(canvas);

        playCurBallCount.draw(canvas);
        playMaxBallCount.draw(canvas);
        playTouchedCount.draw(canvas);

        highMaxBallCount.draw(canvas);
        highTouchedCount.draw(canvas);

        restSecondsLabel.draw(canvas);
        estimateFpsLabel.draw(canvas);

        restSeconds.draw(canvas);
        estimateFps.draw(canvas);

        updateHighMaxBallCount.draw(canvas);
        updateHighTouchedCount.draw(canvas);

        // ゲームの状態を描画
        if (gameStateLabels1.containsKey(info.getGameStates())) {
            float textSize = 35;
            float textHeight = calcTextHeight(textSize);

            float baseX = gameRect.width() / 2;
            float baseY = gameRect.height() / 2;
            drawStateLabel(textSize, canvas, baseX, baseY - textHeight, gameStateLabels1.get(info.getGameStates()));
            drawStateLabel(textSize, canvas, baseX, baseY + textHeight, gameStateLabels2.get(info.getGameStates()));
        }
    }

    private float calcTextHeight(float textSize) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        FontMetrics fontMetrics = textPaint.getFontMetrics();
        return Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);
    }

    /**
     * @param canvas
     * @param baseX テキストのX座標(中心)
     * @param baseY テキストのY座標(中心)
     * @param label テキスト
     */
    private void drawStateLabel(float textSize, Canvas canvas, float baseX, float baseY, String label) {
        if (null == label) {
            return;
        }

        // 文字列用ペイントの生成
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(35);
        textPaint.setColor(Color.WHITE);
        FontMetrics fontMetrics = textPaint.getFontMetrics();

        // 文字列の幅を取得
        float textWidth = textPaint.measureText(label);

        // 文字列の幅からX座標を計算
        float textX = baseX - textWidth / 2;

        // 文字列の高さからY座標を計算
        float textY = baseY - (fontMetrics.ascent + fontMetrics.descent) / 2;

        // 吹き出し用ペイントの生成
        Paint balloonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        balloonPaint.setTextSize(35);
        balloonPaint.setColor(Color.LTGRAY);

        // 吹き出しの座標。文字列の5ポイント外側を囲む
        float balloonStartX = textX - 5;
        float balloonEndX = textX + textWidth + 5;
        float balloonStartY = textY + fontMetrics.ascent - 5;
        float balloonEndY = textY + fontMetrics.descent + 5;

        // 吹き出しの影用ペイントの生成
        Paint balloonShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        balloonShadowPaint.setTextSize(35);
        balloonShadowPaint.setColor(Color.GRAY);

        // 各座標を+2して吹き出しの影を描画
        RectF balloonShadowRectF = new RectF(balloonStartX + 2, balloonStartY + 2, balloonEndX + 2, balloonEndY + 2);
        canvas.drawRoundRect(balloonShadowRectF, 5, 5, balloonShadowPaint);

        // 吹き出しの描画
        RectF balloonRectF = new RectF(balloonStartX, balloonStartY, balloonEndX, balloonEndY);
        canvas.drawRoundRect(balloonRectF, 5, 5, balloonPaint);

        // 文字列の描画
        canvas.drawText(label, textX, textY, textPaint);
    }
}
