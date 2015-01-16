
package com.RnD.xBeat;

import com.RnD.xBeat.sequencer.Sequencer.OnBPMListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.view.View;

public class ProgressBarView extends View implements OnBPMListener {
	
	public ProgressBarView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private static final int BAR_WIDTH = 10;

    private static final int PROGRESS_SIZE = 5;

    ShapeDrawable progressBar;

    ProgressBarView thisView = this;

    Handler progressHandler;

    // All the sizes are in pixels
    int totalWidth, barHeight, currentBarXPos, beatLength;

    private static int barColor = Color.WHITE;

    private static final int BAR_TRANSPARENCY = 200;

    long updateDelay, beatTime;

    Runnable progressRunnable = new Runnable() {

        @Override
        public void run() {
            thisView.invalidate();
            moveBar();
        }
    };

    /**
     * @param context
     * @param width sequencer board total width
     * @param height sequencer board total height
     * @param beatLength beat length in pixels
     */
    public ProgressBarView(Context context, int width, int height, int beatLength, int bpm) {
        super(context);
        progressHandler = new Handler();

        barHeight = height;
        totalWidth = width;
        currentBarXPos = 0;

        // How long it takes to go through a beat
        this.beatTime = (60 * 1000) / bpm;
        this.beatLength = beatLength;

        int nCallsToGoThroughABeat = beatLength / PROGRESS_SIZE;
        this.updateDelay = beatTime / nCallsToGoThroughABeat;

        progressBar = new ShapeDrawable(new RectShape());
        progressBar.getPaint().setColor(barColor);
        progressBar.setAlpha(BAR_TRANSPARENCY);
        moveBar();
    }

    private void moveBar() {
        synchronized (progressBar) {
            currentBarXPos = (currentBarXPos + PROGRESS_SIZE) % totalWidth;
            progressBar.setBounds(currentBarXPos - BAR_WIDTH, 0, currentBarXPos, barHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (progressBar) {
            progressBar.draw(canvas);
        }
    }

    @Override
    public void onBPM(int beatCount) {
        currentBarXPos = beatCount * beatLength;

        progressHandler.removeCallbacks(progressRunnable);
        for (int i = 0; i < beatTime; i += updateDelay) {
            progressHandler.postDelayed(progressRunnable, i);
        }
    }
}
