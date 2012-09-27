package sample.step11.rssreaderwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.Html;
import android.util.Xml;
import android.widget.RemoteViews;

public class RemoteRssViewsService extends RemoteViewsService {

	boolean refresh=true;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refresh=false;
		final Intent intent=new Intent(this, RssReaderWidget.class);
		sendBroadcast(intent);
	}

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent arg0) {
		return new RemoteRssViewsFactory(this.getApplicationContext(), arg0);
	}

	class RemoteRssViewsFactory implements RemoteViewsService.RemoteViewsFactory{

		int mCount;
		ArrayList<String[]> rss;
		Context mContext;
		String title;

		public RemoteRssViewsFactory(Context applicationContext, Intent intent) {
			mContext=applicationContext;
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public long getItemId(int position) {
			// TODO 自動生成されたメソッド・スタブ
			return position;
		}

		@Override
		public RemoteViews getLoadingView() {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public RemoteViews getViewAt(int position) {
			// TODO 自動生成されたメソッド・スタブ
			if(rss==null)return null;
			RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.list_item);
			rv.setTextViewText(android.R.id.text1, rss.get(position)[0]);
			rv.setTextViewText(android.R.id.text2, rss.get(position)[1]);
			final Intent  fillInIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(rss.
					get(position)[2]));
			rv.setOnClickFillInIntent(R.id.linearLayout1, fillInIntent);
			return rv;
		}

		@Override
		public int getViewTypeCount() {
			// TODO 自動生成されたメソッド・スタブ
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO 自動生成されたメソッド・スタブ
			return false;
		}

		@Override
		public void onCreate() {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void onDataSetChanged() {
			RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.rssreader_widget);
			AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(mContext);
			ComponentName me=new ComponentName(mContext, RssReaderWidget.class);
			rv.setTextViewText(android.R.id.empty, 
					getResources().getString(R.string.message_loading));
			appWidgetManager.updateAppWidget(me, rv);
			if(refresh==true||rss==null){
				ArrayList<String[]> rssTemp=readRss();

				if(!rssTemp.isEmpty()){
					rss=rssTemp;
					mCount=rss.size();
				}
			}else refresh=true;

			if(title!=null)rv.setTextViewText(R.id.textView1, title);
			rv.setTextViewText(android.R.id.empty, 
					getResources().getString(R.string.message_error));
			appWidgetManager.updateAppWidget(me, rv);
		}

		@Override
		public void onDestroy() {
			// TODO 自動生成されたメソッド・スタブ

		}

		ArrayList<String[]> readRss(){
			HttpURLConnection connection=null;

			SharedPreferences prefs=mContext.getSharedPreferences(     
					"RssReaderPrefs",Context.MODE_PRIVATE);           
			String strUrl=prefs.getString("server", mContext.getResources().
					getTextArray(R.array.ServiceUrl)[0].toString());     
			ArrayList<String[]> rssArrayList=new ArrayList<String[]>();  

			try {
				URL url=new URL(strUrl);
				connection=(HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				XmlPullParser xmlPP = Xml.newPullParser();
				xmlPP.setInput(new InputStreamReader(connection.getInputStream(),"UTF-8"));
				int eventType = xmlPP.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if(eventType == XmlPullParser.START_TAG) {
						if(xmlPP.getName().equalsIgnoreCase("channel")){
							do{
								eventType=xmlPP.next();
								if(xmlPP.getName()!=null && 
										xmlPP.getName().equalsIgnoreCase("title")){
									title=xmlPP.nextText();
									break;
								}
							}while(xmlPP.getName()!="item");
						}
						if(xmlPP.getName()!=null&&xmlPP.getName().equalsIgnoreCase("item")){
							String itemtitle="title";
							String linkurl="";
							String pubdate="";
							do{
								eventType=xmlPP.next();
								if (eventType==XmlPullParser.START_TAG){
									String tagName=xmlPP.getName();
									if(tagName.equalsIgnoreCase("title"))
										itemtitle=xmlPP.nextText();
									else if(tagName.equalsIgnoreCase("link"))
										linkurl=xmlPP.nextText();
									else if(tagName.equalsIgnoreCase("pubDate"))
										pubdate=xmlPP.nextText();
								}
							}while(!((eventType==XmlPullParser.END_TAG)&&
									(xmlPP.getName().equalsIgnoreCase("item"))));
							String[] strtmp=new String[3];                
							strtmp[0]=Html.fromHtml(itemtitle).toString();        
							strtmp[1]=pubdate;                       
							strtmp[2]=linkurl;                       
							rssArrayList.add(strtmp);    
						}
					}
					eventType = xmlPP.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				if(connection!=null){
					connection.disconnect();
				}
			}
			return rssArrayList;
		}

	}

}
