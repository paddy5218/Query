package com.example.user.signup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryActivity extends AppCompatActivity {

    private ListView listView;
    final ArrayList<String> msg_id_Array = new ArrayList<String>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

       // String[] func = {"星期一","星期二","星期三"};

        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                ListView listView = (ListView) arg0;
                String msg_id = msg_id_Array.get(arg2);

                new TheDeleteTask().execute(msg_id);
               /*Toast.makeText(QueryActivity.this,  "ID：" + arg3 + "   選單文字："+ listView.getItemAtPosition(arg2).toString(),Toast.LENGTH_SHORT).show();*/
            }
        });

        //ArrayAdapter adapter = new ArrayAdapter(QueryActivity.this, android.R.layout.simple_list_item_1, func);

        //listView.setAdapter(adapter);

        new TheQueryTask().execute("123456789");

    }

    public void showListView()
    {

    }

    class TheQueryTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {

            String phoneNumber = arg0[0];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {

                data = "?phone=" + URLEncoder.encode(phoneNumber, "UTF-8");

                link = "http://140.130.33.153/QueryMessage.php" + data;

                URL url = new URL(link);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                result = bufferedReader.readLine();

                return result;

            } catch (Exception e) {

                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result)
        {
            String jsonStr = result;

            ArrayList<String> stringArray = new ArrayList<String>();

            Log.d("result=",result);

            if (jsonStr != null) try {


                JSONArray new_array = new JSONArray(result);
                List<HashMap<String , String>> list = new ArrayList<>();

                for (int i = 0, count = new_array.length(); i < count; i++) {
                    try {
                                            HashMap<String , String> hashMap = new HashMap<>();
                        JSONObject jsonObject = new_array.getJSONObject(i);

                        String msg = jsonObject.getString("msg").toString();
                        String created_date = jsonObject.getString("created_date").toString();
                        String msg_id = jsonObject.getString("msg_id").toString();
                        hashMap.put("msg" , msg);
                        hashMap.put("created_date" , created_date);

                        stringArray.add(msg);
                        list.add(hashMap);

                        msg_id_Array.add(msg_id);
                        Log.d("result=", msg);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

                //ArrayAdapter adapter = new ArrayAdapter(QueryActivity.this, android.R.layout.simple_list_item_1, stringArray);

               // listView.setAdapter(adapter);
                ListAdapter listAdapter = new SimpleAdapter(
                        QueryActivity.this,
                        list,
                        android.R.layout.simple_list_item_2 ,
                        new String[]{"msg" , "created_date"} ,
                        new int[]{android.R.id.text1 , android.R.id.text2});
                        listView.setAdapter(listAdapter);

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(QueryActivity.this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();

            }
            else {

                Toast.makeText(QueryActivity.this, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();

            }
        }
    }



    //Delete程式碼
    class TheDeleteTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            listView.setAdapter(null);
        }

        @Override
        protected String doInBackground(String... arg0) {

            String msg_id = arg0[0];

            String link;
            String data;
            BufferedReader bufferedReader;
            String result;

            try {

                data = "?msg_id=" + URLEncoder.encode(msg_id, "UTF-8");

                link = "http://140.130.33.153/DeleteMessage.php" + data;

                URL url = new URL(link);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                result = bufferedReader.readLine();

                return result;

            } catch (Exception e) {

                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            String jsonStr = result;

            Log.d("result=",result);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String query_result = jsonObj.getString("query_result");
                    if (query_result.equals("SUCCESS")) {

                        Toast.makeText(QueryActivity.this, "紀錄刪除成功" , Toast.LENGTH_SHORT).show();
                        new TheQueryTask().execute("123456789");

                    } else if (query_result.equals("FAILURE")) {

                        Toast.makeText(QueryActivity.this, "紀錄刪除失敗", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(QueryActivity.this, "無法連接伺服器", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    Toast.makeText(QueryActivity.this, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();

                }

            } else {

                Toast.makeText(QueryActivity.this, "Couldn't get any JSON data.", Toast.LENGTH_SHORT).show();

            }

        }
    }
}
