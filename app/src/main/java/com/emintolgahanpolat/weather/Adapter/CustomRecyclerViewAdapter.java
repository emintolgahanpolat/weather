package com.emintolgahanpolat.weather.Adapter;

import android.content.Context;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emintolgahanpolat.weather.Model.City;
import com.emintolgahanpolat.weather.Model.ConsolidatedWeather;
import com.emintolgahanpolat.weather.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_THIS_DAY = 1, TYPE_OTHER_DAY = 2, TYPE_THIS_DAY_DETAIL = 3, TYPE_CITY = 4, TYPE_LOADING = 5;

    private List<Object> objectList;
    private Context context;

    public CustomRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setObjectFeed(List<Object> list) {
        this.objectList = list;
    }


    @Override
    public int getItemViewType(int position) {
        if (objectList == null) {
            return TYPE_LOADING;
        } else if (objectList.get(position) instanceof City) {
            return TYPE_CITY;
        } else if (objectList.get(position) instanceof ConsolidatedWeather) {
            if (position == 0) {
                return TYPE_THIS_DAY;
            } else if (position == objectList.size()-1) {
                return TYPE_THIS_DAY_DETAIL;
            }
            return TYPE_OTHER_DAY;
        }
        return -1;

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case TYPE_THIS_DAY:
                ConsolidatedWeather consolidatedWeather = (ConsolidatedWeather) objectList.get(position);
                ((ThisDayViewHolder) holder).showThisDayDetails(consolidatedWeather);

                break;
            case TYPE_OTHER_DAY:
                ConsolidatedWeather consolidatedWeatherOther = (ConsolidatedWeather) objectList.get(position);
                ((OtherDayViewHolder) holder).showOtherDayDetails(consolidatedWeatherOther);

                break;
            case TYPE_THIS_DAY_DETAIL:
                ConsolidatedWeather consolidatedWeatherDetail = (ConsolidatedWeather) objectList.get(position);
                ((ThisDayDetailViewHolder) holder).showThisDayDetailDetails(consolidatedWeatherDetail);

                break;
            case TYPE_CITY:
                City city = (City) objectList.get(position);
                ((CityViewHolder) holder).showCityDetails(city);
                break;
            case TYPE_LOADING:
                showLoadingView((LoadingViewHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return objectList == null ? 1 : objectList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = 0;
        final RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case TYPE_THIS_DAY:
                layout = R.layout.weather_this_day_list_item;
                View thisDayView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new ThisDayViewHolder(thisDayView);

                break;
            case TYPE_OTHER_DAY:
                layout = R.layout.weather_list_item;
                View otherDayView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new OtherDayViewHolder(otherDayView);
                break;
            case TYPE_THIS_DAY_DETAIL:
                layout = R.layout.weather_this_day_detail_list_item;
                View thisDayDetailView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new ThisDayDetailViewHolder(thisDayDetailView);

                break;
            case TYPE_CITY:
                layout = R.layout.city_list_item;
                View cityView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new CityViewHolder(cityView);
                break;
            case TYPE_LOADING:
                layout = R.layout.loading_list_item;
                View loadingView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new LoadingViewHolder(loadingView);
                break;
            default:
                viewHolder = null;
                break;
        }


        return viewHolder;
    }

    public Object getItemAt(int position) {
        return objectList.get(position);
    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {


        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    public class ThisDayViewHolder extends RecyclerView.ViewHolder {

        public TextView txtState, txtMaxTemp;
        public ImageView img;

        public ThisDayViewHolder(View view) {
            super(view);

            txtState = (TextView) view.findViewById(R.id.txtState);
            txtMaxTemp = (TextView) view.findViewById(R.id.txtMaxTemp);
            img = (ImageView) view.findViewById(R.id.imageIcon);
        }

        public void showThisDayDetails(ConsolidatedWeather consolidatedWeather) {

            Resources res = context.getResources();
            int stringId = res.getIdentifier("weather_state_"+consolidatedWeather.getWeatherStateAbbr(), "string", context.getPackageName());
            String weatherState=context.getResources().getString(stringId);

            txtState.setText(weatherState);
            txtMaxTemp.setText(String.valueOf((int) Math.round(consolidatedWeather.getTheTemp())) + "°");



            int resID = res.getIdentifier(consolidatedWeather.getWeatherStateAbbr(), "drawable", context.getPackageName());
            Drawable drawable = res.getDrawable(resID);
            img.setImageDrawable(drawable);

        }
    }

    public class ThisDayDetailViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAbout, txtWindSpeed, txtWindDirection, txtAirPressure, txtHumidity, txtVisibility, txtPredictability;

        public ThisDayDetailViewHolder(View view) {
            super(view);

            txtAbout = (TextView) view.findViewById(R.id.txtAbout);
            txtWindSpeed = (TextView) view.findViewById(R.id.txtWindSpeed);
            txtWindDirection = (TextView) view.findViewById(R.id.txtWindDirection);
            txtAirPressure = (TextView) view.findViewById(R.id.txtAirPressure);
            txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
            txtVisibility = (TextView) view.findViewById(R.id.txtVisibility);
            txtPredictability = (TextView) view.findViewById(R.id.txtPredictability);

        }

        public void showThisDayDetailDetails(ConsolidatedWeather consolidatedWeather) {




            Resources res = context.getResources();
            int stringId = res.getIdentifier("weather_state_"+consolidatedWeather.getWeatherStateAbbr(), "string", context.getPackageName());
            String weatherState=context.getResources().getString(stringId);

            txtAbout.setText("Bugün: Şu anki hava durumu "+weatherState+". Sıcaklık "+ String.valueOf((int) Math.round(consolidatedWeather.getTheTemp()))+"°, bugünkü en yüksek tahmini "+String.valueOf((int) Math.round(consolidatedWeather.getMaxTemp()))+"°.");
            txtWindSpeed.setText(consolidatedWeather.getWindSpeed());
            txtWindDirection.setText(consolidatedWeather.getWindDirection());
            txtAirPressure.setText(consolidatedWeather.getAirPressure());
            txtHumidity.setText(consolidatedWeather.getHumidity());
            txtVisibility.setText(consolidatedWeather.getVisibility());
            txtPredictability.setText(consolidatedWeather.getPredictability());

        }
    }

    public class OtherDayViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDay, txtMaxTemp, txtMinTemp;
        public ImageView weatherIcon;

        public OtherDayViewHolder(View view) {
            super(view);

            txtDay = (TextView) view.findViewById(R.id.txtDay);
            txtMaxTemp = (TextView) view.findViewById(R.id.txtMaxTemp);
            txtMinTemp = (TextView) view.findViewById(R.id.txtMinTemp);
            weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);


        }

        public void showOtherDayDetails(ConsolidatedWeather consolidatedWeather) {


            txtDay.setText(dayName(consolidatedWeather.getApplicableDate()));

            txtMaxTemp.setText(String.valueOf((int) Math.round(consolidatedWeather.getMaxTemp())));
            txtMinTemp.setText(String.valueOf((int) Math.round(consolidatedWeather.getMinTemp())));

            Resources res = context.getResources();
            int resID = res.getIdentifier(consolidatedWeather.getWeatherStateAbbr(), "drawable", context.getPackageName());
            Drawable drawable = res.getDrawable(resID);
            weatherIcon.setImageDrawable(drawable);
        }
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCityName;

        public CityViewHolder(View view) {
            super(view);

            txtCityName = (TextView) view.findViewById(R.id.txtCityNameItem);


        }

        public void showCityDetails(City city) {

            txtCityName.setText(city.getTitle());

        }
    }


    public static String dayName(String inputDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("EEEE").format(date);
    }

}
