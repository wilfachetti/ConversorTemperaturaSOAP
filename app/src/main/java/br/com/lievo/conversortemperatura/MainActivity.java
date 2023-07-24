package br.com.lievo.conversortemperatura;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.lievo.conversortemperatura.dao.TemperaturaDAO;
import br.com.lievo.conversortemperatura.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    private EditText temperaturaInicial;
    private TextView temperaturaFinal;
    private RadioButton radioTipoConversao;
    private ProgressBar progressBar;
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    protected AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        temperaturaInicial = findViewById(R.id.etTemperaturaConvertTempAct);
        temperaturaFinal = findViewById(R.id.tvTemperaturaConvertTempAct);
        radioTipoConversao = findViewById(R.id.rbCtoFConvertTempAct);
        progressBar = findViewById(R.id.progressBar);

        fadeIn.setDuration(500);
        fadeIn.setStartOffset(500);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(4000);
        fadeOut.setFillAfter(false);

    }

    private void verificaErroConexao(){
        String temperatura = temperaturaFinal.getText().toString();

        if ((temperatura.indexOf("V") == 0) || (temperatura.indexOf("E") == 0))
            temperaturaFinal.setTextSize(32);
    }

    private void converteTemperatura(){
        TemperaturaDAO dao = new TemperaturaDAO();
        String tempIni = temperaturaInicial.getText().toString();
        String tempFim = "";

        try {
            if (radioTipoConversao.isChecked())
                tempFim = dao.converteCparaF(tempIni);
            else
                tempFim = dao.converteFparaC(tempIni);

            temperaturaFinal.setText(tempFim.toString());

            Toast toast = Toast.makeText(getApplicationContext(), tempFim.toString(), Toast.LENGTH_SHORT);
            toast.show();

        }catch (RuntimeException e){
            verificaErroConexao();
        }finally {
            verificaErroConexao();
        }

        temperaturaFinal.setText(tempFim);
    }

    public boolean verificaConexaoInternet() {
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((conectivtyManager.getActiveNetworkInfo() != null) && (conectivtyManager.getActiveNetworkInfo().isAvailable()) && (conectivtyManager.getActiveNetworkInfo().isConnected()))
            return true;
        else
            return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class Task extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            temperaturaFinal.setTextSize(44);

            progressBar.startAnimation(fadeIn);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            converteTemperatura();
            return "";
        }

        @Override
        protected void onPostExecute(String retorno) {
            progressBar.startAnimation(fadeOut);
            progressBar.setVisibility(View.GONE);
        }
    }
    public void onClickBtConvertActConvertTemp(View view) {
        temperaturaFinal.setAnimation(fadeOut);
        temperaturaFinal.setText("");

        if(verificaConexaoInternet()) {
            if(!(temperaturaInicial.getText().toString().equals(""))) {
                new Task().execute();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Digite um valor para conversão!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else {
            temperaturaFinal.setText("Verifique a conexão.");
            verificaErroConexao();
        }
        temperaturaFinal.setAnimation(fadeIn);

    }
}