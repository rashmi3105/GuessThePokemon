package com.example.rashmi.guessthepokemon;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> pokemonURLs = new ArrayList<String>();
    ArrayList<String> pokemonNames = new ArrayList<String>();

   public  class DownloadTask  extends AsyncTask<String, Void, String>
   {

       @Override
       protected String doInBackground(String... urls) {
             String result="";
           URL url;
           HttpURLConnection urlConnection = null;
           try
           {
               url= new URL(urls[0]);
               urlConnection=(HttpURLConnection) url.openConnection();
               InputStream in = urlConnection.getInputStream();
               InputStreamReader reader = new InputStreamReader(in);

               int data = reader.read();
               while(data!=-1)
               {
                   char current = (char) data;
                   result+=current;
                   data = reader.read();
               }
           return result;
           }catch( Exception e)
           {
               e.printStackTrace();
               return  null;
           }
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task= new DownloadTask();
        String result = null;
        try
        {
            result = task.execute("https://www.ign.com/wikis/pokemon-go/List_of_Pokemon_(Pokedex)").get();

            String[] splitResult = result.split("<span class=\"mw-headline\" id=\"Gen_2_Pokemon_.28Johto_Pokedex.29\">");
            Pattern p =  Pattern.compile("img alt=\"(.*?)\" src=\"(.*?)\"");

            Matcher m = p.matcher(splitResult[0]);
            while(m.find())
            {
                pokemonURLs.add(m.group(1));
            }

            p = Pattern.compile("/wikis/pokemon-go/(.*?)");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
