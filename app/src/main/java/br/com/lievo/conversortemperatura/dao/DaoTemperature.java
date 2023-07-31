package br.com.lievo.conversortemperatura.dao;

import android.annotation.SuppressLint;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;

public class DaoTemperature {

    private static final String NAMESPACE = "https://www.w3schools.com/xml/";
    private static final String URL = "https://www.w3schools.com/xml/tempconvert.asmx?WSDL";
    private static final String METHOD_NAME1 = "CelsiusToFahrenheit";
    private static final String METHOD_NAME2 = "FahrenheitToCelsius";
    private static final String SOAP_ACTION1 = String.format("%s%s", NAMESPACE, METHOD_NAME1);
    private static final String SOAP_ACTION2 = String.format("%s%s", NAMESPACE, METHOD_NAME2);

    private HttpTransportSE http;
    private SoapObject request;
    private SoapSerializationEnvelope envelope;
    private String result;
    private static final int TIMEOUT = 10000;

    public DaoTemperature(){
        result = null;

        // Cria envelope para SOAP
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;

        // Define o objeto que efetuará o transporte. Aqui também é definido o timeout.
        http = new HttpTransportSE(Proxy.NO_PROXY, URL, TIMEOUT);
    }

    private void endConnection(){
        request = null;
        envelope = null;
        http = null;
    }

    @SuppressLint("DefaultLocale")
    public String convertCtoF(String degreesC) {
        // Cria a requisição SOAP, para consumir o webService.
        request = new SoapObject(NAMESPACE, METHOD_NAME1);
        // Acrescenta o parâmetro ao argumento do webService.
        request.addProperty("Celsius", degreesC);
        // Insere a requisição montada no envelope.
        envelope.setOutputSoapObject(request);

        String partial;
        try {
            // Solicita uma chamada, envia o envelope no endereço
            http.call(SOAP_ACTION1, envelope);
            partial = envelope.getResponse().toString();

            result = String.format("%.2f ºF", Float.valueOf(partial));
        } catch (IOException | XmlPullParserException e) {
            //result = "Connection error.";
            e.printStackTrace();
        }
        endConnection();

        return result;
    }

    @SuppressLint("DefaultLocale")
    public String convertFtoC(String degreesF){
        request = new SoapObject(NAMESPACE, METHOD_NAME2);
        request.addProperty("Fahrenheit", degreesF);
        envelope.setOutputSoapObject(request);

        String partial;
        try {
            http.call(SOAP_ACTION2, envelope);
            partial = envelope.getResponse().toString();

            result = String.format("%.2f ºC", Float.valueOf(partial));

        } catch (IOException | XmlPullParserException e) {
            //result = "Connection Error.";
            e.printStackTrace();
        }
        endConnection();

        return result;
    }
}