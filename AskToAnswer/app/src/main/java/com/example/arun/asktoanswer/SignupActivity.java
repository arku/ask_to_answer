package com.example.arun.asktoanswer;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
       // Button signUpButton = (Button) findViewById(R.id.signUpButton);
    }

    public void addUser(View v){
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String username = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();



            Uri uri = Uri.parse(Constants.URL)
                    .buildUpon()
                    .appendPath("arun")
                    .appendPath("add_user.php")
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("name", username)
                    .appendQueryParameter("password", password).build();
            String url = uri.toString();

        Log.v("AddUserTask", url);
        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            AddUserTask newTask = new AddUserTask();
            newTask.execute(url);
        }
        else{
            Toast.makeText(getApplicationContext(),"Please fill all the required fields",Toast.LENGTH_SHORT).show();
        }
    }
   /** public boolean isOnline(){
        ConnectivityManager connectivityManager= (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

    } */
    //ProgressDialog progressDialog;
    public class AddUserTask extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(),"Signing you up!",Toast.LENGTH_LONG).show();

            Toast.makeText(getApplicationContext(),"Creating account",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public String getSignupResponse(String response) throws JSONException{

                    JSONObject jsonObject = new JSONObject(response);
                    return jsonObject.getString("reply");


        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response != null){
            Log.v("onPostExecute", response);

            try{
            response = getSignupResponse(response);}
            catch(JSONException e){
                Log.v("onPostExecute","JSON Exception occured");
            }
                if (response.equals("yes")){
                        //Toast.makeText(getApplicationContext(), "Account created successfully.Login now", Toast.LENGTH_SHORT).show();
                        Log.v("onPostExecute","account created!");
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    }
                else{
                    Log.v("onPostExecute","account not created");
                        Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_LONG).show();
                    }
            }
            else{
                Log.v("onPostExecute","response null");
                Toast.makeText(getApplicationContext(), "Account not created.Check your connection!", Toast.LENGTH_LONG).show();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
}
