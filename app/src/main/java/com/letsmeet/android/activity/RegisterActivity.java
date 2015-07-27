package com.letsmeet.android.activity;

import android.letsmeet.com.letsmeet.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letsmeet.android.gcm.GcmRegistrationAsyncTask;


public class RegisterActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    final Button button = (Button) findViewById(R.id.register_button);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        EditText nameEditText = (EditText) findViewById(R.id.registration_name);
        EditText phoneEditText = (EditText) findViewById(R.id.registration_phone);
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        if (isInputValid(name, phone)) {
          new GcmRegistrationAsyncTask(v.getContext(), name, phone).execute();
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_home, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public boolean isInputValid(String name, String phone) {
    if (name.equals("")) {
      Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show();
      return false;
    }
    if (phone.equals("")) {
      Toast.makeText(this, "Please enter phone", Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }
}
