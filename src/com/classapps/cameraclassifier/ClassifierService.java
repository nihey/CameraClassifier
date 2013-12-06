package com.classapps.cameraclassifier;

import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvKNearest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Serviço que roda em background e realiza a classificação
 *
 */

public class ClassifierService extends Service {

	public static final String EXTRA_FILE_NAME = "camclass_filename";
	public static final String EXTRA_FEATURES = "camclass_features";
	
	private final IBinder mBinder = new LocalBinder();
	
	private ArrayList<Element> mElements = new ArrayList<Element>();
	
	private CvKNearest mKNN = new CvKNearest();
	
	FileObserver mObserver;
	
	private Handler handler = new Handler();
	
	public static String mPWD = null; 

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
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
					
					float feat[] = new float[64];
					
					BIC.Hist(src, feat, 32);
					
					if(mElements.size() < 5) {
					
						Intent i = new Intent(getApplicationContext(), ConfirmActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra(EXTRA_FILE_NAME, file);
						i.putExtra(EXTRA_FEATURES, feat);
						getApplication().startActivity(i);
					}
					else {
						
						final Mat test = new Mat(1, 64, CvType.CV_32F);
						

						for(int j = 0; j < 64; j++) {
							
							test.put(0, j, feat[j]);	
						}

						
						Log.w("Classified", "Class: " + mKNN.find_nearest(test, 3, new Mat(), new Mat(), new Mat()));
						
						handler.post(new Runnable() {
						    public void run() {
						        Toast toast = Toast.makeText(ClassifierService.this, "Class: " + mKNN.find_nearest(test, 3, new Mat(), new Mat(), new Mat()), Toast.LENGTH_LONG);
						        toast.show();
						    }
						 });
					}
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
	
	public void addElement(String pFile, float pFeatures[], int pClass) {
		
		mElements.add(new Element(pFile, pFeatures, pClass));
		
		if(mElements.size() == 5) {
			Mat train = new Mat(5, 64, CvType.CV_32F);
			Mat labels = new Mat(5, 1, CvType.CV_32F);
			int i = 0;
			for(Element elem: mElements) {
				
				for(int j = 0; j < 64; j++) {
					
					train.put(i, j, elem.mFeatures[j]);	
				}
				labels.put(i, 0, elem.mClass);
				i++;
			}
			
//			for(i = 0; i < 5; i++) {
//				
//				for(int j = 0; j < 64; j++) {
//					Log.d("DATA", train.get(i, j)[0]+"");
//				}
//				Log.d("LABEL", labels.get(i, 0)[0]+"");
//			}
			
			mKNN.train(train, labels);
		}
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
