package com.zahowenbin.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R.drawable;

public class AppInfo {
	public String packageName;
	public String name;
	public Drawable drawable;
	public Boolean isSystem;
	public Boolean isSDCard;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	public Boolean getIsSDCard() {
		return isSDCard;
	}
	public void setIsSDCard(Boolean isSDCard) {
		this.isSDCard = isSDCard;
	}
	
}
