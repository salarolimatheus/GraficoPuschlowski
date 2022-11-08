package salaroli.com.graficopuschlowski;

import androidx.appcompat.app.AppCompatActivity;
import salaroli.com.dinamicgraphics.DinamicGraphics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements DinamicGraphics.InterfaceVerticalCursor{
    private boolean CursorModoHorizontal;
    private DinamicGraphics dinamicGraphics;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dinamicGraphics = findViewById(R.id.dinamic_graphics);
        TextView betaText = findViewById(R.id.beta);
        TextView alphaText = findViewById(R.id.alpha);
        CursorModoHorizontal = dinamicGraphics.getCursorMode();
        String[] Ytickers = new String[] { "90", "120", "150", "180", "210", "240", "270", "300", "330", "360"};
        String[] Xtickers = new String[] {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180"};

//        //region teste 1
//        double[] valores_x = new double[180];
//        double[] valores = new double[180];
//        double[] valores1 = new double[180];
//        double[] valores2 = new double[180];
//        double[] valores3 = new double[180];
//
//        for (int i = 0; i < 180; i++) {
//            valores_x[i] = i;
//            valores[i] = 360 - 1.5f * i;
//            valores1[i] = 360 - 0.8333f * i;
//            valores2[i] = 360 - 1.0f * i;
//            valores3[i] = 360 - 0.001f * i*i;
//        }
//
//        dinamicGraphics.plotMainCurve(valores_x, valores);
//        dinamicGraphics.plotBackgroundCurves(valores_x, valores1);
//        dinamicGraphics.plotBackgroundCurves(valores_x, valores3);
//        dinamicGraphics.clearBackgroundCurves();
//        dinamicGraphics.setCursorLimits(40,140);
//        dinamicGraphics.setInterfaceListener(this);
//        dinamicGraphics.post(() -> {
//            dinamicGraphics.plotMainCurve(valores_x, valores1);
//            dinamicGraphics.plotBackgroundCurves(valores_x, valores2);
//            dinamicGraphics.plotBackgroundCurves(valores_x, valores3);
//        });
//
//        //endregion

        double[] alpha = new double[180];
        double[] beta = new double[180];
        for (int i = 0; i < beta.length; i++) {
            double phi = i * (Math.PI/360);
            alpha[i] = 20;
            beta[i] = 90+i;
        }
        dinamicGraphics.plotMainCurve(alpha, beta);

            dinamicGraphics.setInterfaceListener(this);
        dinamicGraphics.setGradeStatus(true);
        dinamicGraphics.setXNameTickers(Xtickers);
        dinamicGraphics.setYNameTickers(Ytickers);
        dinamicGraphics.setAxisTitles("α(°)", "β(°)");
        dinamicGraphics.setOnTouchListener((view, motionEvent) -> {
            dinamicGraphics.changeCursor(motionEvent);
            alphaText.setText(String.valueOf(dinamicGraphics.getCursorX()));
            betaText.setText(String.valueOf(dinamicGraphics.getCursorActualY()));
            dinamicGraphics.setCursorText(new DecimalFormat("0.00").format(dinamicGraphics.getCursorActualYNormalized()));
            return true;
        });

        alphaText.setOnClickListener(view -> CursorModoHorizontal = dinamicGraphics.changeCursorMode());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void recalculateMainCurve() {
        Toast.makeText(getApplicationContext(), "RECALCULADO", Toast.LENGTH_SHORT).show();
        dinamicGraphics.setCursorLimits(20, 20);
        dinamicGraphics.setCursorAt(20);
    }
}