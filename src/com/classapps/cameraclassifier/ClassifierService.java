package com.classapps.cameraclassifier;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class ClassifierService extends Service {

	private String mPWD = null; 
	
	/**
	 *  Recebe o Broadcast e executa uma ação
	 * @return 
	 */
	
//	BroadcastReceiver CameraReceiver = new BroadcastReceiver() {
//		
//		public void onReceive(Context context, Intent intent) {
//			
//			Log.d("ClassifierService", "I have the picture");
//			
//			Toast.makeText(context, "I have the picture", Toast.LENGTH_SHORT).show();
//			
//			// O código de classificação deve ser colocado aqui.
//			// Deve-se utilizar intent.getData() para ter acesso ao URI da imagem.
//			
//			// Abaixo, estou demonstrando isto carregando uma imagem e utilizando o algoritmo Canny para binariza-la
//			
//			// Neste exemplo, recebendo o getData de intent posso obter o bitmap da imagem
//			Uri selected_image = intent.getData();
//			
//			Bitmap bmp = null;
//			try {
//				
//				// Carregando o bitmap eh possível fazer a conversão para a estrutura Mat do OpenCV
//				bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selected_image);
//			}
//			
//			catch (Exception e) {
//
//				e.printStackTrace();
//			}
//			
//			// Inicializando Mat
//			Mat src = new Mat();
//			
//			// Com esta função, é feita a conversão de Bitmap para Mat
//			Utils.bitmapToMat(bmp, src);
//			
//			// Finaliza o bitmap
//			bmp.recycle();
//			
//			// Checando se o carregamento foi bem sucedido
//			if((src.rows() == 0) && (src.cols() == 0)) {
//				
//				Log.d("TEST", "Failed to load image");
//			}
//		}
//	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mPWD =  (String) intent.getExtras().get(MainActivity.CLASS_CHOSEN_DIR);
		
		Log.i("Classifier:", "Folder located: " + mPWD);
		
		FileObserver observer = new FileObserver(mPWD, FileObserver.CLOSE_WRITE) { // set up a file observer to watch this directory on sd card
			
			@Override
			public void onEvent(int event, String file) {
				
				Log.i("Event: ", "Something Happened");
				Log.i("Event: ", file + " Happened");

				
				if(event == FileObserver.CREATE && (!file.equals(".probe"))){ // check if its a "create" and not equal to .probe because thats created every time camera is launched
					
					Log.i("HEY!!", "File created [" + android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + file + "]");
					
					Intent i = new Intent(getApplicationContext(), ConfirmActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}
		};
		observer.startWatching(); // start the observer
		
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
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

}
