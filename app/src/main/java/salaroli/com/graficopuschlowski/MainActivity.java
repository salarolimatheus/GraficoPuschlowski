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
//        String[] Ytickers = new String[] { "90", "100", "110", "120", "130", "140", "150", "160", "170", "180",
//                "190", "200", "210", "220", "230", "240", "250", "260", "270", "280", "290", "300", "310", "320",
//                "330", "340", "350", "360"};
        String[] Ytickers = new String[] { "90", "120", "150", "180", "210", "240", "270", "300", "330", "360"};
        String[] Xtickers = new String[] {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180"};

        dinamicGraphics.setGradeStatus(true);
        dinamicGraphics.setXNameTickers(Xtickers);
        dinamicGraphics.setYNameTickers(Ytickers);
        dinamicGraphics.setAxisTitles("α(°)", "β(°)");
    }
}