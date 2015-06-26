package com.lovejoy777.showcase;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lovejoy777.showcase.adapters.CardViewAdapter;
import com.lovejoy777.showcase.adapters.RecyclerItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lovejoy777 on 24/06/15.
 */
public class Screen1NonPS extends AppCompatActivity {
    //private final TextView ThemeName;
    //private final TextView ThemeDeveloper;
    private RecyclerView mRecyclerView;
    ArrayList<Themes> themesList;
    private CardViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        themesList = new ArrayList<Themes>();
        new JSONAsyncTask().execute("https://raw.githubusercontent.com/LayersManager/layers_showcase_json/master/showcase.json");

        mRecyclerView = (RecyclerView)findViewById(R.id.cardList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CardViewAdapter(themesList, R.layout.adapter_card_layout, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(Screen1NonPS.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String title = themesList.get(position).gettitle();
                        String link = themesList.get(position).getlink();
                        String googleplus = themesList.get(position).getgoogleplus();
                        String promo = themesList.get(position).getpromo();
                        String developer = themesList.get(position).getauthor();
                        String screenshot_1 = themesList.get(position).getscreenshot_1();
                        String screenshot_2 = themesList.get(position).getscreenshot_2();
                        String screenshot_3 = themesList.get(position).getscreenshot_3();
                        String description = themesList.get(position).getdescription();


                        Intent Infoactivity = new Intent(Screen1NonPS.this, Details.class);

                        Infoactivity.putExtra("keytitle", title);
                        Infoactivity.putExtra("keylink", link);
                        Infoactivity.putExtra("keygoogleplus", googleplus);
                        Infoactivity.putExtra("keypromo", promo);
                        Infoactivity.putExtra("keyscreenshot_1", screenshot_1);
                        Infoactivity.putExtra("keyscreenshot_2", screenshot_2);
                        Infoactivity.putExtra("keyscreenshot_3", screenshot_3);
                        Infoactivity.putExtra("keydescription", description);
                        Infoactivity.putExtra("keydeveloper", developer);

                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(Infoactivity, bndlanimation);
                    }
                })
        );
    }



    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Screen1NonPS.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("NonPS");

                    Random rnd = new Random();
                    for (int i = jarray.length() - 1; i >= 0; i--)
                    {
                        int j = rnd.nextInt(i + 1);

                        // Simple swap
                        JSONObject object = jarray.getJSONObject(j);
                        jarray.put(j, jarray.get(i));
                        jarray.put(i, object);
                        Themes theme = new Themes();

                        theme.settitle(object.getString("title"));
                        theme.setauthor(object.getString("author"));
                        theme.setversion(object.getString("version"));
                        theme.setlink(object.getString("link"));
                        theme.setgoogleplus(object.getString("googleplus"));
                        theme.seticon(object.getString("icon"));
                        theme.setpromo(object.getString("promo"));
                        theme.setscreenshot_1(object.getString("screenshot_1"));
                        theme.setscreenshot_2(object.getString("screenshot_2"));
                        theme.setscreenshot_3(object.getString("screenshot_3"));
                        theme.setdescription(object.getString("description"));

                        themesList.add(theme);
                    }
                    return true;
                }

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            mAdapter.notifyDataSetChanged();
            //ca.notifyDataSetChanged();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            System.out.println(themesList.size());

        }
    }

}