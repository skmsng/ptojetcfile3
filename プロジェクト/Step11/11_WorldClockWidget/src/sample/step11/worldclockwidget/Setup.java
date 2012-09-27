package sample.step11.worldclockwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Setup extends Activity {
	
	Button btn1,btn2;
	TextView[] tv;
	String[] tz;
	OnClickListener btnClick=new OnClickListener(){
	  public void onClick(View v) {
	    Intent i=new Intent(Setup.this,TimezonePicker.class);
	    switch (v.getId()){
	    case R.id.button1:
	      startActivityForResult(i,0);
	      break;
	    case R.id.button2:
	      startActivityForResult(i,1);
	      break;
	    } 
	  }
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			  tz[requestCode]=data.getStringExtra("tz");
			  tv[requestCode].setText(tz[requestCode]);
			  writePreferences(this);
			}
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		btn1=(Button) this.findViewById(R.id.button1);
		btn2=(Button) this.findViewById(R.id.button2);
		tv=new TextView[]{(TextView) this.findViewById
		    (R.id.textView1),(TextView) this.findViewById(R.id.textView2)};
		btn1.setOnClickListener(btnClick);
		btn2.setOnClickListener(btnClick);
		readPreferences(this);
		}

	void readPreferences(Context c){
		  SharedPreferences prefs=c.getSharedPreferences("TimezonePrefs", MODE_PRIVATE);
		  tz=new String[]{prefs.getString("tz1",""),prefs.getString("tz2","")};
		  for(int i=0;i<2;i++)if(tz[i].length()>0)tv[i].setText(tz[i]);
		}
	
	void writePreferences(Context c){
		  SharedPreferences prefs=c.getSharedPreferences("TimezonePrefs", MODE_PRIVATE);
		  SharedPreferences.Editor editor=prefs.edit();
		  editor.putString("tz1", tz[0]);
		  editor.putString("tz2", tz[1]);
		  editor.commit();
		  Intent i=new Intent(this, WorldClockWidget.class);
		  i.setAction("ACTION_REFRESH_TIMEZONES");
		  PendingIntent operation = PendingIntent.getBroadcast(this, 0, i, 0);
		  try {operation.send();}catch (CanceledException e){}
		}
	
}
