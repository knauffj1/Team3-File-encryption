package com.example.encryptfile;

import java.io.UnsupportedEncodingException;

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
import android.widget.TextView;

public class ChangePassword extends Activity {
	private String oldPass;
	private String securityQuestion;
	private String securityAnswer;
	private EditText edtOldPass;
	private EditText edtNewPass;
	private EditText edtConfirmNewPass;
	private TextView securityQuestionView;
	private EditText edtSecurityAnswer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		//Get EditText old password
		edtOldPass = (EditText) findViewById(R.id.edtOldPass);
		
		//Get EditText new password
		edtNewPass = (EditText) findViewById(R.id.edtNewPass);
		
		//Get EditText confirm password
		edtConfirmNewPass = (EditText) findViewById(R.id.edtConfirmNewPass);
		
		//Get EditText security question
		securityQuestionView = (TextView) findViewById(R.id.securityQuestionView);
		
		//Get EditText security answer
		edtSecurityAnswer = (EditText) findViewById(R.id.edtSecurityAnswerChange);
		
		//Get sharedprefences
		SharedPreferences sharedPref = getSharedPreferences("security",
				Context.MODE_PRIVATE);
		
		String trueQuestion =  sharedPref.getString("question", null);
		
		securityQuestionView.setText(trueQuestion);
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_password, menu);
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
	public void btnSubmitClick(View v){
		//Get sharedprefences
		SharedPreferences sharedPref = getSharedPreferences("security",
				Context.MODE_PRIVATE);
		
		//Get password, security question, security answer user setup firstrun
		String truePass = sharedPref.getString("password", null);
		String trueAnswer =  sharedPref.getString("answer", null);
		
		//Get string user input
		String oldPass = edtOldPass.getText().toString();
		String newPass = edtNewPass.getText().toString();
		String confirmNewPass = edtConfirmNewPass.getText().toString();
		String answer = edtSecurityAnswer.getText().toString();
		
		if(newPass.equals("")||confirmNewPass.equals("")){
			showMessage("Error!", "You must type new password and confirm!");
			return;
		}
		if(oldPass.equals("")&&answer.equals("")){
			showMessage("Error!", "You must type old pass or question and answer!");
			return;
		}
		if(!newPass.equals(confirmNewPass)){
			showMessage("Error!", "Password does not match!");
		}
		String input = "";
		try {
			//encrypte password user input
			input = new String(Encrypt.generateKey(oldPass), "UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//check password or question and answer user type
		if (truePass.equals(input)||trueAnswer.equalsIgnoreCase(answer)) {
			SharedPreferences.Editor editor = sharedPref.edit();
			byte[] pass;
			try {
				
				//Save new pass
				pass = Encrypt.generateKey(newPass);
				editor.putString("password", new String(pass, "UTF8"));
				editor.commit();
				showMessage("Success!", "Change password success!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			showMessage("Error", "Password or question or answer incorrect!");
		}
	}
	public void showMessage(String title, String message){
		AlertDialog.Builder alert = new Builder(this);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
	}
}
