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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void loginUser(View view){
        EditText emailEditText = (EditText)findViewById(R.id.emailLoginEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordLoginEditText);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Log.v("AsyncTask",email + " " +password);


        emailEditText.setText("");
        passwordEditText.setText("");


        //Checks if the fields are empty
        if(!email.trim().isEmpty() && !password.trim().isEmpty()){

            //If the fields are filled,constructs the asynctask

            String url = getUrlForLogin(email,password);
            Log.v("AsyncTask",url);
            CheckLoginForUser checkLoginTask = new CheckLoginForUser();
            checkLoginTask.execute(url);
        }
        else{
            Toast.makeText(getApplication(), "Please fill the required fields", Toast.LENGTH_LONG).show();
        }

    }

    public String getUrlForLogin(String email, String password){

        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("login.php")
                .appendQueryParameter("email",email)
                .appendQueryParameter("password",password)
                .build();
        return uri.toString();
    }
    public void signUp(View v){
        startActivity(new Intent(MainActivity.this,SignupActivity.class));
    }

    public class CheckLoginForUser extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(),"Logging you in",Toast.LENGTH_SHORT).show();
        }



        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public String getLoginResponse(String response) throws JSONException {

            JSONObject responseObject = new JSONObject(response);
            int loginCode = responseObject.getInt("user_id");
            return "" + loginCode;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //HashMap<Integer,String> receivedResponse = null;
            if(response != null){
                Log.v("onPostExecute", response);

                try{
                    response = getLoginResponse(response);}
                catch(JSONException e){
                    Log.v("onPostExecute","JSON Exception occured");
                }
                Log.v("onPostExecute",response);
                int userId =Integer.parseInt(response);
                //String responseString = response.split(":")[1];
                Log.v("onPostExecute", String.valueOf(userId));

                //Email id exists
                if(userId >= 0){

                    //Password is right
                    if(userId > 0){
                        Log.v("onPostExecute","in if");
                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                        homeIntent.putExtra("key",response);
                        startActivity(homeIntent);
                    }

                    // Password is wrong
                    else{
                        Log.v("onPostExecute","in inner else");
                        Toast.makeText(getApplicationContext(),"Wrong password! Try again",Toast.LENGTH_SHORT).show();
                    }
                }

                //Email does not exist
                else{
                    Log.v("onPostExecute","in else");
                    Log.v("onPostExecute","account does not exist");
                    Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Log.v("onPostExecute","response null");
                Toast.makeText(getApplicationContext(), "Couldn't login.Check your connection!", Toast.LENGTH_LONG).show();
            }

        }


    } //End of AsyncTask class
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
