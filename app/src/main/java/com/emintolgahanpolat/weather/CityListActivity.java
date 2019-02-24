package com.emintolgahanpolat.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emintolgahanpolat.weather.Adapter.CustomRecyclerViewAdapter;
import com.emintolgahanpolat.weather.Adapter.RecyclerItemClickListener;
import com.emintolgahanpolat.weather.Model.StringHelper;
import com.emintolgahanpolat.weather.Model.City;
import com.emintolgahanpolat.weather.Utils.ConnectivityReceiver;
import com.emintolgahanpolat.weather.Utils.MyApplication;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    //ConnectivityReceiverListener broadcast ile sayfa açıkken internet kapatılırsa kontrolü yapıyoruz implement ettiğimiz metot internet kapalı ise önceki sayfaya geri dönüyor

    private FirebaseAnalytics mFirebaseAnalytics;

    private String BASE_URL = "https://www.metaweather.com/api/location/search/?lattlong=";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private List<Object> cityList = new ArrayList<>();
    private String lattlong = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);


        init();
        lattlong = getIntent().getExtras().getString("lattlong");
        getCity(lattlong);//API den konuma göre yakın şehirlerin listesini getir.


    }





    private void init() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        //listemeleme işlemleri
        recyclerView = (RecyclerView) findViewById(R.id.cityList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this);
        recyclerView.setAdapter(customRecyclerViewAdapter);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                City city = (City) customRecyclerViewAdapter.getItemAt(position);



                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, city.getTitle());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(CityListActivity.this, WeatherDetailActivity.class);
                intent.putExtra("whoid", String.valueOf(city.getWoeid()));
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }


    private void getCity(String lattlong) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + lattlong,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<City>>() {
                            }.getType();
                            cityList = gson.fromJson(StringHelper.convertFromUTF8(response), listType);

                            customRecyclerViewAdapter.setObjectFeed(cityList);
                            customRecyclerViewAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Response Error : ", e.toString());
                            Toast.makeText(getApplicationContext(), "Veri Alınamadı", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error 400", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}
