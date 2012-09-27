package sample.step10.livewallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

public class Preferences extends PreferenceActivity {

	 SharedPreferences prefs;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getPreferenceManager().setSharedPreferencesName("SlideshowWallpaperPrefs");
		addPreferencesFromResource(R.xml.preferences);
		prefs = this.getSharedPreferences("SlideshowWallpaperPrefs", MODE_PRIVATE);
		((EditText)findViewById(R.id.editText1)).setText(prefs.getString("Folder", ""));
		}

	@Override
	protected void onStop() {
		super.onStop();
		Editor editor=prefs.edit();
		editor.putString("Folder", 
		    ((EditText)findViewById(R.id.editText1)).getText().toString());
		editor.commit();
		}

}
