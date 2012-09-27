package sample.step9.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Region;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BoardView extends View {

	Timer timer;
	Bitmap ball;
	int w,h;
	static int ballR=16;
	static Context mContext;
	static Vibrator vib;
	float new_y=0;
	float speed_y=0;
	float new_x=0;
	float speed_x=0;
	float scale=25f;
	float time=0.04f;
	Path hole, holeCenter;
	Region screen, rHole,rHoleCenter;
	boolean inTheHole=false;
	static final int gameDuration=20000; 
	int score, hiScore,timeLeft;
	
	public BoardView(Context context) {
		super(context);
		mContext=context;
		vib=((Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE));
		setFocusable(true);
		Display disp=((WindowManager)mContext.getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		w=disp.getWidth();
		h=disp.getHeight();
		if(w>480)ballR=16;
		else ballR=8;
		ball=BitmapFactory.decodeResource(getResources(), R.drawable.ball);
		ball=Bitmap.createScaledBitmap(ball, ballR*2, ballR*2, false);
		hole=new Path();
		hole.addCircle(w/2,h/2-(ballR/8),(int)(ballR*1.5),Direction.CW);
		screen=new Region(0,0,w,h);
		rHole=new Region();
		rHole.setPath(hole, screen);
		holeCenter=new Path();
		holeCenter.addCircle(w/2,h/2, (int)(ballR*1.2), Direction.CCW);
		rHoleCenter=new Region();
		rHoleCenter.setPath(holeCenter, screen);
		SharedPreferences prefs=mContext.getSharedPreferences("BallScorePrefs", 
			    Context.MODE_PRIVATE);
			hiScore=prefs.getInt("hiScore", 0);
			timeLeft=gameDuration;
			this.setOnTouchListener(new OnTouchListener(){
			  public boolean onTouch(View arg0, MotionEvent arg1) {
			    if(timeLeft<=0){
			      new_x=0;
			      new_y=0;
			      score=0;
			      timeLeft=gameDuration;
			      startTimer();
			    }
			    return false;
			  }
			});
			}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.rgb(0, 128, 0));

		Paint paint=new Paint();   
		paint.setColor(Color.DKGRAY);   
		canvas.drawPath(hole,paint);   
		paint.setColor(Color.BLACK);   
		canvas.drawPath(holeCenter,paint); 

		canvas.drawBitmap(ball, new_x-ballR, new_y-ballR,null);
		if(inTheHole){         
			paint.setColor(Color.argb(180, 0, 0,0));
			canvas.drawPath(holeCenter,paint); 
		}     

		if(Build.VERSION.SDK_INT>10)this.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		paint.setColor(Color.BLACK);
		paint.setTextSize(h/16);
		paint.setTextAlign(Align.RIGHT);
		   
		canvas.drawText(getResources().getString(R.string.label_timeleft)
		  +String.valueOf((int)(timeLeft/1000)),(int)(w*0.95), h/12, paint);
		canvas.drawText(getResources().getString(R.string.label_score)
		  +String.valueOf(score), (int)(w*0.95), h/6, paint);
		canvas.drawText(getResources().getString(R.string.label_hiscore)
		  +String.valueOf(hiScore), (int)(w*0.95), h/4, paint);
		if(timeLeft<=0){
		  paint.setTextAlign(Align.CENTER);
		  canvas.drawText(getResources().getString(R.string.message_replay), 
		      w/2, (int)(h*0.9), paint);
		}
		}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if(visibility==View.VISIBLE){
			startTimer();
		}else{
			timer.cancel();
		}
	}

	public void startTimer(){
		if(timer!=null) timer.cancel();
		timer=new Timer();
		final android.os.Handler handler = new android.os.Handler();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				handler.post( new Runnable(){
					public void run(){
						newPos();
						BoardView.this.invalidate();
						timeLeft=timeLeft-40;     
						  if(timeLeft<=0)timer.cancel(); 
					}
				});
			}
		}
		, 0, (int)(1000/scale));
	}


	void newPos(){
		new_x=new_x+(((BallActivity.acceler_x*time*scale)/2)+speed_x*time*scale);
		new_y=new_y+(((BallActivity.acceler_y*time*scale)/2)+speed_y*time*scale);
		if(new_x>=w-(ballR)){
			new_x=w-(ballR);
			speed_x=-Math.abs(speed_x)*0.8f;
			if(Math.abs(speed_x)>1){
				vib.vibrate(50);
			}
		}
		else if(new_x<=ballR){
			new_x=ballR;
			speed_x=Math.abs(speed_x)*0.8f;
			if(Math.abs(speed_x)>1){
				vib.vibrate(50);
			}
		}else{
			speed_x=(speed_x+(BallActivity.acceler_x*time*scale))*0.95f;
		}
		if(new_y>=h-(ballR)){
			new_y=h-(ballR);
			speed_y=-Math.abs(speed_y)*0.8f;
			if(Math.abs(speed_y)>1){
				vib.vibrate(50);
			}
		}
		else if(new_y<=ballR){
			new_y=ballR;
			speed_y=Math.abs(speed_y)*0.8f;
			if(Math.abs(speed_y)>1){
				vib.vibrate(50);
			}
		}else{
			speed_y=(speed_y+(BallActivity.acceler_y*time*scale))*0.95f;
		}

		if(rHole.contains((int)new_x, (int)new_y)){
			if(rHoleCenter.contains((int)new_x, (int)new_y)&
					Math.abs(BallActivity.acceler_x)<=1.0 &
					Math.abs(BallActivity.acceler_y)<=1.0&
					Math.abs(speed_x)<1.50 & Math.abs(speed_y)<1.50){
				getIn();
			}else{
				vib.vibrate(50);
				speed_x=(int)(speed_x+(w/2-new_x)*0.2);
				speed_y=(int)(speed_y+(h/2-new_y)*0.2);
			}
		}
	}	

	void getIn(){
		speed_x=0;
		speed_y=0;
		new_x=w/2;
		new_y=h/2;
		inTheHole=true;
		if(timer!=null) timer.cancel();
		addPoint(10);
		timer=new Timer();
		final android.os.Handler handler = new android.os.Handler();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				handler.post( new Runnable(){
					public void run(){
						new_x=0;
						new_y=0;
						inTheHole=false;
						startTimer();
					}
				});
			}
		}
		, 500);
	}
	
	void addPoint(int point){
		  score=score+point;
		  if(score>hiScore){
		    hiScore=score;
		    SharedPreferences prefs=mContext.getSharedPreferences(
		        "BallScorePrefs",Context.MODE_PRIVATE);
		    Editor editor=prefs.edit();
		    editor.putInt("hiScore", hiScore);
		    editor.commit();
		  }
		}
	
}
