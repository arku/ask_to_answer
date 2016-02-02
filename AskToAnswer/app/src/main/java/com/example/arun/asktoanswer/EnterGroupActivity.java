package com.example.arun.asktoanswer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EnterGroupActivity extends AppCompatActivity {

    public ArrayAdapter<String> questionsAdapter;
    public  String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_group);

        Intent intent = getIntent();
        String[] received = intent.getExtras().getStringArray("key");
        //TODO:
        String groupName = received[1];
        userId = received[0];

        //Logs
        Log.v("EnterGroup: userId",received[0]);
        Log.v("EnterGroup: groupName",received[1]);



        //Constructing url for adding a member to a group
        Uri add_uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("add.php")
                .appendQueryParameter("group",groupName)
                .appendQueryParameter("uid",userId)
                .build();

        //Creating an asynctask to add user to a group

        final String[] params = new String[3];
        params[0]= add_uri.toString();
        params[1]= groupName;
        Button enterGroupButton = (Button)findViewById(R.id.enterGroupButton);
        enterGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserToGroupTask anotherTask = new AddUserToGroupTask();
                anotherTask.execute(params[0]);
                Log.v("Enter Group",params[0]);
            }
        });


        TextView groupNameTextView = (TextView)findViewById(R.id.groupNameTextView);
        groupNameTextView.setText(groupName);

        //Constructs the URL and create a new task for retrieving questions
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("retrieve_question.php")
                .appendQueryParameter("group",groupName)
                .build();
        Log.v("DisplayGroup", uri.toString());

        //Populating the listview with questions of that group

        ArrayList<String> questionsList = new ArrayList<String>();
        ListView questionsListView = (ListView)findViewById(R.id.questionsListView);
        questionsAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.question_item_layout,
                R.id.questionTextView,questionsList);
        questionsListView.setAdapter(questionsAdapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Add an alert saying that they have to be a member to view questions of the group
                AlertDialog.Builder builder = new AlertDialog.Builder(EnterGroupActivity.this);

                builder.setMessage(R.string.user_alert);
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //pass
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


        //Create an async task to fetch the questions in a group
        ShowQuestionsTask task = new ShowQuestionsTask();
        task.execute(uri.toString());


    }
    public class AddUserToGroupTask extends AsyncTask<String,Void,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(EnterGroupActivity.this,"Adding..Wait",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

//Parsing of JSON

        public String getAddUserToGroupResponse(String response) throws JSONException {

            JSONObject responseObject = new JSONObject(response);
            return responseObject.getString("code");
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                Log.v("onPostExecute", response);

                try {
                    response = getAddUserToGroupResponse(response);
                } catch (JSONException e) {
                    Log.v("onPostExecute", "JSON Exception occurred");
                }

                //int userId = Integer.parseInt(response);
                //Log.v("onPostExecute", String.valueOf(userId));

                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
                    /*try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(EnterGroupActivity.this,HomeActivity.class);
                    intent.putExtra("key",userId);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Unable to add you.Check your Connection",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public class ShowQuestionsTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Fetching Questions", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public ArrayList<String> getQuestionsFromResponse(String response) throws JSONException {
            ArrayList<String> questionsList = new ArrayList<String>();
            JSONArray questionsArray = new JSONArray(response);
            for(int index = 0; index < questionsArray.length(); index++){
                JSONObject questionObject = questionsArray.getJSONObject(index);
                String questionText = questionObject.getString("text");
                questionsList.add(questionText);
            }
            return questionsList;
        }


        @Override
        protected void onPostExecute(String response) {
            ArrayList<String> questionsList = new ArrayList<String>();
            super.onPostExecute(response);
            if(response != null) {
                Log.v("onPostExecute",response);
                try {
                    questionsList = getQuestionsFromResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("onPostExecute","Populating the questionslist");
                //Populating the listView

                questionsAdapter.addAll(questionsList);


            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_group, menu);
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
