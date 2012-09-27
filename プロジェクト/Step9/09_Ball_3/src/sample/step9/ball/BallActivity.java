package sample.step9.ball;

import android.app.Activity;
import android.os.Bundle;
import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.content.Context;

public class BallActivity extends Activity implements SensorEventListener {

	public static float acceler_y, acceler_x;
	private SensorManager sensorManager;
	 PowerManager.WakeLock wl;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new BoardView(this));
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

		PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
		wl=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK+
		    PowerManager.ON_AFTER_RELEASE, "My Tag");
		wl.acquire();
		}


	@Override
	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(this);
		if(wl.isHeld())wl.release();
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}


	@Override
	public void onSensorChanged(SensorEvent event) {
		acceler_y=event.values[0];
		acceler_x=event.values[1];
	}
}