package com.classapps.cameraclassifier;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class ClassifierService extends Service {

	FileObserver mObserver;
	
	private String mPWD = null; 

	private String mLastFile = "";
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mPWD =  (String) intent.getExtras().get(MainActivity.CLASS_CHOSEN_DIR);
		
		Log.i("Classifier:", "Folder located: " + mPWD);
		
		mObserver = new FileObserver(mPWD, FileObserver.CLOSE_WRITE) { // set up a file observer to watch this directory on sd card
			
			@Override
			public void onEvent(int event, String file) {
				
				if((!file.equals(".probe")) && (!file.equals(mLastFile))){ // check if its a "create" and not equal to .probe because thats created every time camera is launched

					Log.i("Event: ", file + " Happened");
					
					mLastFile = file;
					
					// Mat
					Mat src = Highgui.imread(mPWD + "/" + file);
					
					// Checando se o carregamento foi bem sucedido
					if((src.rows() == 0) && (src.cols() == 0)) {
						
						Log.d("TEST", "Failed to load image");
					}
					
					// Inicializa matriz de nuances de cinza
					Mat src_gray = new Mat();
					
					// Converte cores
					Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
					
					// Cria matriz para armazenar imagem binarizada
					Mat dst = new Mat(src_gray.size(), src_gray.type());
					
					// Binariza dst
					Imgproc.Canny(src_gray, dst, 10, 100);
					
					Highgui.imwrite(mPWD + "/" + file, dst);
				}
			}
		};
		mObserver.startWatching(); // start the observer
		
		return 0;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		Log.i("Classifier:", "OnCreate()");
	}

	@Override
	public void onDestroy() {
		
		Log.i("Classifier:", "OnDestroy()");
		
		mObserver.stopWatching();
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

}
