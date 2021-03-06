package id.ac.polinema.retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.ac.polinema.retrofit.api.helper.ServiceGenerator;
import id.ac.polinema.retrofit.api.models.ApiError;
import id.ac.polinema.retrofit.api.models.Data;
import id.ac.polinema.retrofit.api.models.ErrorUtils;
import id.ac.polinema.retrofit.api.models.PasswordRequest;
import id.ac.polinema.retrofit.api.models.Profile;
import id.ac.polinema.retrofit.api.services.ApiInterface;
import id.ac.polinema.retrofit.ui.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPasswordActivity extends AppCompatActivity {
    public static final String TOKEN_KEY = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token, token_type;
    EditText pass, conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        pass = findViewById(R.id.edtPassword);
        conf = findViewById(R.id.edtConfirm);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            token = extras.getString(TOKEN_KEY);
            token_type = extras.getString(TOKEN_TYPE);
        }
    }

    public void prosesSubmit(View view) {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<Data<Profile>> call = service.editPassword(token_type + "" + token, new PasswordRequest(pass.getText().toString(), conf.getText().toString()));
        call.enqueue(new Callback<Data<Profile>>() {

            @Override
            public void onResponse(Call<Data<Profile>> call, Response<Data<Profile>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditPasswordActivity.this, "Change password success", Toast.LENGTH_SHORT).show();
                } else {
                    ApiError error = ErrorUtils.parseError(response);
                    if (pass.getText().toString().isEmpty()) {
                        if (pass.getText().toString().length() < 8) {
                            pass.setError(error.getError().getPassword().get(0));
                        } else {
                            pass.setError(error.getError().getPassword().get(0));
                        }
                    } else if (!pass.getText().toString().equals(conf.getText().toString())) {
                        pass.setError(error.getError().getPassword().get(0));
                        conf.setError(error.getError().getPassword().get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<Data<Profile>> call, Throwable t) {
                Toast.makeText(EditPasswordActivity.this, "Gagal change password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void prosesLogout(View view) {
        Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
