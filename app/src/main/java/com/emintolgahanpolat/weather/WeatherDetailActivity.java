package com.emintolgahanpolat.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emintolgahanpolat.weather.Adapter.CustomRecyclerViewAdapter;
import com.emintolgahanpolat.weather.Model.StringHelper;
import com.emintolgahanpolat.weather.Model.Weather;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class WeatherDetailActivity extends AppCompatActivity {


    private String BASE_URL = "https://www.metaweather.com/api/location/";



    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private List<Object> consolidatedList = new ArrayList<>();


    private TextView txtCityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);





        init();
        getWeather(getIntent().getExtras().getString("whoid"));//şehrin whoid ile bilgilerini API den getir.


    }

    private void init() {
        txtCityName=(TextView) findViewById(R.id.txtCityName);


        //listeleme işlemleri
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        customRecyclerViewAdapter =new CustomRecyclerViewAdapter(this);
        recyclerView.setAdapter(customRecyclerViewAdapter);
    }



    private void getWeather(String whoid) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + whoid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new Gson();

                            Weather weather  =  gson.fromJson(StringHelper.convertFromUTF8(response), Weather.class);

                            txtCityName.setText(weather.getTitle());

                            consolidatedList.addAll(weather.getConsolidatedWeather());//haftalık hava durumunu listeye ekle
                            consolidatedList.add(weather.getConsolidatedWeather().get(0));// ilk günü sona tekrar ekle (sayfa sonunda ekstra bilgileri tekrar göstermek için )

                            customRecyclerViewAdapter.setObjectFeed(consolidatedList);
                            customRecyclerViewAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Response Error : ",e.toString());
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
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        super.onBackPressed();
    }
}
