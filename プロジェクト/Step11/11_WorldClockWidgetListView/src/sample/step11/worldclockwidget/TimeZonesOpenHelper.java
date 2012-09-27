package sample.step11.worldclockwidget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class TimeZonesOpenHelper extends SQLiteOpenHelper {

	static String name="timezones.db";
	static int version=1;
	static CursorFactory factory=null;
	
	public TimeZonesOpenHelper(Context context){
	    super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="CREATE TABLE tzList ("+android.provider.BaseColumns._ID
			    +" INTEGER PRIMARY KEY AUTOINCREMENT, timezone Text);";
			db.execSQL(sql);
			}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
