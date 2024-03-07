package com.example.unityapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CurvedImageView extends AppCompatImageView {

    private Path path;

    public CurvedImageView(Context context) {
        super(context);
        init();
    }

    public CurvedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurvedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        float curveRadius = 40f; // Adjust this value for the desired curvature

        path.reset();
        path.moveTo(0, height / 2);

        // Adjust control points for less curvature
        float controlX1 = curveRadius;
        float controlY1 = height;
        float controlX2 = width - curveRadius;
        float controlY2 = height;

        // Add curves to the path
        path.lineTo(0, height);
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, width, height);
        path.lineTo(width, height / 2);
        path.lineTo(width, 0);
        path.lineTo(0, 0);

        path.close();

        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
