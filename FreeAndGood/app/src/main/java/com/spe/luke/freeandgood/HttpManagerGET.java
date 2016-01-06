package com.spe.luke.freeandgood;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Luke on 7/6/2015.
 */
public class HttpManagerGET
{
    public static String getData(String uri) {

        BufferedReader reader = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            //con.getHeader

            con.setReadTimeout(15000);
            con.connect();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.d("%*&#***SHOWJSON", sb.toString());
            return sb.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;

        }
        finally {
            if (reader != null) {
                try
                {
                    reader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

}