package bello.andrea.bonusbezier;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyRelativeLayout relativeLayout = (MyRelativeLayout) findViewById(R.id.container);

        findViewById(R.id.switch_curve_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.changeCurve();
            }
        });


    }
}
