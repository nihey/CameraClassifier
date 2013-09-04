package com.classapps.cameraclassifier;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ClassifierService extends Service {

	
	/**
	 *  Recebe o Broadcast e executa uma ação
	 */
	
	BroadcastReceiver CameraReceiver = new BroadcastReceiver() {
		
		public void onReceive(Context context, Intent intent) {
			
			Log.d("ClassifierService", "I have the picture");
			
			Toast.makeText(context, "I have the picture", Toast.LENGTH_SHORT).show();
			
			// O código de classificação deve ser colocado aqui.
			// Deve-se utilizar intent.getData() para ter acesso ao URI da imagem.
			
			// Abaixo, estou demonstrando isto carregando uma imagem e utilizando o algoritmo Canny para binariza-la
			
			// Neste exemplo, recebendo o getData de intent posso obter o bitmap da imagem
			Uri selected_image = intent.getData();
			
			Bitmap bmp = null;
			try {
				
				// Carregando o bitmap eh possível fazer a conversão para a estrutura Mat do OpenCV
				bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selected_image);
			}
			
			catch (Exception e) {

				e.printStackTrace();
			}
			
			// Inicializando Mat
			Mat src = new Mat();
			
			// Com esta função, é feita a conversão de Bitmap para Mat
			Utils.bitmapToMat(bmp, src);
			
			// Finaliza o bitmap
			bmp.recycle();
			
			// Checando se o carregamento foi bem sucedido
			if((src.rows() == 0) && (src.cols() == 0)) {
				
				Log.d("TEST", "Failed to load image");
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d("ClassifierService", "OnCreate()");
		
		// Inicialização do OpenCV para Android
		if (!OpenCVLoader.initDebug())
		{
		    Log.e("TEST", "Cannot connect to OpenCV Manager");
		}
		
		// Declarando o Broadcast Receiver
		registerReceiver(CameraReceiver, new IntentFilter(Camera.ACTION_NEW_PICTURE));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d("Classifier:", "OnDestroy()");
		unregisterReceiver(CameraReceiver);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
