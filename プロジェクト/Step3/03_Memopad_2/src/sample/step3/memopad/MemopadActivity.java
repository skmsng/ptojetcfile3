package sample.step3.memopad;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.text.Selection;
import android.widget.EditText;

public class MemopadActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		EditText et=(EditText) findViewById(R.id.editText1);
		SharedPreferences pref=this.getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		et.setText(pref.getString("memo", ""));
		et.setSelection(pref.getInt("cursor", 0));
	}


	@Override
	protected void onStop() {
		super.onStop();
		EditText et=(EditText) findViewById(R.id.editText1);
		SharedPreferences pref=getSharedPreferences("MemoPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor=pref.edit();
		editor.putString("memo", et.getText().toString());
		editor.putInt("cursor", Selection.getSelectionStart(et.getText()));
		editor.commit();
	}
}