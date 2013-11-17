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
 *  Classe usada para confirmar a classifica��o obtida
 *  Ainda dever� ser modificado no futuro, dependendo da implementa��o de ClassifierService
 */

public class ConfirmActivity extends Activity {
	
	private ClassifierService mService;
	
	private boolean mBound = false;
	
	private TextView mTextView;
	
	private Button mYesButton;
	private Button mNoButton;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_screen);
		
		mTextView = (TextView) findViewById(R.id.confirmtextview);
		
		mYesButton = (Button) findViewById(R.id.yesbutton);
		mNoButton = (Button) findViewById(R.id.nobutton);
		
		mTextView.setText("Is the photo " + getIntent().getStringExtra(ClassifierService.EXTRA_FILE_NAME) + " from category\n 'Coding'?");
		
		mYesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mBound) {
				
					// Archive manipulation here
					
					finish();
				}
			}
		});
		
		mNoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mBound) {

					// Call Suggest activity here
					
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
	
	/** Define callbacks para a liga��o Activity -> Service */
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
