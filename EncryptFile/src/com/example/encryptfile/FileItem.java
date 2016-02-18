package com.example.encryptfile;

import java.io.File;

public class FileItem extends File{
	public FileItem(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	private boolean isChoose;
	private boolean isEncrypted;
	public boolean isChoose() {
		return isChoose;
	}
	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}
	public boolean isEncrypted() {
		return isEncrypted;
	}
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	
}
