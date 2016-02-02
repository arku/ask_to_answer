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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    public ArrayAdapter<String> userGroupsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Intent receivedIntent = getIntent();

        final String response = receivedIntent.getStringExtra("key");
        Log.v("HomeAcitivity",response);


        Button createGroup =(Button)findViewById(R.id.createGroup);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CreateGroupActivity.class);
                intent.putExtra("key",response);
                startActivity(intent);
            }
        });

        Button retrieveGroups = (Button)findViewById(R.id.retrieveGroups);
        retrieveGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HomeActivity.this,GroupListActivity.class);
                intent1.putExtra("key",response);
                startActivity(intent1);
            }
        });



        //Constructing the url

        Uri uri = Uri.parse(Constants.URL)
                .buildUpon()
                .appendPath("arun")
                .appendPath("retrieve_user_groups.php")
                .appendQueryParameter("user_id", response)
                .build();
        Log.v("HomeActivity",uri.toString());

        Log.v("HomeActivity", response);

        FindUserGroupsTask task = new FindUserGroupsTask();
        task.execute(uri.toString(), response);

        //Adding the list item listener to open the group activity
        //String[] dummy = {"PHP Topics","Linux Questions","iOS Development","Math Puzzles"};
        ArrayList<String> dummy_data = new ArrayList<>();

        userGroupsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.group_list_item, R.id.groupItemTextView,dummy_data);
        ListView groupListView = (ListView)findViewById(R.id.groupListView);
        groupListView.setAdapter(userGroupsAdapter);


        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = userGroupsAdapter.getItem(position);
                Log.v("onItemClickListener", groupName);
                //Toast.makeText(getApplicationContext(),groupName,Toast.LENGTH_SHORT).show();
                Intent groupIntent = new Intent(HomeActivity.this,DisplayGroupActivity.class);

                String[] itemsToSend = new String[2];
                itemsToSend[0] = response;
                itemsToSend[1] = groupName;
                Log.v("ItemsToSend",itemsToSend[0] + itemsToSend[1]);
                groupIntent.putExtra("key",itemsToSend);
                startActivity(groupIntent);

                Log.v("HomeActivity","Intent called.In DisplayGroupActivity");
            }
        });

    }


    public class FindUserGroupsTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Wait..Fetching your groups",Toast.LENGTH_SHORT).show();
        }



        @Override
        protected String doInBackground(String... params) {
            Constants constant = new Constants();
            return constant.getResponseFromServer(params[0]);
        }

        /**
         * Parsing of JSON string
         * @param response
         * @return  groupNames - the arrayList of groupNames the user's a member of
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
//            Log.v("getGroupNames", groupNames.get(0));
            return groupNames;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            ArrayList<String> groupNames = new ArrayList<String>();
            Log.v("onPostExecute",response);
            if(response != null) {
                try {
                    groupNames = getGroupNamesFromResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Populating the listView with user groups
                userGroupsAdapter.addAll(groupNames);
            }
        }
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
}
