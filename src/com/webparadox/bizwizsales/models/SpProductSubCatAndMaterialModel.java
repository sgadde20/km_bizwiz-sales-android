package com.webparadox.bizwizsales.models;

import java.io.Serializable;

public class SpProductSubCatAndMaterialModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4647296595977413718L;
	public String mSubcategoryName="";
	public String mSubcategoryId="";
	
	
	public String mMaterialName="";
	public String mMaterialId="";
	public String mProductDescription="";
	public String mProductImageURL="";
	public String mProductVideoURL="";
	public String mUnitSellingPrice="";
	public String mProductType="";
	
	public boolean mIsHeader;
}
