package sample.step11.worldclockwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WorldClockWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		updateTimes(context);
		scheduleUpdate(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	void updateTimes(Context context){
		ComponentName me=new ComponentName(context, WorldClockWidget.class);
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
		RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.main);
		SharedPreferences prefs=context.getSharedPreferences(
				"TimezonePrefs",Context.MODE_PRIVATE);
		String[] tzChoice=new String[]{prefs.getString("tz1", ""),prefs.getString("tz2", "")};
		int[] tvTimezoneName=new int[]{R.id.tz1Name,R.id.tz2Name};
		int[] tvTimezoneTime=new int[]{R.id.tz1Time,R.id.tz2Time};
		SimpleDateFormat stf=new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf=new SimpleDateFormat("yD");
		SimpleDateFormat symd=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		Date currentTime=mCalendar.getTime(); 
		int localDate=Integer.parseInt(sdf.format(currentTime));
		rv.setTextViewText(R.id.localTime,symd.format(currentTime));
		for(int i=0;i<2;i++){
			TimeZone tz=TimeZone.getTimeZone(tzChoice[i]);
			stf.setTimeZone(tz);
			sdf.setTimeZone(tz);
			String tzTime=stf.format(currentTime);
			int tzDate=Integer.parseInt(sdf.format(currentTime));
			if(tzDate+1==localDate) tzTime=tzTime+" (-1)";
			else if(tzDate-1==localDate) tzTime=tzTime+" (+1)";
			rv.setTextViewText(tvTimezoneName[i],tzChoice[i]+" ("+tz.getDisplayName()+")");
			rv.setTextViewText(tvTimezoneTime[i],tzTime);
		}
		appWidgetManager.updateAppWidget(me, rv);
	}	

	void scheduleUpdate(Context context){ 
		Intent intent=new Intent(context, WorldClockWidget.class);
		intent.setAction("ACTION_REFRESH_TIMEZONES");
		PendingIntent operation=PendingIntent.getBroadcast(context, 0, intent, 0);
		long now = System.currentTimeMillis();
		long next=60000-now%60000;
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC, now+next, operation);
	}
}
