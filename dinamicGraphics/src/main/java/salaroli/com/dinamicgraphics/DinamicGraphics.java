package salaroli.com.dinamicgraphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import static java.lang.Math.abs;

public class DinamicGraphics extends View {
    private Paint paintEixos, paintGrade, paintTitles, paintNameTickers, paintCursor, paintCurve, paintBackgroundCurves;
    private Path pathEixos, pathGradeX, pathGradeY, pathTickerX, pathTickerY, pathCursor, pathMainCurve, pathBackgroundCurves;
    private Rect rect;
    private int larguraTotal, alturaTotal;
    private float widthLeft, widthRight, heightTop, heightBottom, paddingTicker;
    private boolean gradeStatus, boundaries, horizontalCursor;
    private String xTitle, yTitle;
    private String[] xNameTickers, yNameTickers;
    private float sizeNameTickers;
    private float ymax = 360, ymin = 90, xmin = 0, xmax = 180, cxmin = 0, cxmax = 180;
    private float A, B, C, D;
    private float cursorX, cursorY, cursorActualY, cursorXmin, cursorXmax;
    private double[] alpha, beta;
    private String cursorText;

    private InterfaceVerticalCursor interfaceVerticalCursor;

    public void setCircuitoListener(InterfaceVerticalCursor interfaceVerticalCursor) {
        this.interfaceVerticalCursor = interfaceVerticalCursor;
    }

    public interface InterfaceVerticalCursor {
        void recalculateMainCurve(double x, double y);
    }

    public DinamicGraphics(Context context) {
        super(context);
        init();
    }
    public DinamicGraphics(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        gradeStatus = true;
        boundaries = false;
        horizontalCursor = true;
        cursorX = 0; cursorY = 0;
        cursorText = "";
        xNameTickers = new String[]{};
        yNameTickers = new String[]{};
        xTitle = "";
        yTitle = "";

        paintEixos = new Paint();
        paintEixos.setStrokeWidth(5);
        paintEixos.setStyle(Paint.Style.STROKE);
        paintEixos.setColor(Color.BLACK);
        paintEixos.setAntiAlias(true);

        paintGrade = new Paint();
        paintGrade.setStrokeWidth(2);
        paintGrade.setStyle(Paint.Style.STROKE);
        paintGrade.setColor(Color.LTGRAY);
        paintGrade.setPathEffect(new DashPathEffect(new float[]{15, 30}, 0));
        paintGrade.setAntiAlias(true);

        paintCursor = new Paint();
        paintCursor.setStrokeWidth(5);
        paintCursor.setStyle(Paint.Style.FILL_AND_STROKE);
        paintCursor.setColor(Color.RED);
        paintCursor.setAntiAlias(true);

        paintCurve = new Paint();
        paintCurve.setStrokeWidth(5);
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setColor(Color.BLUE);
        paintCurve.setAntiAlias(true);

        paintBackgroundCurves = new Paint();
        paintBackgroundCurves.setStrokeWidth(4);
        paintBackgroundCurves.setStyle(Paint.Style.STROKE);
        paintBackgroundCurves.setColor(Color.GRAY);
        paintBackgroundCurves.setAntiAlias(true);

        paintTitles = new Paint();
        paintTitles.setAntiAlias(true);

        paintNameTickers = new Paint();
        paintNameTickers.setAntiAlias(true);

        pathEixos = new Path();
        pathGradeX = new Path();
        pathGradeY = new Path();
        pathTickerX = new Path();
        pathTickerY = new Path();
        pathMainCurve = new Path();
        pathBackgroundCurves = new Path();
        pathCursor = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paintEixos.setColor(Color.WHITE);
        canvas.drawRect(rect, paintEixos);
        if (gradeStatus) {
            canvas.drawPath(pathGradeX, paintGrade);
            canvas.drawPath(pathGradeY, paintGrade);
        }
        paintEixos.setColor(Color.BLACK);
        canvas.drawPath(pathEixos, paintEixos);
        canvas.drawPath(pathTickerX, paintEixos);
        canvas.drawPath(pathTickerY, paintEixos);

        drawAxisTitles(canvas);
        drawNameTickers(canvas);

        canvas.drawPath(pathMainCurve, paintCurve);
        canvas.drawPath(pathBackgroundCurves, paintBackgroundCurves);
        canvas.drawPath(pathCursor, paintCursor);
        if(!horizontalCursor) {
            paintCursor.setStrokeWidth(2);
            canvas.drawText(cursorText, cursorX + paintEixos.getStrokeWidth() * 10, cursorActualY + paintEixos.getStrokeWidth() * 2.25f, paintCursor);
            paintCursor.setStrokeWidth(5);
        }
    }

    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        if (oldw != 0 || oldh != 0) {
            unnormalize(pathMainCurve);
            unnormalize(pathBackgroundCurves);
        }
        // TODO: E SE oldw e oldh forem diferente de 0 ??????
        alturaTotal = height;
        larguraTotal = width;

        heightTop = 0.05f * alturaTotal;
        heightBottom = 0.94f * alturaTotal;
        widthLeft = 0.10f * larguraTotal;
        widthRight = 0.96f * larguraTotal;
        paddingTicker = 0.01f * larguraTotal;

        calculateBoundaries();
        pathEixos.reset();
        pathGradeX.reset();

        drawAxis();
        drawGridX(xNameTickers.length);
        drawGridY(yNameTickers.length);

        float sizeTitles = widthLeft * 0.45f;
        sizeNameTickers = widthLeft*0.4f;
        paintTitles.setTextSize(sizeTitles);
        paintNameTickers.setTextSize(sizeNameTickers);
        paintCursor.setTextAlign(Paint.Align.LEFT);
        paintCursor.setTextSize(paintEixos.getStrokeWidth() * 8f);
        cursorXmax = normalizeToPlot(cxmax, A, B);
        cursorXmin = normalizeToPlot(cxmin, A, B);
        cursorX = cursorXmin;
        normalize(pathMainCurve);
        normalize(pathBackgroundCurves);

        rect = new Rect(0, 0, width, height);
        super.onSizeChanged(width, height, oldw, oldh);
    }
    private void calculateBoundaries() {
        A = (widthRight - widthLeft)/(xmax - xmin);
        B = widthRight - A * xmax;
        C = (heightTop - heightBottom)/(ymax - ymin);
        D = heightTop - C * ymax;
        boundaries = true;
    }
    private void checkNormalize(Path path) {
        if (!boundaries)
            return;

        normalize(path);
        invalidate();
    }
    private void normalize(Path pathMainCurve) {
        Matrix matrix = new Matrix();
        matrix.preScale(A, C);
        pathMainCurve.transform(matrix);
        matrix.setTranslate(B, D);
        pathMainCurve.transform(matrix);
    }

    private void unnormalize(Path pathMainCurve) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(-B, -D);
        pathMainCurve.transform(matrix);
        matrix.preScale(1/A, 1/C);
        pathMainCurve.transform(matrix);
    }
    private float normalizeToScalar(float value, float coefAngular, float coefLinear) {
        if (coefAngular == 0)
            return 0;
        return (value - coefLinear)/coefAngular;
    }
    private float normalizeToPlot(float value, float coefAngular, float coefLinear) {
        return (coefAngular * value + coefLinear);
    }

    public void changeCursor(MotionEvent event) {
        if (horizontalCursor) {
            cursorX = event.getX();
            if (cursorX < cursorXmin) cursorX = cursorXmin;
            else if (cursorX > cursorXmax) cursorX = cursorXmax;
            pathCursor.reset();
            pathCursor.moveTo(cursorX, heightTop);
            pathCursor.lineTo(cursorX, heightBottom);

            float cursorAlpha = normalizeToScalar(cursorX, A, B);
            float cursorBeta = (float) beta[findIndexOfNearestValue(alpha, cursorAlpha)];
            cursorY = normalizeToPlot(cursorBeta, C, D);
            pathCursor.addCircle(cursorX, cursorY,paintEixos.getStrokeWidth() * 1.5f, Path.Direction.CW);
        } else {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_UP) ||  (action == MotionEvent.ACTION_CANCEL)) {
                interfaceVerticalCursor.recalculateMainCurve(getCursorX(), getCursorActualY());
                return;
            }
            cursorActualY = event.getY();
            if (cursorActualY < heightTop) cursorActualY = heightTop;
            else if (cursorActualY > heightBottom) cursorActualY = heightBottom;
            pathCursor.reset();
            pathCursor.moveTo(cursorX, heightTop);
            pathCursor.lineTo(cursorX, heightBottom);
            pathCursor.addCircle(cursorX, cursorActualY,paintEixos.getStrokeWidth() * 4.5f, Path.Direction.CW);
        }
        invalidate();
    }

    private int findIndexOfNearestValue(double[] array, float value) {
        int index = 0;
        float difference = (float) abs(value - array[0]);
        for (int i = 0; i < array.length; i++) {
            if (abs(value - array[i]) < difference) {
                difference = (float) abs(value - array[i]);
                index = i;
            }
        }
        return index;
    }

    private void drawAxis() {
        pathEixos.moveTo(widthLeft, heightTop);
        pathEixos.lineTo(widthLeft, heightBottom);
        pathEixos.lineTo(widthRight, heightBottom);
        pathEixos.lineTo(widthRight, heightTop);
        pathEixos.lineTo(widthLeft, heightTop);
    }
    private void drawGridX(int length) {
        if (length <= 0)
            return;
        length -= 1;
        pathGradeX.reset();
        for (int i = 1; i < length; i++) {
            float passo = (widthRight - widthLeft)/length;
            pathGradeX.moveTo(widthLeft + i * passo, heightTop);
            pathGradeX.lineTo(widthLeft + i * passo, heightBottom);

            pathTickerX.moveTo(widthLeft + i * passo, heightBottom + paddingTicker);
            pathTickerX.lineTo(widthLeft + i * passo, heightBottom - paddingTicker);
        }
    }
    private void drawGridY(int length) {
        if (length <= 2)
            return;
        length -= 1;
        pathGradeY.reset();
        for (int i = 1; i < (length); i++) {
            float passo = (heightBottom - heightTop)/length;
            pathGradeY.moveTo(widthLeft, heightTop + i * passo);
            pathGradeY.lineTo(widthRight, heightTop + i * passo);

            pathTickerY.moveTo(widthLeft + paddingTicker, heightTop + i * passo);
            pathTickerY.lineTo(widthLeft - paddingTicker, heightTop + i * passo);
        }
    }
    private void drawNameTickers(Canvas canvas) {
        paintNameTickers.setTextAlign(Paint.Align.CENTER);
        if (xNameTickers.length > 0) {
            for (int i = 0; i < xNameTickers.length; i++) {
                float passo = (widthRight - widthLeft)/(xNameTickers.length-1);
                canvas.drawText(xNameTickers[i], widthLeft + i * passo, 0.97f * alturaTotal, paintNameTickers);
            }
        }

        paintNameTickers.setTextAlign(Paint.Align.RIGHT);
        if(yNameTickers.length > 0) {
            for (int i = 0; i < yNameTickers.length; i++) {
                float passo = (heightBottom - heightTop)/(yNameTickers.length-1);
                final float heightConstant = heightBottom + (sizeNameTickers * 0.4f);
                canvas.drawText(yNameTickers[i], (widthLeft*0.8f), heightConstant - i * passo, paintNameTickers);
            }
        }
    }
    private void drawAxisTitles(Canvas canvas) {
        paintTitles.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(yTitle, 0, (heightTop*0.5f), paintTitles);
        paintTitles.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(xTitle, (larguraTotal), (alturaTotal*0.99f), paintTitles);
    }
    public boolean plotMainCurve(double[] alpha, double[] beta) {
        if (alpha.length != beta.length) {
            return false;
        }
        if (alpha.length <= 0) {
            return false;
        }

        this.alpha = alpha;
        this.beta = beta;

        pathMainCurve.reset();
        pathMainCurve.moveTo((float) alpha[0], (float) beta[0]);
        for (int i = 1; i < alpha.length; i++) {
            pathMainCurve.lineTo((float) alpha[i], (float) beta[i]);
        }
        checkNormalize(pathMainCurve);
        return true;
    }
    public boolean plotBackgroundCurves(double[] alpha, double[] beta) {
        if (alpha.length != beta.length) {
            return false;
        }
        if (alpha.length <= 0) {
            return false;
        }

        Path pathCurve = new Path();
        pathCurve.moveTo((float) alpha[0], (float) beta[0]);
        for (int i = 1; i < alpha.length; i++) {
            pathCurve.lineTo((float) alpha[i], (float) beta[i]);
        }
        checkNormalize(pathCurve);
        pathBackgroundCurves.addPath(pathCurve);
        return true;
    }
    public void clearBackgroundCurves() {
        pathBackgroundCurves.reset();
    }

    public void setGradeStatus(boolean status) {
        this.gradeStatus = status;
    }
    public void setXNameTickers(String[] nameTickers) {
        drawGridX(nameTickers.length);
        xNameTickers = nameTickers;
    }
    public void setYNameTickers(String[] nameTickers) {
        drawGridY(nameTickers.length);
        yNameTickers = nameTickers;
    }
    public void setAxisTitles(String xTitle, String yTitle) {
        this.xTitle = xTitle;
        this.yTitle = yTitle;
    }
    public void setAxisLimits(int ymax, int ymin, int xmax, int xmin) {
        this.ymax = ymax;
        this.ymin = ymin;
        this.xmax = xmax;
        this.cxmax = xmax;
        this.xmin = xmin;
        this.cxmin = xmin;
    }
    public void setCursorLimits(int xmin, int xmax) {
        this.cxmax = xmax;
        this.cxmin = xmin;
    }
    public void setMainCurveColor(int color) {
        paintCurve.setColor(color);
    }
    public void setBackgroundCurvesColor(int color) {
        paintCurve.setColor(color);
    }
    public void setMainCurveWidth(int width) {
        paintCurve.setStrokeWidth(width);
    }
    public void setBackgroundCurvesWidth(int width) {
        paintCurve.setStrokeWidth(width);
    }
    public void setCursorText(String cursorText) {
        this.cursorText = cursorText;
    }
    public boolean changeCursorMode() {
        this.horizontalCursor = !this.horizontalCursor;
        return this.horizontalCursor;
    }
    public boolean getCursorMode() {
        return this.horizontalCursor;
    }

    public float getCursorX() {
        return normalizeToScalar(cursorX, A, B);
    }
    public float getCursorY() {
        return normalizeToScalar(cursorY, C, D);
    }

    public float getCursorActualY() {
        if (horizontalCursor)
            return normalizeToScalar(cursorY, C, D);
        else
            return normalizeToScalar(cursorActualY, C, D);

    }
}
