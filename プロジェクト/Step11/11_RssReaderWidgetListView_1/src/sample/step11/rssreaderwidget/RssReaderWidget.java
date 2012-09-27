package sample.step11.rssreaderwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.widget.RemoteViews;

public class RssReaderWidget extends AppWidgetProvider {

	Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		mContext=context;
		RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.rssreader_widget);
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
		ComponentName me=new ComponentName(context,RssReaderWidget.class);
		Intent i=new Intent(context,RssReaderActivity.class);
		PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, i, 0);
		rv.setOnClickPendingIntent(R.id.textView1, pendingIntent);
		i=new Intent(context,RssReaderWidget.class);
		pendingIntent=PendingIntent.getBroadcast(context, 0, i, 0);
		rv.setOnClickPendingIntent(android.R.id.empty, pendingIntent);
		i=new Intent();
		pendingIntent=PendingIntent.getActivity(context, 0, i, 0);
		rv.setPendingIntentTemplate(android.R.id.list, pendingIntent);
		i=new Intent(context, RemoteRssViewsService.class);
		rv.setEmptyView(android.R.id.list, android.R.id.empty);
		int[] appWidgetIds=appWidgetManager.getAppWidgetIds(me);

//APIレベル11～13
/*
 		for(int j=0;j<appWidgetIds.length;j++){            
		  rv.setRemoteAdapter(appWidgetIds[j],android.R.id.list, i); 
		  appWidgetManager.updateAppWidget(appWidgetIds[j],rv);  
		  appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[j],android.R.id.list);
		}
 */

//APIレベル14以降
		rv.setRemoteAdapter(android.R.id.list, i);
		appWidgetManager.updateAppWidget(me, rv);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.list);

	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO 自動生成されたメソッド・スタブ
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
