package sample.step10.livewallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.os.Handler;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Environment;

public class LiveWallpaper extends WallpaperService {

	private final Handler handler=new Handler();
	int interval;
	int counter;
	String strFolder;

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	public void init(){
		counter=0;
		interval=600000;
		strFolder="/sdcard/mypaint/";
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
			String fn=findImageFile();
			if(fn==""){
				bitmap=loadImageFromResource();
			}else{
				bitmap=loadImageFile(fn);
			}

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

		Bitmap loadImageFile(String path){
			Bitmap bitmap;
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opt);
			int oh=opt.outHeight;
			int ow=opt.outWidth;
			BitmapFactory.Options opt2 = new BitmapFactory.Options();
			if(oh<h||ow<w){
				opt2.inSampleSize=Math.min(w/ow, h/oh);
			}else{
				opt2.inSampleSize=Math.max(ow/w, oh/h);
			}
			bitmap = BitmapFactory.decodeFile(path,opt2);
			if(h>w){
				Bitmap offbitmap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				Canvas offCanvas=new Canvas(offbitmap);
				bitmap=Bitmap.createScaledBitmap(bitmap, 
						(int)(w), (int)(w*(((double)oh)/((double)ow))), false);
				offCanvas.drawBitmap(bitmap, 0, (h-bitmap.getHeight())/2, null);
				bitmap=offbitmap;
			}else{
				Bitmap offbitmap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				Canvas offCanvas=new Canvas(offbitmap);
				bitmap=Bitmap.createScaledBitmap(bitmap,
						(int)(h*((double)ow/(double)oh)), h, false);
				offCanvas.drawBitmap(bitmap, 
						(w-bitmap.getWidth())/2, (h-bitmap.getHeight())/2, null);
				bitmap=offbitmap;
			}
			return bitmap;
		}

		String findImageFile(){
			String fn="";
			FileFilter fFilter=new FileFilter() {
				public boolean accept(File file) {
					Pattern p=Pattern.compile
							("\\.png$|\\.jpg$|\\.gif$|\\.jpeg$|\\.bmp$",Pattern.CASE_INSENSITIVE);
					Matcher m=p.matcher(file.getName());
					boolean result = m.find()&&!file.isHidden();
					return result;
				}
			};
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File folder=new File(strFolder);
				File[] fc=folder.listFiles(fFilter);
				if(fc!=null){
					if(fc.length>0){
						if(counter>=fc.length)counter=0;
						fn=fc[counter].toString();
					}
				}
			}
			return fn;
		}
	}

}
