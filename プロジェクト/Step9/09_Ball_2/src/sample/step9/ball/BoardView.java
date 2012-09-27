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
		}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.rgb(0, 128, 0));
		canvas.drawBitmap(ball, new_x-ballR, new_y-ballR,null);
if(Build.VERSION.SDK_INT>10)this.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
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
			}	
	
}
