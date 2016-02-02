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


public class DisplayGroupActivity extends AppCompatActivity {

    public ArrayAdapter<String> questionsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group);

        Intent intent = getIntent();
        final String[] receivedItems = intent.getExtras().getStringArray("key");
        final String groupName = receivedItems[1];
        final String response = receivedItems[0];

        Log.v("DisplayGroup:groupName",groupName);
        Log.v("DisplayGroup:userId",response);

        TextView groupNameTextView = (TextView)findViewById(R.id.groupNameTextView);
        groupNameTextView.setText(groupName);

        //Constructs the URL and create a new task for retrieving questions
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                 .appendPath("retrieve_question.php")
                .appendQueryParameter("group",groupName)
                .build();
        Log.v("DisplayGroup",uri.toString());

        //Populating the questions
        ArrayList<String> questionsList = new ArrayList<String>();
        ListView questionsListView = (ListView)findViewById(R.id.questionsListView);
        questionsAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.question_item_layout,
                R.id.questionTextView,questionsList);
        questionsListView.setAdapter(questionsAdapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String questionName = questionsAdapter.getItem(position);
                String[] sendItems = new String[3];
                sendItems[0] = response;
                sendItems[1] = questionName;
                sendItems[2] = groupName;
                Intent intent = new Intent(getApplicationContext(), DisplayQuestionActivity.class);
                intent.putExtra("key", sendItems);
                startActivity(intent);
            }
        });

        Button addQuestionButton = (Button)findViewById(R.id.addQuestionButton);
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisplayGroupActivity.this,CreateQuestionActivity.class);
                intent1.putExtra("key",receivedItems);
                startActivity(intent1);

            }
        });


        //Create an async task to fetch the questions in a group
        ShowQuestionsTask task = new ShowQuestionsTask();
        task.execute(uri.toString());


    }

    public class ShowQuestionsTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Fetching Questions",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public ArrayList<String> getQuestionsFromResponse(String response) throws JSONException{
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
        getMenuInflater().inflate(R.menu.menu_display_group, menu);
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
