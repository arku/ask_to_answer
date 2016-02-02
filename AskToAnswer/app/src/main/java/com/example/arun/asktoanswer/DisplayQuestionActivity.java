package com.example.arun.asktoanswer;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DisplayQuestionActivity extends AppCompatActivity {

    public ArrayAdapter<String> answersAdapter;
    public ArrayList<String> answers;
    public String questionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_question);

        //Receiving the intent and setting the question text

        Intent intent = getIntent();
        String[] receivedStrings = intent.getExtras().getStringArray("key");

        final String userId = receivedStrings[0];
        questionName = receivedStrings[1];
        final String groupName = receivedStrings[2];

        Log.v("DisplayQuestion",userId + questionName);

        TextView questionItemTextView = (TextView)findViewById(R.id.questionItemTextView);
        questionItemTextView.setText(questionName);

        //Populating the listview initially with a empty arrayList

        final ArrayList<String> answers = new ArrayList<>();
        ListView answersListView = (ListView)findViewById(R.id.answersListView);
        answersAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.answer_item_layout,R.id.answerItemTextView,answers);
        answersListView.setAdapter(answersAdapter);


        //Constructs the url for retrieving the answers


        //Setting the onItemClickListener to the listview
        answersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent()
                //TODO: Have to get the answer and send it to the displayAnswerActivity
                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayQuestionActivity.this);
                builder.setTitle("Full Answer");
                builder.setMessage(answers.get(position));

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        //Adding answer to the listView
        final EditText answerEditText = (EditText)findViewById(R.id.answerEditText);

        ImageButton addAnswerButton = (ImageButton)findViewById(R.id.addAnswerButton);
        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = answerEditText.getText().toString();
                Toast.makeText(DisplayQuestionActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                if(!answer.isEmpty()){
                    AddAnswerTask task1 = new AddAnswerTask();
                    answerEditText.setText("");
                    task1.execute(getUriForAddingAnswer(answer,questionName,userId,groupName));
                }
            }
        });


        // Create an asynctask for grabbing the answers for a particular question
        GetAnswersTask task = new GetAnswersTask();
        task.execute(getUriForAnswers(questionName));

    }

    public String getUriForAnswers(String questionName){
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("retrieve_answer.php")
                .appendQueryParameter("question",questionName)
                .build();
        return  uri.toString();
    }

    public class AddAnswerTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(DisplayQuestionActivity.this,"Adding your answer",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        public String getAddAnswerResponse(String response) throws JSONException{
            JSONObject responseObject = new JSONObject(response);
            return responseObject.getString("code");
        }



        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                Log.v("onPostExecute", response);

                try {
                    response = getAddAnswerResponse(response);
                } catch (JSONException e) {
                    Log.v("onPostExecute", "JSON Exception occurred");
                }

                //int userId = Integer.parseInt(response);
                //Log.v("onPostExecute", String.valueOf(userId));

                if(response.equals("1")){
                    Toast.makeText(getApplicationContext(),"Answer added",Toast.LENGTH_SHORT).show();
                    //TODO: Have to modify the code so as to save time  (use adapter.add())
                    //answersAdapter.add();
                    //answersAdapter.notifyDataSetChanged();
                    GetAnswersTask task = new GetAnswersTask();
                    task.execute(getUriForAnswers(questionName));
                }
                else{
                    Toast.makeText(getApplicationContext(),"Unable to add your answer.Check your Connection",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //Constructs the url for adding answer to a question
    public String getUriForAddingAnswer(String answer,String question, String userId, String groupName){
        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("add_answer.php")
                .appendQueryParameter("text", answer)
                .appendQueryParameter("group", groupName)
                .appendQueryParameter("question", question)
                .appendQueryParameter("uid",userId)
                .build();
        Log.v("DisplayGroup",uri.toString());
        return uri.toString();

    }


    public class GetAnswersTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Fetching answers",Toast.LENGTH_SHORT).show();
        }



        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }
        public ArrayList<String> getAnswersFromResponse(String response) throws JSONException {
            ArrayList<String> answersList = new ArrayList<String>();
            JSONArray answersArray = new JSONArray(response);
            for(int index = 0; index < answersArray.length(); index++){
                JSONObject questionObject = answersArray.getJSONObject(index);
                String answerText = questionObject.getString("text");
                // TODO: Have to shrink the answerText to a limit otherwise each list item may look very big
                answersList.add(answerText);
            }
            return answersList;
        }

        public ArrayList<String> truncateAnswers(ArrayList<String> answers){
            ArrayList<String> truncatedAnswers = new ArrayList<>();
            int maxLength = 197;
            String truncatedAnswer;
            for(int index = 0; index < answers.size(); index ++)
            {
                //Selecting the substring only if the answer's length is greater than the maxLength
                if (answers.get(index).length() >= maxLength)
                    truncatedAnswer = answers.get(index).substring(0, maxLength) + "...";
                else
                    truncatedAnswer = answers.get(index);

                truncatedAnswers.add(truncatedAnswer);

            }
            return  truncatedAnswers;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            answers = new ArrayList<String>();
            ArrayList<String> truncatedAnswers = new ArrayList<>();
            try {
                answers = getAnswersFromResponse(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            truncatedAnswers = truncateAnswers(answers);
            answersAdapter.addAll(truncatedAnswers);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_question, menu);
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
