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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateQuestionActivity extends AppCompatActivity {
    public String[] received = new String[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        Intent intent = getIntent();
        received = intent.getExtras().getStringArray("key");
        final String userId = received[0];
        final String groupName = received[1];

        final EditText questionEditText = (EditText)findViewById(R.id.questionEditText);


        //Adding an event listener to the button to add question
        Button askQuestionButton = (Button)findViewById(R.id.submitQuestionButton);
        askQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questionText = questionEditText.getText().toString();
                //Constructs the url to add question to the group
                if(!questionText.isEmpty()) {
                    String uri = getUriForAddingQuestion(questionText, userId, groupName);
                    questionEditText.setText("");
                    Toast.makeText(getApplicationContext(), "Adding your question..", Toast.LENGTH_SHORT).show();

                    //Creates a new task for adding question to the group
                    AddQuestionToGroup task = new AddQuestionToGroup();
                    task.execute(uri);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Question can't be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public String getUriForAddingQuestion(String questionText, String userId, String groupName){
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("add_question.php")
                .appendQueryParameter("text",questionText)
                .appendQueryParameter("group",groupName)
                .appendQueryParameter("uid",userId)
                .build();
        return  uri.toString();
    }
    public class AddQuestionToGroup extends AsyncTask<String,Void,String>{



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(CreateQuestionActivity.this,"Adding your question",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Constants constant =new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public String getAddQuestionToGroupResponse(String response)throws JSONException{


                JSONObject responseObject = new JSONObject(response);
                return responseObject.getString("code");
            }

        @Override
        protected void onPostExecute(String response) {

            super.onPostExecute(response);
            if (response != null) {
                Log.v("onPostExecute", response);

                try {
                    response = getAddQuestionToGroupResponse(response);
                } catch (JSONException e) {
                    Log.v("onPostExecute", "JSON Exception occurred");
                }

                //int userId = Integer.parseInt(response);
                //Log.v("onPostExecute", String.valueOf(userId));

                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(),"Question added",Toast.LENGTH_SHORT).show();
                    /*try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                    Intent intent = new Intent(CreateQuestionActivity.this, DisplayGroupActivity.class);
                    intent.putExtra("key",received);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Unable to add your question.Check your Connection",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_question, menu);
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
