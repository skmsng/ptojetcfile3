package sample.step4.calculator;

import android.app.Activity;
import android.os.Bundle;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import android.content.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends Activity {

	String strTemp="";
	String strResult="0";
	int operator=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}


	public void numKeyOnClick(View v){
		String strInKey=(String) ((Button)v).getText();
		if(strInKey.equals(".")){
			if(strTemp.length()==0){
				strTemp="0.";
			}else{
				if(strTemp.indexOf(".")==-1){
					strTemp=strTemp+".";
				}
			}
		}else{
			strTemp=strTemp+strInKey;
		}
		showNumber(strTemp);
	}

	private void showNumber(String strNum){
		DecimalFormat form=new DecimalFormat("#,##0");
		String strDecimal="";
		String strInt="";
		String fText="";
		if(strNum.length()>0){
			int decimalPoint=strNum.indexOf(".");
			if(decimalPoint>-1){
				strDecimal=strNum.substring(decimalPoint);
				strInt=strNum.substring(0,decimalPoint);
			}else{
				strInt=strNum;
			}
			fText=form.format(Double.parseDouble(strInt))+strDecimal;
		}else fText="0";
		((TextView)findViewById(R.id.displayPanel)).setText(fText);
	}

	public void functionKeyOnClick(View v){
		switch(v.getId()){
		case R.id.keypadAC:
			strTemp="";
			strResult="0";
			operator=0;
			break;
		case R.id.keypadC:
			strTemp="";
			break;
		case R.id.keypadBS:
			if(strTemp.length()==0)return;
			else strTemp=strTemp.substring(0,strTemp.length()-1);
			break;
		case R.id.keypadCopy:
			ClipboardManager cm=(ClipboardManager) getSystemService(CLIPBOARD_SERVICE
					);
			cm.setText(((TextView)findViewById(R.id.displayPanel)).getText());
			return;
		}
		showNumber(strTemp);
	}
}