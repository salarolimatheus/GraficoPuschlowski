package salaroli.com.graficopuschlowski;

import androidx.appcompat.app.AppCompatActivity;
import salaroli.com.dinamicgraphics.DinamicGraphics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements DinamicGraphics.InterfaceVerticalCursor{
    private boolean CursorModoHorizontal;
    private DinamicGraphics dinamicGraphics;
    private TextView alphaText, betaText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dinamicGraphics = findViewById(R.id.dinamic_graphics);
        alphaText = findViewById(R.id.alpha);
        betaText = findViewById(R.id.beta);
        CursorModoHorizontal = dinamicGraphics.getCursorMode();
        String[] Ytickers = new String[] { "90", "120", "150", "180", "210", "240", "270", "300", "330", "360"};
        String[] Xtickers = new String[] {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180"};

        double[] alpha = new double[180];
        double[] beta = new double[180];
        for (int i = 0; i < beta.length; i++) {
            double phi = i * (Math.PI/360);
            alpha[i] = i;
            beta[i] = 90+i;
        }
        dinamicGraphics.plotMainCurve(alpha, beta);
        dinamicGraphics.setCursorLimits((float) alpha[30], (float) alpha[30]);
//        dinamicGraphics.setCursorAt(5);
        dinamicGraphics.setInterfaceListener(this);
        dinamicGraphics.setGradeStatus(true);
        dinamicGraphics.setXNameTickers(Xtickers);
        dinamicGraphics.setYNameTickers(Ytickers);
        dinamicGraphics.setAxisTitles("α(°)", "β(°)");
        dinamicGraphics.setCursorStyle(new DashPathEffect(new float[]{15, 30}, 0));
        dinamicGraphics.setCursorColor(Color.MAGENTA);

        dinamicGraphics.clearBackgroundCurves();

        double[] x_background = new double []{0, 90, 180};
        double[] y_background = new double []{360, 270, 180};
        dinamicGraphics.plotBackgroundCurves(x_background, y_background);
        y_background[0] = 180;
        y_background[1] = 90;
        y_background[2] = 180;
        dinamicGraphics.plotBackgroundCurves(x_background, y_background);

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