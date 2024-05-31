package com.androiod.skycast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    final String url = "https://api.openweathermap.org/data/2.5/weather";
    final String api = "46def0756dc17dcccb6772426f173d47";
    DecimalFormat df = new DecimalFormat("#.##");    // for showing temp up-to 2 decimal places

    SearchView searchView;
    TextView weather,condition,temp,max,min,day,date,cityTv,humidity,sunrise,sunset,wind,sea;
    LottieAnimationView lottieAnimationView;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
        

        searchView = findViewById(R.id.searchView);
        condition = findViewById(R.id.condition);
        weather = findViewById(R.id.weather);
        temp = findViewById(R.id.temp);
        max = findViewById(R.id.max_temp);
        min = findViewById(R.id.min_temp);
        date = findViewById(R.id.date);
        day = findViewById(R.id.day);
        cityTv = findViewById(R.id.city);
        humidity = findViewById(R.id.humidity);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.wind);
        sea = findViewById(R.id.sea);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        layout = findViewById(R.id.mainLayout);


//        https://api.openweathermap.org/data/2.5/weather?q=dhariwal&appid=46def0756dc17dcccb6772426f173d47

//        getWeather("dhariwal");



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getWeather(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getWeather(String city) {
        String finalUrl = url +"?q="+ city +"&appid="+ api;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("response",response);

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArrayWeather = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObject = jsonArrayWeather.getJSONObject(0);
                    String weatherVal = jsonObject.getString("description");
                    String conditionVal = jsonObject.getString("main");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double tempVal = jsonObjectMain.getDouble("temp")-273.15;
                    double maxVal = jsonObjectMain.getDouble("temp_max")-273.15;
                    double minVal = jsonObjectMain.getDouble("temp_min")-273.15;
                    int humidityVal = jsonObjectMain.getInt("humidity");
                    int pressureVal = jsonObjectMain.getInt("pressure");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String windVal = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String country = jsonObjectSys.getString("country");
                    String sunriseVal = jsonObjectSys.getString("sunrise");
                    String sunsetVal = jsonObjectSys.getString("sunset");
                    String cityVal = jsonResponse.getString("name");

                    weather.setText(String.valueOf(weatherVal));
                    condition.setText(String.valueOf(conditionVal));
                    temp.setText(String.valueOf(df.format(tempVal)) );
                    max.setText(String.valueOf("Max :"+df.format(maxVal)+"°C"));
                    min.setText(String.valueOf("Min :"+df.format(minVal)+"°C"));
                    humidity.setText(String.valueOf(humidityVal)+"%");
                    sea.setText(String.valueOf(pressureVal+"hpa"));
                    wind.setText(String.valueOf(windVal)+" m/s");
                    cityTv.setText(String.valueOf(cityVal));
                    date.setText(getDate());
                    day.setText(getDay());
                    sunrise.setText(getTime(Long.parseLong(sunriseVal)));
                    sunset.setText(getTime(Long.parseLong(sunsetVal)));

                    changeImage(conditionVal);



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
               


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        return date;
    }

    private String getDay() {
        Calendar calendar = Calendar.getInstance();
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault());
//        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
//        return date;
        return day;
       
    }
    private String getTime(Long timestamp) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        return simpleDateFormat.format(timestamp);
    }
    private void changeImage(String weather){
        if(weather.equals("Clear Sky")|| weather.equals("Clear") || weather.equals("Sunny")){
            layout.setBackgroundResource(R.drawable.sunny_background);
            lottieAnimationView.setAnimation(R.raw.sun);
        }
        if(weather.equals("Partly Clouds") || weather.equals("Clouds") || weather.equals("Overcast")|| weather.equals("Mist") || weather.equals("Foggy")){
            layout.setBackgroundResource(R.drawable.colud_background);
            lottieAnimationView.setAnimation(R.raw.cloud);

        }
        if(weather.equals("Light Rain") || weather.equals("Drizzle") || weather.equals("Heavy Rain") || weather.equals("Moderate Rain") || weather.equals("Showers")|| weather.equals("Rain")){
            layout.setBackgroundResource(R.drawable.rain_background);
            lottieAnimationView.setAnimation(R.raw.rain);

        }
        if(weather.equals("Light Snow") || weather.equals("Moderate Snow") || weather.equals("Heavy Snow") || weather.equals("Blizzard")|| weather.equals("Snow")){
            layout.setBackgroundResource(R.drawable.snow_background);
            lottieAnimationView.setAnimation(R.raw.snow);
        }
        lottieAnimationView.playAnimation();
    }

}