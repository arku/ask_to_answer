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




public class CreateGroupActivity extends AppCompatActivity {
    public String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }

    public void addNewGroup(View view){
        Intent receivedIntent = getIntent();
        userId = receivedIntent.getStringExtra("key");
        Log.v("CreateGroupActivity",userId);

        EditText groupNameEditText = (EditText)findViewById(R.id.groupNameEditText);
        String groupname = groupNameEditText.getText().toString();
        String url = getUrlForAddGroup(userId,groupname);

        AddGroupTask task = new AddGroupTask();
        task.execute(url);
    }


    public String getUrlForAddGroup(String userId,String groupname){
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("add_group.php")
                .appendQueryParameter("name",groupname)
                .appendQueryParameter("uid",userId)
                .build();
        Log.v("CreateGroupActivity",uri.toString());

        return uri.toString();
    }


    public class AddGroupTask extends AsyncTask<String , Void, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Creating group",Toast.LENGTH_SHORT).show();
        }



        @Override
        protected String doInBackground(String... params) {

            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public String getAddGroupResponse(String response) throws JSONException {

            JSONObject responseObject = new JSONObject(response);
            return responseObject.getString("code");

        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                Log.v("onPostExecute", response);

                try {
                    response = getAddGroupResponse(response);
                } catch (JSONException e) {
                    Log.v("onPostExecute", "JSON Exception occurred");
                }

                //int userId = Integer.parseInt(response);
                //Log.v("onPostExecute", String.valueOf(userId));

                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(),"Group created!",Toast.LENGTH_SHORT).show();
                    /*try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(CreateGroupActivity.this, HomeActivity.class);
                    intent.putExtra("key",userId);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(getApplicationContext(),"Group not created.Check your Connection",Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
