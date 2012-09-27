package sample.step11.worldclockwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Setup extends Activity {
	
	Button btn1,btn2;
	TextView tv;
	String tz;
	int position;
	String itemId;
	Cursor cursor; 
	 
	OnClickListener btnClick=new OnClickListener(){
	  public void onClick(View v) {
	    Intent i;     
	    switch (v.getId()){
	    case R.id.button1:
	      i=new Intent(Setup.this,TimezonePicker.class);
	      i.putExtra("ItemID", itemId);
	      startActivity(i);
	      Setup.this.finish();
	      break;
	    case R.id.button2:
	      i=new Intent(Setup.this,WorldClockWidget.class);
	      deleteTimezone(position);
	      Setup.this.sendBroadcast(i);
	      Setup.this.finish();
	      break;
	    } 
	  }
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		btn1=(Button) this.findViewById(R.id.button1);
		btn2=(Button) this.findViewById(R.id.button2);
		tv=(TextView) this.findViewById(R.id.textView1);
		Intent i=getIntent();
		position=i.getIntExtra("ItemPosition", 0);
		btn1.setOnClickListener(btnClick);
		btn2.setOnClickListener(btnClick);
		TimeZonesOpenHelper tzHelper=new TimeZonesOpenHelper(this);
		SQLiteDatabase db=tzHelper.getWritableDatabase();
		cursor=db.query("tzList", new String[] {android.provider.BaseColumns._ID,"timezone"},
		    null, null, null, null, null);
		startManagingCursor(cursor);
		cursor.moveToPosition(position);
		itemId=String.valueOf(cursor.getInt(0));
		tv.setText(String.valueOf(position)+": "+cursor.getString(1));
		db.close();
		tzHelper.close();
		
	}

	void deleteTimezone(int position){
		  TimeZonesOpenHelper tzHelper=new TimeZonesOpenHelper(this);
		  SQLiteDatabase db=tzHelper.getWritableDatabase();
		  db.delete("tzList", "_id=’"+itemId+"’",null);
		  db.close();
		  tzHelper.close();
		}
	
	
}
