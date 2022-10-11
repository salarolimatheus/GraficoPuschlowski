package salaroli.com.graficopuschlowski;

import androidx.appcompat.app.AppCompatActivity;
import salaroli.com.dinamicgraphics.DinamicGraphics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DinamicGraphics dinamicGraphics = findViewById(R.id.dinamic_graphics);
        TextView betaText = findViewById(R.id.beta);
        TextView alphaText = findViewById(R.id.alpha);

        String[] Ytickers = new String[] { "90", "120", "150", "180", "210", "240", "270", "300", "330", "360"};
        String[] Xtickers = new String[] {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180"};

        double[] valores_x = new double[180];
        double[] valores = new double[180];
        double[] valores1 = new double[180];
        double[] valores2 = new double[180];
        double[] valores3 = new double[180];

        for (int i = 0; i < 180; i++) {
            valores_x[i] = i;
            valores[i] = 360 - 1.5f * i;
            valores1[i] = 360 - 0.8333f * i;
            valores2[i] = 360 - 1.0f * i;
            valores3[i] = 360 - 0.001f * i*i;
        }

        dinamicGraphics.setGradeStatus(true);
        dinamicGraphics.setXNameTickers(Xtickers);
        dinamicGraphics.setYNameTickers(Ytickers);
        dinamicGraphics.setAxisTitles("α(°)", "β(°)");
        dinamicGraphics.plotMainCurve(valores_x, valores);
        dinamicGraphics.plotBackgroundCurves(valores_x, valores1);
        dinamicGraphics.plotBackgroundCurves(valores_x, valores3);
        dinamicGraphics.clearBackgroundCurves();

        dinamicGraphics.post(new Runnable() {
            @Override
            public void run() {
                dinamicGraphics.plotMainCurve(valores_x, valores1);
                dinamicGraphics.plotBackgroundCurves(valores_x, valores2);
                dinamicGraphics.plotBackgroundCurves(valores_x, valores3);
            }
        });

        dinamicGraphics.setOnTouchListener((view, motionEvent) -> {
            dinamicGraphics.changeCursor(motionEvent);
            alphaText.setText(String.valueOf(dinamicGraphics.getCursorX()));
            betaText.setText(String.valueOf(dinamicGraphics.getCursorY()));
            return true;
        });

        final boolean[] cursorMode = {false};
        alphaText.setOnClickListener(view -> {
            cursorMode[0] = !cursorMode[0];
            dinamicGraphics.setHorizontalCursor(cursorMode[0]);
        });
    }
}