package id.ac.polinema.retrofit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.ac.polinema.retrofit.R;
import id.ac.polinema.retrofit.api.helper.ServiceGenerator;
import id.ac.polinema.retrofit.api.models.AppVersion;
import id.ac.polinema.retrofit.api.models.LoginRequest;
import id.ac.polinema.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static final String NAME_KEY = "name";
    public static final String VERSION_KEY = "version";
    private static SharedPreferences pref;
    TextView lblAppName, lblAppTittle, lblAppVersion;
    private LoginRequest loginRequest;

    public static String getAppName(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(NAME_KEY, "name");
    }

    public static void setAppName(Context context, String appName) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(NAME_KEY, appName).apply();
    }

    public static String getAppVersion(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(VERSION_KEY, "0");
    }

    public static void setAppVersion(Context context, String appVer) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(VERSION_KEY, appVer).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupLayout();
        if (checkInternetConnection()) {
            checkAppVersion();
        } else {
            Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        setAppInfo();
    }

    private void setupLayout() {
        lblAppName = findViewById(R.id.lblAppName);
        lblAppTittle = findViewById(R.id.lblAppTittle);
        lblAppVersion = findViewById(R.id.lblAppVersion);
        lblAppVersion.setVisibility(View.INVISIBLE);
        lblAppName.setVisibility(View.INVISIBLE);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setAppInfo() {
        String appName = getAppName(SplashActivity.this);
        String appVersion = getAppVersion(SplashActivity.this);
        lblAppName.setText(appName);
        lblAppVersion.setText(appVersion);
        lblAppName.setVisibility(View.VISIBLE);
        lblAppVersion.setVisibility(View.VISIBLE);
    }

    private void checkAppVersion() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<AppVersion> call = service.getAppVersion();
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                Toast.makeText(SplashActivity.this, response.body().getApp(), Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        setAppName(SplashActivity.this, response.body().getApp());
                        setAppVersion(SplashActivity.this, response.body().getVersion());
                        String appName = getAppName(SplashActivity.this);
                        String appVersion = getAppVersion(SplashActivity.this);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "Gagal Koneksi Ke Server", Toast.LENGTH_SHORT).show();
                Log.e("Retrofit Get", t.toString());
            }
        });
    }
}
