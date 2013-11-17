package com.classapps.cameraclassifier;

import org.opencv.android.OpenCVLoader;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * Activity que inicia o programa, ela pode iniciar o service, que ficará à espera de um evento da camera.
 */

public class MainActivity extends Activity {
	
	public final static String CLASS_CHOSEN_DIR = "CL4SS_CH0SEN_DIR";
	
	/**
	 *  CheckBox para controle do service (iniciar ou parar service)
	 */
	
	private CheckBox box;
	private Button setdir;
	private String mDir = Environment.getExternalStorageDirectory().getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		Log.d("MainActivity", "OnCreate()");
		
		// Carregamento dos métodos nativos do OpenCV
		OpenCVLoader.initDebug();
		
		box = (CheckBox) findViewById(R.id.checkBox);
		
		if(isRunning()) {
			
			box.setChecked(true);
		}
		
		// Aqui são definidas as ações caso o checkbox seja marcado
		box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					
					Intent intent = new Intent(MainActivity.this, ClassifierService.class);
					intent.putExtra(CLASS_CHOSEN_DIR, mDir);
					
					startService(intent);
				}
				else {
					
					// Finaliza service
					stopService(new Intent(MainActivity.this, ClassifierService.class));
				}
			}
		});
		
		setdir = (Button) findViewById(R.id.setdir);
		
		setdir.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				startActivityForResult(new Intent(MainActivity.this, DirectoryPicker.class), DirectoryPicker.PICK_DIRECTORY);
			}
		});
	}
	
	// Indica se o service está rodando
	private boolean isRunning() {
	    
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (ClassifierService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	// Chamado quando recebe a resposta do DirectoryPicker
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		Toast.makeText(this, "Dir: " + data.getStringExtra(DirectoryPicker.CHOSEN_DIRECTORY), Toast.LENGTH_LONG).show();
		mDir = data.getStringExtra(DirectoryPicker.CHOSEN_DIRECTORY);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
