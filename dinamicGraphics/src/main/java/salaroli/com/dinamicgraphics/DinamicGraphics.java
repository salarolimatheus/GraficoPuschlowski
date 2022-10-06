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
   private Paint paintEixos, paintGrade;
   private Path pathEixos, pathGrade, pathTicker, pathCursor, pathCurvaSelecionada, pathCurvasSecundarias;
   private Rect rect;
   private float widthLeft;
   private float heightTop;
   private int larguraTotal;
   private int alturaTotal;
   private float widthRight;
   private float heightBottom;
   private float paddingTicker;

   public DinamicGraphics(Context context) {
      super(context);
      init();
   }
   public DinamicGraphics(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }
   private void init() {
      paintEixos = new Paint();
      paintEixos.setStrokeWidth(5);
      paintEixos.setStyle(Paint.Style.STROKE);
      paintEixos.setColor(Color.BLACK);
      paintEixos.setAntiAlias(true);

      paintGrade = new Paint();
      paintGrade.setStrokeWidth(5);
      paintGrade.setStyle(Paint.Style.STROKE);
      paintGrade.setColor(Color.GRAY);
      paintGrade.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
      paintGrade.setAntiAlias(true);


      pathEixos = new Path();
      pathGrade = new Path();
      pathTicker = new Path();

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
      canvas.drawPath(pathGrade, paintGrade);
      paintEixos.setColor(Color.BLACK);
      canvas.drawPath(pathEixos, paintEixos);
      canvas.drawPath(pathTicker, paintEixos);

      //DESENHO DAS CURVAS
   }

   protected void onSizeChanged(int width, int height, int oldw, int oldh) {
      alturaTotal = height;
      larguraTotal = width;

      heightTop = 0.02f * alturaTotal;
      heightBottom = 0.94f * alturaTotal;
      widthLeft = 0.10f * larguraTotal;
      widthRight = 0.98f * larguraTotal;
      paddingTicker = 0.01f * larguraTotal;

      pathEixos.reset();
      pathGrade.reset();

      desenhaEixos();
      desenhaGrade();

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
   private void desenhaGrade() {
      for (int i = 1; i < 26; i++) {
         float passo = (heightBottom - heightTop)/26;
         pathGrade.moveTo(widthLeft, heightTop + i * passo);
         pathGrade.lineTo(widthRight, heightTop + i * passo);

         pathTicker.moveTo(widthLeft + paddingTicker, heightTop + i * passo);
         pathTicker.lineTo(widthLeft - paddingTicker, heightTop + i * passo);
      }

      for (int i = 1; i < 18; i++) {
         float passo = (widthRight - widthLeft)/18;
         pathGrade.moveTo(widthLeft + i * passo, heightTop);
         pathGrade.lineTo(widthLeft + i * passo, heightBottom);

         pathTicker.moveTo(widthLeft + i * passo, heightBottom + paddingTicker);
         pathTicker.lineTo(widthLeft + i * passo, heightBottom - paddingTicker);
      }
   }
}
