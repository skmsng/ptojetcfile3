package sample.step10.livewallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;

public class Preferences extends PreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener {
	 SharedPreferences prefs;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getPreferenceManager().setSharedPreferencesName("SlideshowWallpaperPrefs");
		addPreferencesFromResource(R.xml.preferences);
		prefs = this.getSharedPreferences("SlideshowWallpaperPrefs", MODE_PRIVATE);
		((EditText)findViewById(R.id.editText1)).setText(prefs.getString("Folder", ""));
		 prefs.registerOnSharedPreferenceChangeListener(this);
		 }

	@Override
	protected void onStop() {
		super.onStop();
		Editor editor=prefs.edit();
		editor.putString("Folder", 
		    ((EditText)findViewById(R.id.editText1)).getText().toString());
		editor.commit();
		}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		((EditText)findViewById(R.id.editText1)).setText(prefs.getString("Folder", ""));
		
	}

	public void onClick(View v){
		  Intent intent=new Intent(this, FilePicker.class);
		  startActivity(intent);
		}
	
}
