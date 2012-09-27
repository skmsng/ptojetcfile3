package sample.step11.worldclockwidget;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import java.util.TimeZone;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class TimezonePicker extends ListActivity {

	String itemId;

	public static String tzchoice;
	String[] tzc;
	int sPosition;
	TextWatcher tw;
	ArrayAdapter<String> adapter;
	int tzchange;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		tzchoice=adapter.getItem(position);
		sPosition=position;
		Intent i=new Intent();
		i.putExtra("tz", tzchoice);
		setResult(RESULT_OK,i);
		TimeZonesOpenHelper tzHelper=new TimeZonesOpenHelper(this);
		SQLiteDatabase db=tzHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("timezone", tzchoice);
		if(itemId!=null)
			db.update("tzList", values,"_ID=?",new String[]{itemId});
		else
			db.insertOrThrow("tzList", null,values);
		db.close();
		tzHelper.close();
		Intent intent=new Intent(this,WorldClockWidget.class);
		this.sendBroadcast(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timezonepicker);
		Intent i=getIntent();
		itemId=i.getStringExtra("ItemID");
		ListView list = (ListView)findViewById(android.R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final EditText et=(EditText) this.findViewById(R.id.editText1);
		tzc=TimeZone.getAvailableIDs();
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tzc);
		list.setAdapter(adapter);
		list.setSelection(sPosition);
		list.pointToPosition(0, sPosition);
		tw=new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				Filter filter=adapter.getFilter();
				filter.filter(et.getText().toString());
			}
		};
		et.addTextChangedListener(tw);
	}

}
