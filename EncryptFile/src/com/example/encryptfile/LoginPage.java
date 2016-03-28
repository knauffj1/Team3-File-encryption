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

public class LoginPage extends Activity {
	private EditText edtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_page);
		edtPassword = (EditText) findViewById(R.id.edtPasswordLogin);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_page, menu);
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

	public void btnLoginClick(View v) {
		if (edtPassword.getText().toString().equals("")) {
			showMessage("Error", "You must type password!");
			return;
		}
		//Get shared preferences
		SharedPreferences sharedPref = getSharedPreferences("security",
				Context.MODE_PRIVATE);
		
		//Get password user set
		String password = sharedPref.getString("password", null);
		String input = "";
		try {
			//Encript password 128 bit and convert this to string
			input = new String(Encrypt.generateKey(edtPassword.getText()
					.toString()), "UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Check pass user input
		System.out.println(password);
		System.out.println(input);
		if (password.equals(input)) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else {
			showMessage("Error", "Password incorrect!");
		}
	}

	private void showMessage(String title, String message) {
		AlertDialog.Builder alert = new Builder(this);
		alert.setMessage(message);
		alert.setTitle(title);
		alert.show();
	}
	
	//Button change click event
	public void btnChangePassClick(View v){
		Intent intent = new Intent(this, ChangePassword.class);
		startActivity(intent);
	}
}
