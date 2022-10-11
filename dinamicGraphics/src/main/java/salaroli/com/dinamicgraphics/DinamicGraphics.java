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
import android.view.View;

import androidx.annotation.Nullable;

public class DinamicGraphics extends View {
   private Paint paintEixos, paintGrade, paintTitles, paintNameTickers, paintCurve, paintBackgroundCurves;
   private Path pathEixos, pathGradeX, pathGradeY, pathTickerX, pathTickerY, pathCursor, pathMainCurve, pathBackgroundCurves;
   private Rect rect;
   private int larguraTotal, alturaTotal;
   private float widthLeft, widthRight, heightTop, heightBottom, paddingTicker;
   private boolean gradeStatus, boundaries;
   private String xTitle, yTitle;
   private String[] xNameTickers, yNameTickers;
   private float sizeTitles, sizeNameTickers;
   private float ymax = 360, ymin = 90, xmin = 0, xmax = 180;
   private float A, B, C, D;

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

      // AINDA NÃO UTILIZADAS
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

   protected void onSizeChanged(int width, int height, int oldw, int oldh) {
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

      sizeTitles = widthLeft*0.45f;
      sizeNameTickers = widthLeft*0.4f;
      paintTitles.setTextSize(sizeTitles);
      paintNameTickers.setTextSize(sizeNameTickers);

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
      this.xmin = xmin;
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

   public boolean plotMainCurve(double[] alpha, double[] beta) {
      if (alpha.length != beta.length) {
         return false;
      }
      if (alpha.length <= 0) {
         return false;
      }

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
}
