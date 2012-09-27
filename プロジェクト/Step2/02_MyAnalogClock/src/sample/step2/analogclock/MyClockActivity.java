package sample.step2.analogclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyClockActivity extends Activity {

	int counter=1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick(View v){
		View layout=findViewById(R.id.layout);
		if(counter==1){
			layout.setBackgroundResource(R.drawable.img2);
			counter=2;
		}
		else
		{
			layout.setBackgroundResource(R.drawable.img1);
			counter=1;
		}
	}

}