package com.android.faveo.frontend.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.faveo.Constants;
import com.android.faveo.R;
import com.android.faveo.backend.api.v1.Register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {

    TextView textViewSignIn, textViewErrorFullName, textViewErrorEmail, textViewErrorPassword, textViewErrorBirthday;
    EditText editTextFullName, editTextEmail, editTextPassword, editTextBirthday;
    Button buttonSignUp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setUpViews();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextFullName.getText().toString();
                String firstName = name.substring(0, name.indexOf(" "));
                String lastName = name.substring(name.indexOf(" ") + 1, name.length() - 1);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                //TODO error checks
                progressDialog.show();
                new SignUp(SignUpActivity.this, firstName, lastName, email, password, Constants.API_KEY).execute();
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public class SignUp extends AsyncTask<String, Void, String> {
        Context context;
        String firstName;
        String lastName;
        String email;
        String password;
        String apiKey;

        public SignUp(Context context, String firstName, String lastName, String email, String password, String apiKey) {
            this.context = context;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.apiKey = apiKey;
        }

        protected String doInBackground(String... urls) {
            return new Register().postRegisterUser(email, password, 1, "", firstName, lastName, "", "");
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if(result == null) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            if(result.contains("The email has already been taken.")) {
                Toast.makeText(getApplicationContext(), "Email already taken", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String ID = jsonObject.getString("id");
                String firstName = jsonObject.getString("first_name");
                String lastName = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                SharedPreferences.Editor authenticationEditor = getApplicationContext().getSharedPreferences(Constants.PREFERENCE, MODE_PRIVATE).edit();
                authenticationEditor.putString("ID", ID);
                authenticationEditor.putString("FIRST_NAME", firstName);
                authenticationEditor.putString("LAST_NAME", lastName);
                authenticationEditor.putString("EMAIL", email);
                authenticationEditor.apply();
                Toast.makeText(getApplicationContext(), "Sign Up successful", Toast.LENGTH_SHORT).show();
                finish();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    private void setUpViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up");
        textViewSignIn = (TextView) findViewById(R.id.textView_sign_in);
        textViewErrorFullName = (TextView) findViewById(R.id.textView_error_full_name);
        textViewErrorEmail = (TextView) findViewById(R.id.textView_error_email);
        textViewErrorPassword = (TextView) findViewById(R.id.textView_error_password);
        textViewErrorBirthday = (TextView) findViewById(R.id.textView_error_birthday);
        editTextFullName = (EditText) findViewById(R.id.editText_full_name);
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextBirthday = (EditText) findViewById(R.id.editText_birthday);
        buttonSignUp = (Button) findViewById(R.id.button_sign_up);
    }

}
