package com.letsmeet.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.letsmeet.com.letsmeet.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.letsmeet.android.apiclient.RegistrationServiceClient;
import com.letsmeet.android.common.PhoneNumberHelper;
import com.letsmeet.android.gcm.GcmRegistrationHandler;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.server.registration.model.RegistrationRequest;
import com.letsmeet.server.registration.model.RegistrationResponse;

import java.io.IOException;


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
        PhoneNumberHelper phoneNumberHelper = new PhoneNumberHelper(RegisterActivity.this);
        String phone = phoneNumberHelper.formatPhoneNumber(phoneEditText.getText().toString());
        registerUser(name, phone);
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

  private boolean isInputValid(String name, String phone) {
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

  private void registerUser(String name, String phone) {
    final ProgressDialog pd = new ProgressDialog(this);

    final RegistrationRequest registrationRequest = new RegistrationRequest()
        .setName(name)
        .setPhoneNumber(phone);

    new AsyncTask<RegistrationRequest, Void, RegistrationResponse>() {

      @Override
      protected void onPreExecute() {
        pd.show();
      }

      @Override protected RegistrationResponse doInBackground(RegistrationRequest... params) {
        try {
          String registrationId = GcmRegistrationHandler.getRegistrationId(RegisterActivity.this);
          registrationRequest.setRegId(registrationId);
          // TODO(suhas): Verify phone number by sms before registration.
          RegistrationResponse response = RegistrationServiceClient.getInstance()
              .registerUser(registrationRequest);
          LocalStore localStore = LocalStore.getInstance(RegisterActivity.this);
          localStore.saveUserId(response.getUserId());
          return response;
        } catch (IOException e) {
          // TODO(suhas): Handle gracefully.
          throw new RuntimeException(e);
        }
      }

      @Override
      protected void onPostExecute(RegistrationResponse response) {
        pd.hide();
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      }
    }.execute(registrationRequest);
  }
}
