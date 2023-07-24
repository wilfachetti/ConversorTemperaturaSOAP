package br.com.lievo.conversortemperatura.dao;

import android.annotation.SuppressLint;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;

public class TemperaturaDAO {

    private static final String NAMESPACE = "https://www.w3schools.com/xml/";
    private static final String URL = "https://www.w3schools.com/xml/tempconvert.asmx?WSDL";
    private static final String METHOD_NAME1 = "CelsiusToFahrenheit";
    private static final String METHOD_NAME2 = "FahrenheitToCelsius";
    private static final String SOAP_ACTION1 = String.format("%s%s", NAMESPACE, METHOD_NAME1);
    private static final String SOAP_ACTION2 = String.format("%s%s", NAMESPACE, METHOD_NAME2);

    private HttpTransportSE http;
    private SoapObject request;
    private SoapSerializationEnvelope envelope;
    private String resultado;
    private static final int TIMEOUT = 10000;

    public TemperaturaDAO(){

        resultado = null;

        // Cria envelope para SOAP
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        // Define o objeto que efetuará o transporte. Aqui também é definido o timeout.
        http = new HttpTransportSE(Proxy.NO_PROXY, URL, TIMEOUT);

    }

    private void finalizaConexao(){

        request = null;
        envelope = null;
        http = null;

    }

    @SuppressLint("DefaultLocale")
    public String converteCparaF(String grausC) {

        // Cria a requisição SOAP, para consumir o webService.
        request = new SoapObject(NAMESPACE, METHOD_NAME1);

        // Acrescenta o parâmetro ao argumento do webService.
        request.addProperty("Celsius", grausC);

        // Insere a requisição montada no envelope.
        envelope.setOutputSoapObject(request);

        String parcial;

        try {
            // Solicita uma chamada, envia o envelope no endereço
            http.call(SOAP_ACTION1, envelope);
            parcial = envelope.getResponse().toString();

            resultado = String.format("%.2f ºF", Float.valueOf(parcial));

        } catch (IOException | XmlPullParserException e) {

            resultado = "Verifique a conexão.";
            e.printStackTrace();
        }

        finalizaConexao();

        return resultado;
    }

    @SuppressLint("DefaultLocale")
    public String converteFparaC(String grausF){

        // Cria a requisição SOAP, para consumir o webService.
        request = new SoapObject(NAMESPACE, METHOD_NAME2);

        // Acrescenta o parâmetro ao argumento do webService.
        request.addProperty("Fahrenheit", grausF);

        // Insere a requisição montada no envelope.
        envelope.setOutputSoapObject(request);

        String parcial;

        try {
            http.call(SOAP_ACTION2, envelope);
            parcial = envelope.getResponse().toString();

            resultado = String.format("%.2f ºC", Float.valueOf(parcial));

        } catch (IOException | XmlPullParserException e) {

            resultado = "Verifique a conexão.";
            e.printStackTrace();
        }

        finalizaConexao();

        return resultado;
    }
}