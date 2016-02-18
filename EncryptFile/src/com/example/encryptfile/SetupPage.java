package com.example.encryptfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SetupPage extends Activity {

	private EditText edtPassword;
	private EditText edtSecurityQuestion;
	private EditText edtSecurityAnswer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_page);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		edtSecurityQuestion = (EditText) findViewById(R.id.edtSecurityQuestions);
		edtSecurityAnswer = (EditText) findViewById(R.id.edtSecurityAnswer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Button submit click event to save setting
	public void btnSubmitClick(View v){
		
		//Check if blank password or question or answer
		if(edtPassword.getText().toString().equals("")){
			showMessage("You must type password!");
			return;
		}
		if(edtSecurityQuestion.getText().toString().equals("")){
			showMessage("You must type security question!");
			return;
		}
		if(edtSecurityAnswer.getText().toString().equals("")){
			showMessage("You must type security answer!");
			return;
		}

		//Get shared preferences
		SharedPreferences sharedPref = getSharedPreferences("security", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		//Encrypt password 128 bit and save this as string
		byte[] pass;
		try {
			pass = Encrypt.generateKey(edtPassword.getText().toString());
			editor.putString("password", new String(pass, "UTF8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.commit();
		
		//Save security question and answer to shared preferences
		editor.putString("question", edtSecurityQuestion.getText().toString());
		editor.commit();
		editor.putString("answer", edtSecurityAnswer.getText().toString());
		editor.commit();
		
		//
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	private void showMessage(String message){
		AlertDialog.Builder alert = new Builder(this);
		alert.setMessage(message);
		alert.show();
	}
}
