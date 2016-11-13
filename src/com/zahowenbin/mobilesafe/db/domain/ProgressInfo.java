package com.zahowenbin.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class ProgressInfo {
	public String packageNmae;
	public String name;
	public Drawable icon;
	public long dirtySpace;
	public Boolean isCheck = false;
	public Boolean isSystem;
	public String getPackageNmae() {
		return packageNmae;
	}
	public void setPackageNmae(String packageNmae) {
		this.packageNmae = packageNmae;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getDirtySpace() {
		return dirtySpace;
	}
	public void setDirtySpace(long dirtySpace) {
		this.dirtySpace = dirtySpace;
	}
	public Boolean getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(Boolean isCheck) {
		this.isCheck = isCheck;
	}
	public Boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	
}
