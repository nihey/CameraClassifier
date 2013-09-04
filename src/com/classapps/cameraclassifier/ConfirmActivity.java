package com.classapps.cameraclassifier;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *  Classe usada para confirmar a classificação obtida
 *  Ainda deverá ser modificado no futuro, dependendo da implementação de ClassifierService
 */

public class ConfirmActivity extends Activity {

	private TextView mTextView;
	
	private Button mYesButton;
	private Button mNoButton;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_screen);
		
		mTextView = (TextView) findViewById(R.id.confirmtextview);
		
		mYesButton = (Button) findViewById(R.id.yesbutton);
		mNoButton = (Button) findViewById(R.id.nobutton);
		
		mTextView.setText("Is this photo from category\n \"Australian Nature\"?");
		
		mYesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Archive manipulation here
				
				finish();
			}
		});
		
		mNoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Call Suggest activity here
				
				finish();
			}
		});
	};

}
