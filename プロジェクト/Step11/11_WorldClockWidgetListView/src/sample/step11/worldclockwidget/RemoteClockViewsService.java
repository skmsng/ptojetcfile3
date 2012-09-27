package sample.step11.worldclockwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.RemoteViews;

public class RemoteClockViewsService extends RemoteViewsService {

	Date currentTime;
	int localDate;
	static    SimpleDateFormat stf=new SimpleDateFormat("HH:mm");
	static SimpleDateFormat sdf=new SimpleDateFormat("yD");
	Cursor cursor;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RemoteClockViewsFactory(this.getApplicationContext(),intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Intent intent=new Intent(this,WorldClockWidget.class);
		PendingIntent operation=PendingIntent.getBroadcast(this, 0, intent, 0);
		long now = System.currentTimeMillis();
		long next=3000;
		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC, now+next, operation);
		}

	class RemoteClockViewsFactory implements RemoteViewsService.RemoteViewsFactory{
		Context mContext;
		int mCount;
		public RemoteClockViewsFactory(Context applicationContext, Intent intent) {
			mContext=applicationContext;
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.list_item);
			String tzName;
			String tzTime;
			cursor.moveToPosition(position);
			tzName=cursor.getString(1);
			TimeZone tz=TimeZone.getTimeZone(tzName);
			stf.setTimeZone(tz);
			sdf.setTimeZone(tz);
			tzTime=stf.format(currentTime);
			int tzDate=Integer.parseInt(sdf.format(currentTime));
			if(tzDate+1==localDate) tzTime=tzTime+" (-1)";
			else if(tzDate-1==localDate) tzTime=tzTime+" (+1)";
			rv.setTextViewText(android.R.id.text1, tzName+" ("+tz.getDisplayName()+")");
			rv.setTextViewText(android.R.id.text2, tzTime);
			Bundle extras = new Bundle();
			extras.putInt("ItemPosition", position);
			Intent fillInIntent = new Intent();
			fillInIntent.putExtras(extras);
			rv.setOnClickFillInIntent(R.id.linearLayout1, fillInIntent);
			return rv;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public void onCreate() {
		}

		@Override
		public void onDataSetChanged() {
			TimeZonesOpenHelper tzHelper=new TimeZonesOpenHelper(mContext);
			SQLiteDatabase db=tzHelper.getReadableDatabase();
			String[] cols={"_ID","timezone"};
			cursor=db.query("tzList",cols,null,null,null, null, null);
			mCount=cursor.getCount();
			db.close();
			tzHelper.close();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			currentTime=mCalendar.getTime(); 
			sdf.setTimeZone(TimeZone.getDefault());
			localDate=Integer.parseInt(sdf.format(currentTime));

		}

		@Override
		public void onDestroy() {
			cursor.close();
		}
	}
}
