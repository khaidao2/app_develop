package com.example.k23411t_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Biểu đồ cột ngang tự vẽ bằng Canvas (không cần thư viện ngoài).
 * Mỗi mục gồm nhãn + giá trị; cột dài theo tỉ lệ với giá trị lớn nhất.
 */
public class BarChartView extends View {

    public static class Bar {
        final String label;
        final double value;
        final String valueText;
        final int color;

        public Bar(String label, double value, String valueText, int color) {
            this.label = label;
            this.value = value;
            this.valueText = valueText;
            this.color = color;
        }
    }

    private final List<Bar> bars = new ArrayList<>();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private final float rowHeight = dp(46);
    private final float barThickness = dp(18);
    private final float corner = dp(9);
    private final float labelTextSize = sp(13);
    private final float valueTextSize = sp(12);

    public BarChartView(Context c) {
        this(c, null);
    }

    public BarChartView(Context c, @Nullable AttributeSet a) {
        super(c, a);
        trackPaint.setColor(Color.parseColor("#EEEEF2"));
        labelPaint.setColor(Color.parseColor("#1F2933"));
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setFakeBoldText(true);
        valuePaint.setColor(Color.parseColor("#52606D"));
        valuePaint.setTextSize(valueTextSize);
        valuePaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setBars(List<Bar> data) {
        bars.clear();
        if (data != null) bars.addAll(data);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = resolveSize((int) dp(260), widthMeasureSpec);
        int h = (int) Math.max(rowHeight, bars.size() * rowHeight) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(w, resolveSize(h, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bars.isEmpty()) {
            valuePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Chưa có dữ liệu", getWidth() / 2f, getHeight() / 2f, valuePaint);
            valuePaint.setTextAlign(Paint.Align.RIGHT);
            return;
        }

        double max = 0;
        for (Bar b : bars) max = Math.max(max, b.value);
        if (max <= 0) max = 1;

        float left = getPaddingLeft();
        float right = getWidth() - getPaddingRight();
        float fullWidth = right - left;

        float y = getPaddingTop();
        for (Bar b : bars) {
            float labelBaseline = y + rowHeight / 2f - dp(6);
            float barTop = y + rowHeight / 2f + dp(2);
            float barBottom = barTop + barThickness;

            // Nhãn
            canvas.drawText(b.label, left, labelBaseline, labelPaint);
            // Giá trị (phải)
            canvas.drawText(b.valueText, right, labelBaseline, valuePaint);

            // Rãnh nền
            rect.set(left, barTop, right, barBottom);
            canvas.drawRoundRect(rect, corner, corner, trackPaint);

            // Cột giá trị
            float ratio = (float) (b.value / max);
            float barRight = left + Math.max(barThickness, fullWidth * ratio);
            barPaint.setColor(b.color);
            rect.set(left, barTop, barRight, barBottom);
            canvas.drawRoundRect(rect, corner, corner, barPaint);

            y += rowHeight;
        }
    }

    private float dp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }
}
