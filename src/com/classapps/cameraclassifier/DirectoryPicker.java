package com.classapps.cameraclassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class DirectoryPicker extends ListActivity {

	public static final String CHOSEN_DIRECTORY = "chosenDir";
	public static final int PICK_DIRECTORY = 0x5EA1;
	
	private static File mDir;
	private static Button mConfirm;
	private boolean showHidden = false;
	private boolean onlyDirs = true ;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.chooser_list);
		
        mDir = Environment.getExternalStorageDirectory();
        
        setTitle(mDir.getAbsolutePath());
        
        mConfirm = (Button) findViewById(R.id.btnChoose);

        mConfirm.setText("Choose '" + mDir.getAbsolutePath()  + "'");
        
        mConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	returnDir(mDir.getAbsolutePath());
            }
        });
        
        getListView().setTextFilterEnabled(true);
        
        if(!mDir.canRead()) {
        	
        	return;
        }
        
        String[] names = names(filter(mDir.listFiles(), onlyDirs, showHidden));
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, names));        	
        

        getListView().setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		
        		if(!filter(mDir.listFiles(), onlyDirs, showHidden).get(position).isDirectory()) {
        			
        			return;
        		}
        		String path = filter(mDir.listFiles(), onlyDirs, showHidden).get(position).getAbsolutePath();
        		
        		mDir = new File(path);
        		
        		setTitle(mDir.getAbsolutePath());
        		mConfirm.setText("Choose '" + mDir.getAbsolutePath()  + "'");
        		
                String[] names = names(filter(mDir.listFiles(), onlyDirs, showHidden));
                setListAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, names));    
        	}
        });
    }
	
    private void returnDir(String path) {
    	
    	Intent result = new Intent();
    	
    	result.putExtra(CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
    	
        finish();    	
    }

	public ArrayList<File> filter(File[] file_list, boolean onlyDirs, boolean showHidden) {
		
		ArrayList<File> files = new ArrayList<File>();
		
		for(File file: file_list) {
			if(onlyDirs && !file.isDirectory())
				continue;
			if(!showHidden && file.isHidden())
				continue;
			files.add(file);
		}
		
		Collections.sort(files);
		
		return files;
	}
	
	public String[] names(ArrayList<File> files) {
		
		String[] names = new String[files.size()];
		int i = 0;
		
		for(File file: files) {
			names[i] = file.getName();
			i++;
		}
		
		return names;
	}
}

