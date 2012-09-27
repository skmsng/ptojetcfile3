package sample.step11.worldclockwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.widget.RemoteViews;
import android.net.Uri;

public class WorldClockWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		updateTimes(context);
		scheduleUpdate(context);
		setListener(context);
		context.startService(new Intent(context, RemoteClockViewsService.class));
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
		int[] appWidgetIds=appWidgetManager.getAppWidgetIds(me);
		SimpleDateFormat stf=new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf=new SimpleDateFormat("y/MM/dd");
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		Date currentTime=mCalendar.getTime(); 
		rv.setTextViewText(R.id.localDate,sdf.format(currentTime));
		rv.setTextViewText(R.id.localTime,stf.format(currentTime)); 
		Intent intent = new Intent(context, RemoteClockViewsService.class);

//APIレベル11～13
/*		
		for(int i=0;i<appWidgetIds.length;i++){
		  rv.setRemoteAdapter(appWidgetIds[i], android.R.id.list, intent);
		  appWidgetManager.updateAppWidget(appWidgetIds[i],rv);
		  appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],android.R.id.list);
		}
 */
		
//APIレベル14以降
		rv.setRemoteAdapter(android.R.id.list, intent);
		appWidgetManager.updateAppWidget(me, rv);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.list);

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

	void setListener(Context context){
		ComponentName me=new ComponentName(context, WorldClockWidget.class);
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
		RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.main);
		Intent intent;
		intent=new Intent(context, TimezonePicker.class);
		PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, 0);
		rv.setOnClickPendingIntent(R.id.linearLayout1, pendingIntent);

		intent=new Intent(context, Setup.class);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rv.setPendingIntentTemplate(android.R.id.list, pendingIntent);

		appWidgetManager.updateAppWidget(me, rv);
	}
}
