package com.classapps.cameraclassifier;

import com.classapps.cameraclassifier.ClassifierService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *  Classe usada para confirmar a classificação obtida
 *  Ainda deverá ser modificado no futuro, dependendo da implementação de ClassifierService
 */

public class ConfirmActivity extends Activity {
	
	private ClassifierService mService;
	
	private boolean mBound = false;
	
	private TextView mTextView;
	
	private Button mYesButton;
	private Button mNoButton;
	
	private String mFile;
	private float mFeatures[];
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_screen);
		
		mFile = getIntent().getStringExtra(ClassifierService.EXTRA_FILE_NAME);
		mFeatures = getIntent().getFloatArrayExtra(ClassifierService.EXTRA_FEATURES);
		
		mTextView = (TextView) findViewById(R.id.confirmtextview);
		
		mYesButton = (Button) findViewById(R.id.yesbutton);
		mNoButton = (Button) findViewById(R.id.nobutton);
		
		mTextView.setText("Is the photo " + mFile + " from category\n '0'?");
		
		mYesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mBound) {
				
					mService.addElement(mFile, mFeatures, 0);
					
					finish();
				}
			}
		});
		
		mNoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mBound) {

					mService.addElement(mFile, mFeatures, 1);
					
					finish();
				}
			}
		});
	};
	
	@Override
	protected void onResume() {

		super.onResume();
		bindService(new Intent(this, ClassifierService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		unbindService(mConnection);
	}
	
	/** Define callbacks para a ligação Activity -> Service */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            
            Log.d("ChatActvitity", "We're bound together forever");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
