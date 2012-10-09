package jp.gr.java_conf.dhun.split.character;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TextLabel {

    private Paint textPaint;
    private float x;
    private float y;
    private String text;
    private boolean visible = true;

    public TextLabel() {
    }

    public TextLabel(String text) {
        this.text = text;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setText(String value) {
        this.text = value;
    }

    public void setText(int value) {
        this.text = String.valueOf(value);
    }

    public void setText(float value) {
        this.text = String.valueOf(value);
    }

    public String getText() {
        return this.text;
    }

    public void draw(Canvas canvas) {
        if (visible) {
            canvas.drawText(text, x, y, textPaint);
        }
    }
}
