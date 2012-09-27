package sample.step9.ball;

import android.app.Activity;
import android.os.Bundle;
import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class BallActivity extends Activity implements SensorEventListener {

	public static float acceler_y, acceler_x;
	private SensorManager sensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		List<Sensor> sensors=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size()>0){
			Sensor sensor=sensors.get(0);
			sensorManager.registerListener(this, sensor,1);
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(this);
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}


	@Override
	public void onSensorChanged(SensorEvent event) {
		acceler_y=event.values[0];
		acceler_x=event.values[1];
		((TextView)findViewById(R.id.textView1)).setText(acceler_x+" , "+acceler_y);
	}
}