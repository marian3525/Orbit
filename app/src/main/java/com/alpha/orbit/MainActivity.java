package com.alpha.orbit;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button compute;
    Button launchActivity;

    TextView resultOutput;
    TextView periodText;
    EditText massBaseText;
    EditText massExponentText;
    EditText radiusText;
    EditText orbitalPosition;
    EditText apoapsisText;
    EditText periapsisText;

    static BigDecimal mass;        //input
    static double position;          //input
    static double axis;              //input
    static BigDecimal G;
    static double apoapsis, periapsis;

    static double radius;
    static int massExponent;
    static double massBase;
    static double period;

    final int mRequestCode = 0x1;
    final String stringCode = "stringData";

    public static SharedPreferences prefs;

    @Override
    @TargetApi(24)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compute = (Button) findViewById(R.id.button);
        launchActivity = (Button) findViewById(R.id.button2);
        resultOutput = (TextView) findViewById(R.id.textView);
        massBaseText = (EditText) findViewById(R.id.editText);
        massExponentText = (EditText) findViewById(R.id.editText3);
        radiusText = (EditText) findViewById(R.id.editText4);
        orbitalPosition = (EditText) findViewById(R.id.editText5);
        apoapsisText = (EditText) findViewById(R.id.editText6);
        periapsisText = (EditText) findViewById(R.id.editText7);
        periodText = (TextView) findViewById(R.id.textView14);

        prefs = this.getSharedPreferences("restore", MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @TargetApi(24)
    public void onClick(View v) {
        //lengths in km and weights in kg
        G = BigDecimal.valueOf(6.67 * Math.pow(10, -11));

        try {
            radius = Double.valueOf(radiusText.getText().toString()) * 1000;
            apoapsis = Double.valueOf(apoapsisText.getText().toString()) * 1000 + radius;
            periapsis = Double.valueOf(periapsisText.getText().toString()) * 1000 + radius;
            position = Double.valueOf(orbitalPosition.getText().toString()) * 1000 + radius;
        } catch (Exception e) {
            e.printStackTrace();
            new Toast(this).makeText(this, "Please provide all data!", Toast.LENGTH_SHORT).show();
            return;
        }


        massBase = Double.valueOf(massBaseText.getText().toString());

        massExponent = Integer.valueOf(massExponentText.getText().toString());

        mass = BigDecimal.valueOf(massBase).multiply(BigDecimal.TEN.pow(BigDecimal.valueOf(massExponent)));     //mass = massBase*10^massExponent for short

        axis = (apoapsis + periapsis) / 2;

        Double u = G.multiply(mass).doubleValue();

        /* The orbital period is given by the formula: T = 2*pi*sqrt(semi major axis^3/GM)
          The result is given in seconds and formatted according to its scale in formatPeriod()
         */
        period = 2 * Math.PI * Math.sqrt(Math.pow(axis, 3) / u);

        formatPeriod(period);

        BigDecimal vSquared;

        G = G.multiply(mass);

        vSquared = G;

        /*
          The formula used to determine the orbital speed at any point along the elipse is
          v^2 = GM(2/position - 1/semi major axis)
         */
        BigDecimal aux1 = new BigDecimal(2).divide(new BigDecimal(position), 30, BigDecimal.ROUND_HALF_UP);
        BigDecimal aux2 = new BigDecimal(1).divide(new BigDecimal(axis), 30, BigDecimal.ROUND_HALF_UP);
        BigDecimal aux3 = aux1.subtract(aux2);

        vSquared = vSquared.multiply(aux3);
        double s = vSquared.doubleValue();
        s = Math.sqrt(s);
        Log.e("Double s=", "onClick: " + s);

        formatSpeed(s);
    }

    public void formatPeriod(Double period) {
        if (period < 3600) {
            period /= 60;
            period *= 10;
            this.periodText.setText(period.intValue() / 10.0 + " minutes");                                //2 decimals for all units
        } else {
            if (period < 86400) {
                period /= 3600;
                period *= 100;
                this.periodText.setText(period.intValue() / 100.0 + " hours");
            } else {
                period /= 86400;
                period *= 100;
                this.periodText.setText(period.intValue() / 100.0 + " days");
            }
        }
    }

    public void formatSpeed(Double s) {
        if (s < 1) {
            int spd = (int) (s * 1000);
            this.resultOutput.setText(spd / 10 + "," + spd % 10 + "cm/s");
            return;
        }

        if (s < 10) {
            int spd = (int) (s * 1000);
            this.resultOutput.setText(spd / 1000 + "," + spd / 10 % 100 + "m/s");
            return;
        }
        int sp = s.intValue();
        if (sp < 1000 && sp > 1) {
            this.resultOutput.setText(sp + "m/s");
        } else {
            this.resultOutput.setText(sp / 1000 + "," + sp / 10 % 100 + " km/s");
        }
    }

    public void onValuesButtonClick(View v) {
        Intent i = new Intent();

        i.setClass(this, ListActivity.class);
        startActivityForResult(i, mRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (mRequestCode == requestCode) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String dataString = data.getExtras().getString(stringCode);
                try {
                    String radiusString = dataString.substring(dataString.indexOf("Radius") + 8, dataString.indexOf("km"));
                    Log.e("!!!", "onActivityResult: " + radiusString);
                    radius = Double.valueOf(radiusString);

                    String massBaseString = dataString.substring(dataString.indexOf("Mass") + 6, dataString.indexOf("*"));
                    Log.e("!!!", "onActivityResult:" + massBaseString);
                    massBase = Double.valueOf(massBaseString);

                    String massExponentString = dataString.substring(dataString.indexOf("^") + 1, dataString.indexOf("kg"));
                    Log.e("!!!", "onActivityResult: " + massExponentString);
                    massExponent = Integer.valueOf(massExponentString);

                    updateViews();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
            }
        }
    }

    public void updateViews() {
        this.massBaseText.setText(String.valueOf(massBase));
        this.massExponentText.setText(String.valueOf(massExponent));
        this.radiusText.setText(String.valueOf(radius));
        this.periodText.setText(String.valueOf(period));
        this.apoapsisText.setText("400");
        apoapsis = 400;
        this.periapsisText.setText("400");
        periapsis = 400;
        this.orbitalPosition.setText("400");
        position = 400;
    }
}
