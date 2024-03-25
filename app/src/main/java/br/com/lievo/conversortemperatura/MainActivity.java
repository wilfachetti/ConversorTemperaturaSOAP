package br.com.lievo.conversortemperatura;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import br.com.lievo.conversortemperatura.dao.DaoTemperature;

public class MainActivity extends Activity {
    private EditText initialTemperature;
    private TextView finalTemperature;
    private RadioButton radioTypeConversion;
    private ProgressBar progressBar;
    protected AlphaAnimation fadeIn;
    protected AlphaAnimation fadeOut;
    private String finalTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeVariables();
    }

    public void initializeVariables() {
        initialTemperature = findViewById(R.id.etInitialTemperature);
        finalTemperature = findViewById(R.id.tvFinalTemperature);
        radioTypeConversion = findViewById(R.id.rbCtoFConvert);
        progressBar = findViewById(R.id.progressBar);

        putSettingsAnimationConversion();
    }

    public void putSettingsAnimationConversion() {
        fadeIn = new AlphaAnimation(0f, 1f);
        fadeOut = new AlphaAnimation(1f, 0f);

        fadeIn.setDuration(1000);
        //fadeIn.setStartOffset(500);
        fadeIn.setFillAfter(true);

        fadeOut.setDuration(1000);
        //fadeOut.setStartOffset(500);
        fadeOut.setFillAfter(true);
    }

    public void animationForBegin() {
        finalTemperature.setAnimation(fadeOut);
        finalTemperature.startAnimation(fadeOut);
        finalTemperature.setAlpha(0f);
        finalTemperature.setVisibility(View.GONE);
        finalTemperature.setText("");

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAnimation(fadeIn);
        progressBar.startAnimation(fadeIn);
        progressBar.setAlpha(1f);
    }

    public void animationForEnd() {
        progressBar.setAnimation(fadeOut);
        progressBar.startAnimation(fadeOut);
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.GONE);

        finalTemperature.setText(finalTemp);
        finalTemperature.setVisibility(View.VISIBLE);
        finalTemperature.setAnimation(fadeIn);
        finalTemperature.startAnimation(fadeIn);
        finalTemperature.setAlpha(1f);
    }

    public void setMessageErrorConnection(String message) {
        finalTemperature.setText(message);
    }

    public void toastFillTemperature() {
        //Toast toast = Toast.makeText(getApplicationContext(), R.string.digite_um_valor_para_conversao, Toast.LENGTH_SHORT);
        Toast.makeText(getApplicationContext(), R.string.big_digite_valor, Toast.LENGTH_SHORT).show();
        //toast.show();
    }

    private Boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {
                setMessageErrorConnection(getString(R.string.verifique_sua_rede));
                return false;
            }
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            setMessageErrorConnection(getString(R.string.erro_compatibilidade_android));
            return false;
        }
    }

    private void convertTemperature(){
        DaoTemperature daoTemperature = new DaoTemperature();
        String tempIni = initialTemperature.getText().toString();

        if (radioTypeConversion.isChecked())
            finalTemp = daoTemperature.convertCtoF(tempIni);
        else
            finalTemp = daoTemperature.convertFtoC(tempIni);
    }

    public void onClickBtConvertTemp(View view) {
        if(checkInternetConnection()) {
            if(!(initialTemperature.getText().toString().equals(""))) {
                animationForBegin();

                new BackgroundTask(MainActivity.this) {
                    @Override
                    public void doInBackground() {
                        convertTemperature();
                    }

                    @Override
                    public void onPostExecute() {
                        animationForEnd();
                    }
                }.execute();

            }else{
                toastFillTemperature();
            }
        }else
            setMessageErrorConnection(getString(R.string.verifique_sua_rede));
    }
}