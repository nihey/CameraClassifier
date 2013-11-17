package com.classapps.cameraclassifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class ClassifierService extends Service {

	public static final String EXTRA_FILE_NAME = "camclass_filename";
	
	private final IBinder mBinder = new LocalBinder();
	
	FileObserver mObserver;
	
	public static String mPWD = null; 

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		ObjectInputStream ois = null;
		
		try {

		    ois = new ObjectInputStream(openFileInput("features"));
		    
		    float val = ois.readFloat();
		    
		    while(val != -1) {
				
				Log.d("Feature ", "0: " + val);
				val = ois.readFloat();
			}
		    ois.close();
		    
		} 
		catch (Exception ex) {

			ex.printStackTrace();
		}
		finally {
			
			try {
				
				ois.close();			
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		mPWD =  (String) intent.getExtras().get(MainActivity.CLASS_CHOSEN_DIR);
		
		Log.i("Classifier:", "Folder located: " + mPWD);
		
		mObserver = new FileObserver(mPWD, FileObserver.CLOSE_WRITE) { // set up a file observer to watch this directory on sd card
			
			@Override
			public void onEvent(int event, String file) {
				
				if((!file.equals(".probe")) && (!file.equals("features"))){ // check if its a "create" and not equal to .probe because thats created every time camera is launched

					Log.i("Event: ", file + " Happened");
					
					// Mat
					Mat src = Highgui.imread(mPWD + "/" + file);
					
					// Checando se o carregamento foi bem sucedido
					if((src.rows() == 0) && (src.cols() == 0)) {
						
						Log.d("TEST", "Failed to load image");
					}
					
					Imgproc.resize(src, src, new Size(600, 480));
					
					ArrayList<Float> feat = new ArrayList<Float>();
					
					BIC.Hist(src, feat, 32);
					
					Log.d("Histogram", "is Done");
					
					try {
					    
					    ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("features", Context.MODE_APPEND));
					    for(int i = 0; i < 64; i++) {
							
							Log.d("Feature " + i, "0: " + feat.get(i));
							oos.writeFloat(feat.get(i));
						}
					    oos.close();
					} 
					catch (Exception ex) {

						ex.printStackTrace();
					}
					Intent i = new Intent(getApplicationContext(), ConfirmActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra(EXTRA_FILE_NAME, file);
					getApplication().startActivity(i);
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
		
		return mBinder;
	}
	
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	ClassifierService getService() {

            return ClassifierService.this;
        }
    }
}
