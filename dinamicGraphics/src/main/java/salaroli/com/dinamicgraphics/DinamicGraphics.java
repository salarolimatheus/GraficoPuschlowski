package salaroli.com.dinamicgraphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DinamicGraphics extends View {
   private Paint paintEixos, paintGrade, paintTitles, paintNameTickers;
   private Path pathEixos, pathGradeX, pathGradeY, pathTickerX, pathTickerY, pathCursor, pathCurvaSelecionada, pathCurvasSecundarias;
   private Rect rect;
   private int larguraTotal, alturaTotal;
   private float widthLeft, widthRight, heightTop, heightBottom, paddingTicker;
   private boolean gradeStatus;
   private String xTitle, yTitle;
   private String[] xNameTickers, yNameTickers;
   private float sizeTitles, sizeNameTickers;

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
      paintEixos = new Paint();
      paintEixos.setStrokeWidth(5);
      paintEixos.setStyle(Paint.Style.STROKE);
      paintEixos.setColor(Color.BLACK);
      paintEixos.setAntiAlias(true);

      paintGrade = new Paint();
      paintGrade.setStrokeWidth(3);
      paintGrade.setStyle(Paint.Style.STROKE);
      paintGrade.setColor(Color.GRAY);
      paintGrade.setPathEffect(new DashPathEffect(new float[]{15, 30}, 0));
//      paintGrade.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
      paintGrade.setAntiAlias(true);

      paintTitles = new Paint();
      paintTitles.setAntiAlias(true);

      paintNameTickers = new Paint();
      paintNameTickers.setAntiAlias(true);

      pathEixos = new Path();
      pathGradeX = new Path();
      pathGradeY = new Path();
      pathTickerX = new Path();
      pathTickerY = new Path();

      // AINDA NÃO UTILIZADAS
      pathCursor = new Path();
      pathCurvaSelecionada = new Path();
      pathCurvasSecundarias = new Path();
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

      //DESENHO DAS CURVAS
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

      pathEixos.reset();
      pathGradeX.reset();

      desenhaEixos();
      desenhaGradeX(xNameTickers.length);
      desenhaGradeY(yNameTickers.length);

      sizeTitles = widthLeft*0.45f;
      sizeNameTickers = widthLeft*0.4f;
      paintTitles.setTextSize(sizeTitles);
      paintNameTickers.setTextSize(sizeNameTickers);

      rect = new Rect(0, 0, width, height);
      super.onSizeChanged(width, height, oldw, oldh);
   }

   private void desenhaEixos() {
      pathEixos.moveTo(widthLeft, heightTop);
      pathEixos.lineTo(widthLeft, heightBottom);
      pathEixos.lineTo(widthRight, heightBottom);
      pathEixos.lineTo(widthRight, heightTop);
      pathEixos.lineTo(widthLeft, heightTop);
   }
   private void desenhaGradeX(int length) {
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
   private void desenhaGradeY(int length) {
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
      desenhaGradeX(nameTickers.length);
      xNameTickers = nameTickers;
   }
   public void setYNameTickers(String[] nameTickers) {
      desenhaGradeY(nameTickers.length);
      yNameTickers = nameTickers;
   }
   public void setAxisTitles(String xTitle, String yTitle) {
      this.xTitle = xTitle;
      this.yTitle = yTitle;
   }
}
