package sample.step10.livewallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.os.Handler;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

public class LiveWallpaper extends WallpaperService {

	private final Handler handler=new Handler();
	int interval;
	int counter;

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	public void init(){
		counter=0;
		interval=600000;
	}

	private class WallpaperEngine extends Engine {

		int[] resBitmap;
		int w, h;
		boolean mVisible;
		private final Runnable mWallpaper=new Runnable(){
			@Override
			public void run() {
				showWallpaper();
			}
		};

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			resBitmap=new int[]{R.drawable.img1,R.drawable.img2};
			init();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			handler.removeCallbacks(mWallpaper);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			w=width;
			h=height;
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			mVisible=visible;
			if(visible){
				showWallpaper();
			}else{
				handler.removeCallbacks(mWallpaper);
			}
		}

		void showWallpaper(){
			final SurfaceHolder mHolder = getSurfaceHolder();
			Bitmap bitmap;
			bitmap=loadImageFromResource();
			Canvas canvas=mHolder.lockCanvas();
			if(canvas!=null){
				canvas.drawColor(Color.BLACK);
				canvas.drawBitmap(bitmap,0,0, null);
				mHolder.unlockCanvasAndPost(canvas);
			}
			counter++;
			handler.removeCallbacks(mWallpaper);
			if(mVisible)handler.postDelayed(mWallpaper, interval);
		}

		Bitmap loadImageFromResource(){
			Bitmap bitmap;
			if(counter>=resBitmap.length)counter=0;
			Resources res=getResources();
			bitmap=BitmapFactory.decodeResource(res, resBitmap[counter]);
			bitmap=Bitmap.createScaledBitmap(bitmap, w, h, false);
			return bitmap;
		}
	}

}
