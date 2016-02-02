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
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GroupListActivity extends AppCompatActivity {

    public ArrayAdapter<String> groupsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Intent intent = getIntent();
        final String response = intent.getStringExtra("key");

        //Constructing the url for retrieving the groups

         Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("retrieve_groups.php")
                .appendQueryParameter("user_id", response)
                .build();


        //Populating the groups in which the user is not a member of

        ArrayList<String> dummy_data = new ArrayList<String>();
        groupsAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.enter_group_list_item, R.id.otherGroupItemTextView, dummy_data);
        ListView groupListView = (ListView) findViewById(R.id.groupListView);
        groupListView.setAdapter(groupsAdapter);

        //Adding a list item listener to enable the user to enter the existing group

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = groupsAdapter.getItem(position);
                String[] userIdAndGroupName = new String[2];
                userIdAndGroupName[0] = response;
                userIdAndGroupName[1] = groupName;
                Intent intent = new Intent(GroupListActivity.this,EnterGroupActivity.class);
                intent.putExtra("key",userIdAndGroupName);
                startActivity(intent);
            }
        });

        //TODO: Create an AsyncTask object
        GetGroupsTask task = new GetGroupsTask();
        task.execute(uri.toString());

    }

    public class GetGroupsTask extends AsyncTask<String,Void,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Loading Groups",Toast.LENGTH_LONG).show();
        }



        @Override
        protected String doInBackground(String... params) {

            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        /**
         * Parsing of JSON string
         * @param response
         * @return  groupNames - the arrayList of groupNames the user's not a member of
         * @throws JSONException
         */

        public ArrayList<String> getGroupNamesFromResponse(String response) throws JSONException{

            ArrayList<String> groupNames = new ArrayList<String>();
            JSONArray groups = new JSONArray(response);
            for(int index = 0; index < groups.length();index++){
                JSONObject groupObject = groups.getJSONObject(index);
                String groupName = groupObject.getString("name");
                groupNames.add(groupName);
            }
            //Log.v("getGroupNames", groupNames.get(0));
            return groupNames;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            //TODO: Parse the response string and Update the UI

            if(response != null){
            ArrayList<String> groupNames = new ArrayList<String>();
            Log.v("onPostExecute", response);
                try {
                    groupNames = getGroupNamesFromResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Populating the listView with groups
                groupsAdapter.addAll(groupNames);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
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
