package sample.step11.rssreaderwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.widget.RemoteViews;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import android.os.Handler;
import android.content.SharedPreferences;

public class RssReaderWidget extends AppWidgetProvider {

	Handler handler;
	Future<?> waiting=null;
	ExecutorService executorService;
	String content;
	Context mContext;
	Runnable inMainThread=new Runnable(){
	  @Override
	  public void run() {
	    RemoteViews rv=new RemoteViews(
	        mContext.getPackageName(),R.layout.rssreader_widget);
	    AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(mContext);
	    ComponentName me=new ComponentName(mContext, RssReaderWidget.class);
	    rv.setTextViewText(R.id.textView1, content);
	    appWidgetManager.updateAppWidget(me, rv);
	  }
	};
	Runnable inReadingThread=new Runnable(){
	  @Override
	  public void run() {
	    content=RssReaderActivity.readRss(true);
	    if(content.length()>0){
	    	handler.removeCallbacks(inMainThread);
	      handler.post(inMainThread);
	    }
	  }
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		super.onReceive(context, intent);
		mContext=context;
		RemoteViews rv=new RemoteViews(context.getPackageName(),R.layout.rssreader_widget);
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
		ComponentName me=new ComponentName(context,RssReaderWidget.class);
		Intent i=new Intent(context,RssReaderActivity.class);
		PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, i, 0);
		rv.setOnClickPendingIntent(R.id.textView1, pendingIntent);
		appWidgetManager.updateAppWidget(me, rv);
		SharedPreferences prefs=context.getSharedPreferences(
		    "RssReaderPrefs",Context.MODE_PRIVATE);
		RssReaderActivity.strUrl=prefs.getString(
		    "server",context.getResources().getTextArray(R.array.ServiceUrl)[0].toString());
		handler=new Handler();
		executorService=Executors.newSingleThreadExecutor();
		if(waiting!=null)waiting.cancel(true);
		waiting=executorService.submit(inReadingThread);
		
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO 自動生成されたメソッド・スタブ
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
