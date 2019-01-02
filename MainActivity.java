package com.example.rashmi.guessthepokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> pokemonURLs = new ArrayList<String>();
    ArrayList<String> pokemonNames = new ArrayList<String>();
     int chosenPokemon = 0;
     String[] answer = new String[4];
     int locationOfCorrectAnswer=0;
     ImageView imageView ;
     Button button0;
    Button button1;
    Button button2;
    Button button3;

    public  void pokeChosen(View view)
    {
              if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
              {
                  Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
              }
              else
              {
                  Toast.makeText(getApplicationContext(),"Wrong! It was " + pokemonNames.get(chosenPokemon),Toast.LENGTH_SHORT).show();
              }
              newQuestion();
    }

    public  class  ImageDownloader extends AsyncTask<String, Void , Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);
                 HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                         connection.connect();
                         InputStream inputStream = connection.getInputStream();
                         Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                         return  myBitmap;
            }catch (Exception e)
            {
                e.printStackTrace();
                return  null;
            }
        }
    }

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
   public  void  newQuestion()
   {
       try {

           Random rand = new Random();
           chosenPokemon = rand.nextInt(pokemonURLs.size());
           ImageDownloader imageTask = new ImageDownloader();
           Bitmap pokemonImage = imageTask.execute(pokemonURLs.get(chosenPokemon)).get();
           imageView.setImageBitmap(pokemonImage);
           locationOfCorrectAnswer = rand.nextInt(4);
           int incorrectAnswerLocation;
           for (int i = 0; i < 4; i++) {
               if (i == locationOfCorrectAnswer) {
                   answer[i] = pokemonNames.get(chosenPokemon);
               } else {
                   incorrectAnswerLocation = rand.nextInt(pokemonURLs.size());
                   while (incorrectAnswerLocation == chosenPokemon) {
                       incorrectAnswerLocation = rand.nextInt(pokemonURLs.size());
                   }
                   answer[i] = pokemonNames.get(incorrectAnswerLocation);
               }
           }

           button0.setText(answer[0]);
           button1.setText(answer[1]);
           button2.setText(answer[2]);
           button3.setText(answer[3]);
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadTask task= new DownloadTask();
        String result = null;
        try
        {
            result = task.execute("https://www.giantbomb.com/profile/wakka/lists/the-150-original-pokemon/59579/").get();

            String[] splitResult = result.split("<div class=\"aside-pod pod\">");
            Pattern p =  Pattern.compile("<img src=\"(.*?)\" /> ");

            Matcher m = p.matcher(splitResult[0]);
            while(m.find())
            {
                pokemonURLs.add(m.group(1));
            }

            p = Pattern.compile("<h3>(.*?)</h3>");
            m = p.matcher(splitResult[0]);
            while(m.find())
            {
                pokemonNames.add(m.group(1));
            }

           newQuestion();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
