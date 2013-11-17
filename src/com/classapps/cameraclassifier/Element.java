package com.classapps.cameraclassifier;

import java.util.ArrayList;

public class Element {

	String mFile;
	ArrayList<Float> mFeatures;
	int mClass;
	
	 public Element(String pFile, ArrayList<Float> pFeatures, int pClass) {

	 	mFile = pFile;
	 	mFeatures = pFeatures;
	 	pClass = mClass;
	}
	 
	public String getFile() {
		
		return mFile;
	}
	
	public ArrayList<Float> getFeatures() {
		
		return mFeatures;
	}
	
	public int getElementClass() {
		
		return mClass;
	}
}
