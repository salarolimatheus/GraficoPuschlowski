package salaroli.com.graficopuschlowski;

import androidx.appcompat.app.AppCompatActivity;
import salaroli.com.dinamicgraphics.DinamicGraphics;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DinamicGraphics dinamicGraphics = findViewById(R.id.dinamic_graphics);

        String[] Ytickers = new String[] { "90", "120", "150", "180", "210", "240", "270", "300", "330", "360"};
        String[] Xtickers = new String[] {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180"};

        double[] valores_x = new double[180];
        double[] valores = new double[180];
        for (int i = 0; i < 180; i++) {
            valores_x[i] = i;
            valores[i] = 360 - 0.8333f * i;
        }

        dinamicGraphics.setGradeStatus(true);
        dinamicGraphics.setXNameTickers(Xtickers);
        dinamicGraphics.setYNameTickers(Ytickers);
        dinamicGraphics.setAxisTitles("α(°)", "β(°)");
        dinamicGraphics.plotMainCurve(valores_x, valores);
        dinamicGraphics.plotBackgroundCurves();
        dinamicGraphics.clearSecondaryCurves();
    }
}