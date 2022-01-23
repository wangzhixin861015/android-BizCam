package com.bcnetech.hyphoto.data;


import com.bcnetech.hyphoto.sql.dao.ImageData;

import java.io.Serializable;

public class GridItem implements Serializable{
	private String path;
	private String time;
	private int section;
	private boolean ischeck;
	private ImageData imageData;
	private String type;


	public GridItem(String path, String time) {
		super();
		this.path = path;
		this.time = time;
	}


	public boolean ischeck() {
		return ischeck;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public ImageData getImageData() {
		return imageData;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "GridItem{" +
				"path='" + path + '\'' +
				", time='" + time + '\'' +
				", section=" + section +
				", ischeck=" + ischeck +
				", imageData=" + imageData +
				'}';
	}
}
