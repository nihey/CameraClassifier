package com.classapps.cameraclassifier;

public class Element {

	String mFile;
	float mFeatures[];
	int mClass;
	
	 public Element(String pFile, float pFeatures[], int pClass) {

	 	mFile = pFile;
	 	mFeatures = pFeatures;
	 	mClass = pClass;
	}
	 
	public String getFile() {
		
		return mFile;
	}
	
	public float[] getFeatures() {
		
		return mFeatures;
	}
	
	public int getElementClass() {
		
		return mClass;
	}
}
