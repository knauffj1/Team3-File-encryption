package com.example.encryptfile;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context mContext;
	private static MainActivity sMainActivity;
	private FilesAdapter mAdapter;
	private ListView lstView;
	private List<FileItem> lstFiles = new ArrayList<FileItem>();
	public String currentDir;
	private int selectedCount = 0;
	private EditText edtFileName;
	private List<String> lstFileEcrypted = new ArrayList<String>();
	private ObjectMapper jsonMapper = new ObjectMapper();
	private List<FileItem> lstCopy = new ArrayList<FileItem>();
	private List<FileItem> lstCut = new ArrayList<FileItem>();
	private List<FileItem> lstStore = new ArrayList<FileItem>();
	private boolean copy = false;
	private boolean cut = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get sharedpreferences
		SharedPreferences sharedPref = getSharedPreferences("security",
				Context.MODE_PRIVATE);
		
		//Get list file encrypted saved by string
		String savedText = sharedPref.getString("listencrypted", null);
		if (savedText != null) {
			try {
				//Convert list to List<String>
				lstFileEcrypted = jsonMapper.readValue(
						savedText,
						jsonMapper.getTypeFactory().constructCollectionType(
								List.class, String.class));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sMainActivity = this;
		
		//Get listview file
		lstView = (ListView) findViewById(R.id.listViewFile);
		edtFileName = (EditText) findViewById(R.id.edtFileName);
		mContext = this;
		
		//Set current dir
		File rootDir = Environment.getExternalStorageDirectory();
		currentDir = rootDir.getAbsolutePath();
		setListFiles();

	}

	public static MainActivity getInstance() {
		return sMainActivity;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.cut) {
			//Set lstCut to null
			lstCut = new ArrayList<FileItem>();
			
			//Check all files if file is checked
			for (FileItem file : mAdapter.getLstFiles()) {
				if (file.isChoose()) {
					lstCut.add(file);
				}
			}
			cut = true;
			copy = false;
		}
		if (id == R.id.copy) {
			
			// set lst Copy to null
			lstCopy = new ArrayList<FileItem>();
			
			//Check all files are display, if file is checked add this to 
			for (FileItem file : mAdapter.getLstFiles()) {
				if (file.isChoose()) {
					lstCopy.add(file);
				}
			}
			copy = true;
			cut = false;
		}
		if (id == R.id.paste) {
			//if cut
			if (cut) {
				//show login with option 2
				showLogin(2);
			}
			//if cupy
			if (copy) {
				//Call copyFile function
				copyFile();
			}
		}
		if (id == R.id.delete) {
			//Show login with option 1
			showLogin(1);
		}
		if (id == R.id.new_file) {
			//Show alert with editext to type file name
			AlertDialog.Builder alert = new Builder(this);
			final EditText input = new EditText(MainActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setLayoutParams(lp);
			alert.setView(input);
			//Button cancel of alert
			alert.setNegativeButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
			//Button OK of alert
			alert.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					//Create new file or replace if file exist
					String newFilePath = currentDir + "/"
							+ input.getText().toString();
					File newFile = new File(newFilePath);
					try {
						
						//Write string 'test' to new file
						FileOutputStream output = new FileOutputStream(newFile);
						output.write("test".getBytes());
						output.flush();
						output.close();

						//Refresh list file displaying
						setListFiles();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			alert.setTitle("File name:");
			alert.setMessage("Type File Name:");
			
			//Show alert
			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	//Display list file in current dir to listview
	public void setListFiles() {
		
		//Set lstFiles to null
		lstFiles = new ArrayList<FileItem>();
		File dir = new File(currentDir);
		
		//Get all file and folder current dir
		File[] files = dir.listFiles();

		//Add each file to lstFiles
		for (File file : files) {
			FileItem item = new FileItem(file.getPath());
			item.setChoose(false);
			if (lstFileEcrypted.contains(item.getPath())) {
				item.setEncrypted(true);
			} else {
				item.setEncrypted(false);
			}
			lstFiles.add(item);
		}
		//init FilesAdapter object and set Adapter to listview
		mAdapter = new FilesAdapter(mContext, lstFiles, selectedCount);
		lstView.setAdapter(mAdapter);
	}

	//Button search click
	public void btnSearchClick(View v) {
		if (edtFileName.getText().toString().equals("")) {
			setListFiles();
			return;
		}
		//Set lstFiles to null
		lstFiles = new ArrayList<FileItem>();
		//Call search file method
		searchFile(currentDir);
		
		//Refresh listview
		mAdapter = new FilesAdapter(mContext, lstFiles, selectedCount);
		lstView.setAdapter(mAdapter);
	}

	private void searchFile(String currentPath) {
		
		
		File currentFolder = new File(currentPath);
		if (currentFolder.getName().contains(edtFileName.getText().toString())) {
			lstFiles.add(new FileItem(currentPath));
		}
		File[] files = currentFolder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				searchFile(file.getPath());
			} else {
				if (file.getName().contains(edtFileName.getText().toString())) {
					lstFiles.add(new FileItem(file.getPath()));
				}
			}
		}
	}

	//Up to parent folder
	public void btnUpClick(View v) {
		try {
			
			//Get parent path 
			String lastDir = currentDir.split("/")[currentDir.split("/").length - 1];
			currentDir = currentDir.replace("/" + lastDir, "");
			
			//Refresh list file
			setListFiles();
		} catch (Exception e) {
			e.printStackTrace();
			showMessage("Error", "This is root dir!");
		}
	}

	//Button copy click event
	public void btnCopyClick(View v) {
		
		// set lst Copy to null
		lstCopy = new ArrayList<FileItem>();
		
		//Check all files are display, if file is checked add this to 
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				lstCopy.add(file);
			}
		}
		copy = true;
		cut = false;
	}

	//Button copy click event
	public void btnStoreClick(View v) throws IOException {

		// set lst Store to null
		lstStore = new ArrayList<FileItem>();

		//Check all files are display, if file is checked add this to Array
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				lstStore.add(file);

				// Execute new StoreTask
				new StoreTask().execute(file.toString());

				Toast.makeText(this, file.toString() + " has been uploaded successfully!", Toast.LENGTH_LONG).show();
				System.out.println(file.getAbsolutePath());
			}
		}

	}

	//Button paste click event
	public void btnPasteClick(View v) {
		
		//if cut
		if (cut) {
			
			//show login with option 2
			showLogin(2);
		}
		
		//if cupy
		if (copy) {
			//Call copyFile function
			copyFile();
		}
	}

	
	public void btnCutClick(View v) {
		//Set lstCut to null
		lstCut = new ArrayList<FileItem>();
		
		//Check all files if file is checked
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				lstCut.add(file);
			}
		}
		cut = true;
		copy = false;
	}

	//Button delete click event
	public void btnDeleteClick(View v) {
		
		//Show login with option 1
		showLogin(1);
	}

	//Button encrypt file click event
	public void btnEncryptClick(View v) {
		int count = 0;
		
		//Check each file in list file displaying
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				
				//if file is not encrypt
				if (!file.isEncrypted()) {
					try {
						
						//Read file to byte[]
						byte[] fileData = Encrypt.read(file);
						
						//Encrypt password 128 bit
						byte[] key = Encrypt.generateFileKey();
						
						//saving the key for use later (will be changed to be more secure)
						SharedPreferences sharedPref = getSharedPreferences("security",
								Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						String keyString = Base64.encodeToString(key, Base64.DEFAULT);
						editor.putString("key-" + file.toString(), keyString);
						editor.commit();
						
						//Encrypt byte[] file
						byte[] encryptedData = Encrypt
								.encodeFile(key, fileData);
						
						//Write byte[] encrypted to file
						FileOutputStream output = new FileOutputStream(file);
						output.write(encryptedData);
						output.flush();
						output.close();
						
						//Add file path to lstFileEncrypted
						lstFileEcrypted.add(file.getPath());
						//save lstFileEncrypted as json
						saveSharedPreference("listencrypted",
								jsonMapper.writeValueAsString(lstFileEcrypted));
						showMessage("Success", "Encrypt file success!");
						
						//Refresh list file displaying
						for (FileItem item : lstFiles) {
							if (lstFileEcrypted.contains(item.getPath())) {
								item.setEncrypted(true);
							} else {
								item.setEncrypted(false);
							}
							item.setChoose(false);
						}
						mAdapter = new FilesAdapter(mContext, lstFiles,
								selectedCount);
						lstView.setAdapter(mAdapter);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						showMessage("Error!", "Encrypt file failed!");

					}
				}
			}
		}
	}

	public void btnDecryptClick(View v) {
		showLogin(0);

	}

	
	//Button new file click event
	public void btnNewFileClick(View v) {
		
		//Show alert with editext to type file name
		AlertDialog.Builder alert = new Builder(this);
		final EditText input = new EditText(MainActivity.this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alert.setView(input);
		//Button cancel of alert
		alert.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		//Button OK of alert
		alert.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				//Create new file or replace if file exist
				String newFilePath = currentDir + "/"
						+ input.getText().toString();
				File newFile = new File(newFilePath);
				try {
					
					//Write string 'test' to new file
					FileOutputStream output = new FileOutputStream(newFile);
					output.write("test".getBytes());
					output.flush();
					output.close();

					//Refresh list file displaying
					setListFiles();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		alert.setTitle("File name:");
		alert.setMessage("Type File Name:");
		
		//Show alert
		alert.show();
	}

	private void saveSharedPreference(String key, String value) {
		SharedPreferences sharedPref = getSharedPreferences("security",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void showMessage(String title, String message) {
		AlertDialog.Builder alert = new Builder(this);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
	}

	public boolean login = false;
	public boolean waiting = false;

	//Show login with param option
	public boolean showLogin(final int option) {

		login = false;
		
		//Show alert with edittext for user type password
		AlertDialog.Builder alert = new Builder(this);
		final EditText edtPassword = new EditText(MainActivity.this);
		edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		edtPassword.setLayoutParams(lp);
		alert.setView(edtPassword);
		alert.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alert.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//if user does not type pass show message and return
				if (edtPassword.getText().toString().equals("")) {
					showMessage("Error", "You must type password!");
					return;
				}
				
				//Get password user set and encrypt password
				SharedPreferences sharedPref = getSharedPreferences("security",
						Context.MODE_PRIVATE);
				String password = sharedPref.getString("password", null);
				String input = "";
				try {
					input = new String(Encrypt.generateKey(edtPassword
							.getText().toString()), "UTF8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (password.equals(input)) {
					switch (option) {
					case 0:
						decryptFile();
					case 1:
						deleteFile();
					case 2:
						cutFile();
					default:
						break;
					}
				} else {
					showMessage("Error!", "Password incorrect!");
				}
				waiting = false;
			}
		});
		alert.setTitle("Login:");
		alert.setMessage("Type the password:");
		alert.show();
		return login;
	}

	public void deleteFile() {
		//Delete file in list File displaying if this checked
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				file.delete();
				mAdapter.getLstFiles().remove(file);
			}
		}
		//Refresh lstView
		lstView.setAdapter(mAdapter);
	}

	public void decryptFile() {
		for (FileItem file : mAdapter.getLstFiles()) {
			if (file.isChoose()) {
				if (file.isEncrypted()) {
					try {
						System.out.println("File: " + file.toString());
						//Get file byte[]
						byte[] fileData = Encrypt.read(file);
						
						//retrieving key from shared prefs
						SharedPreferences sharedPref = getSharedPreferences("security",
								Context.MODE_PRIVATE);
						String keyString = sharedPref.getString("key-" + file.toString(), null);
						byte [] key = Base64.decode(keyString, Base64.DEFAULT);
						
						//Generate key and decrypt file byte[]
						//byte[] key = Encrypt.generateKey("password");

						String password = sharedPref.getString("password", null);
						System.out.println(password);
						System.out.println(key);
						byte[] encryptedData = Encrypt
								.decodeFile(key, fileData);
						
						//Write decrypted byte[] to file
						FileOutputStream output = new FileOutputStream(file);
						output.write(encryptedData);
						output.flush();
						output.close();
						
						//Remove file path from saved list file encrypted
						lstFileEcrypted.remove(file.getPath());
						saveSharedPreference("listencrypted",
								jsonMapper.writeValueAsString(lstFileEcrypted));
						showMessage("Success", "Decrypt file success!");
						for (FileItem item : lstFiles) {
							if (lstFileEcrypted.contains(item.getPath())) {
								item.setEncrypted(true);
							} else {
								item.setEncrypted(false);
							}
							item.setChoose(false);
						}
						
						//Refresh list file displaying
						mAdapter = new FilesAdapter(mContext, lstFiles,
								selectedCount);

						lstView.setAdapter(mAdapter);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						showMessage("Error!", "Decrypt file failed!");

					}
				}
			}
		}
	}

	public void cutFile() {
		
		//Copy and delete each file in lstCut
		for (FileItem file : lstCut) {
			
			//create new file
			File newFile = new File(currentDir + "/" + file.getName());
			try {
				
				//Copy byte[] of file to new file
				FileOutputStream output = new FileOutputStream(newFile);
				byte[] data = Encrypt.read(file);
				output.write(data);
				output.close();
				
				//Delete file
				file.delete();
				
				//Refresh list file displaying
				setListFiles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	public void copyFile() {
		
		//Copy each file in lstCopy to current dir with same file name
		for (FileItem file : lstCopy) {
			
			//Create new file
			File newFile = new File(currentDir + "/" + file.getName());
			try {
				//Write byte[] of file in lstCopy to new file
				FileOutputStream output = new FileOutputStream(newFile);
				byte[] data = Encrypt.read(file);
				output.write(data);
				output.close();
				//Refresh list file displaying
				setListFiles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void btnDownloadClick(View view) {
		Intent downloadIntent = new Intent(MainActivity.this,
				DownloadActivity.class);

		MainActivity.this.startActivity(downloadIntent);
	}
}
