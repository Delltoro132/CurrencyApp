package com.example.appurrencytest;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;


import javax.net.ssl.HttpsURLConnection;

class GetURLData extends AsyncTask<String, String, String> {

    protected void onPreExecute() {
        super.onPreExecute();
        //Ожидание. Дописать если нужно быудет
    }
    @Override
    protected String doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(strings[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while((line = reader.readLine()) != null)
                buffer.append(line).append("\n");
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null)
                connection.disconnect();

            try {
                if(reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {


        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject all_currency = jsonObject.getJSONObject("Valute");
            MainActivity.currency_name.clear();


            Iterator<String> iterator = all_currency.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String name_currency = all_currency.getJSONObject(key).getString("Name");
                String value_currency = all_currency.getJSONObject(key).getString("Value");
                MainActivity.currency_name.add(name_currency + "\n" + " - " + value_currency);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
