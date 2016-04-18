package com.example.encryptfile;

import java.util.ArrayList;
import java.util.List;





import org.w3c.dom.ls.LSInput;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FilesAdapter extends BaseAdapter {

	private Context mContext;
	private List<FileItem> lstFiles = new ArrayList<FileItem>();
	

	public FilesAdapter(Context mContext, List<FileItem> lstFiles,
			int selectCount) {
		super();
		this.mContext = mContext;
		this.lstFiles = lstFiles;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lstFiles.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.list_view_item, null);
		//if(convertView==null){
			final CheckBox chkFile = (CheckBox) vi.findViewById(R.id.chkFile);
			TextView tvFileName = (TextView) vi.findViewById(R.id.tvFileName);
			//System.out.println("pos: "+position);
			tvFileName.setText(lstFiles.get(position).getName());
			ImageView imgView = (ImageView) vi.findViewById(R.id.imgIcon);
			if(lstFiles.get(position).isDirectory()){
				imgView.setImageResource(R.drawable.folder_icon);
			}
			else {
				imgView.setImageResource(R.drawable.file_icon);
			}

			tvFileName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(lstFiles.get(position).isDirectory()){
						MainActivity.getInstance().currentDir = lstFiles.get(position).getPath();
						MainActivity.getInstance().setListFiles();
					}
					else {
						if(!chkFile.isChecked()){
							chkFile.setChecked(true);
							lstFiles.get(position).setChoose(true);
						}
						else{

							chkFile.setChecked(false);
							lstFiles.get(position).setChoose(false);
						}
					}
				}
			});
			chkFile.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(chkFile.isChecked()){
						lstFiles.get(position).setChoose(true);
					}
					else{
						lstFiles.get(position).setChoose(false);
					}
					
				}
			});
			imgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(lstFiles.get(position).isDirectory()){
						MainActivity.getInstance().currentDir = lstFiles.get(position).getPath();
						MainActivity.getInstance().setListFiles();
					}
					else {
						if(!chkFile.isChecked()){
							chkFile.setChecked(true);
							lstFiles.get(position).setChoose(true);
						}
						else{

							chkFile.setChecked(false);
							lstFiles.get(position).setChoose(false);
						}
					}
				}
			});
			ImageView imgLock = (ImageView) vi.findViewById(R.id.imgKey);
			if(lstFiles.get(position).isEncrypted()){
				imgLock.setImageResource(R.drawable.key);
			}
		//}
		//else vi = convertView;
		return vi;
	}

	public List<FileItem> getLstFiles() {
		return lstFiles;
	}

	public void setLstFiles(List<FileItem> lstFiles) {
		this.lstFiles = lstFiles;
	}

}
