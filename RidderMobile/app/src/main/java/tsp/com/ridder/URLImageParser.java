package tsp.com.ridder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Onder Altintas 30.5.2015
 */
public class URLImageParser implements Html.ImageGetter {
    Context c;
    View container;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param t
     * @param c
     */
    public URLImageParser(View t, Context c) {
        this.c = c;
        this.container = t;
    }

    public Drawable getDrawable(String source) {

        URLDrawable urlDrawable = new URLDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = "";
                    if(params!=null) source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;
            // set the correct bound according to the result from HTTP call
            if (result != null && urlDrawable != null) {
                urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
                urlDrawable.setGravity(Gravity.TOP);
            } else {
                if(urlDrawable!= null)
                {
                    urlDrawable.setBounds(0, 0, 0,0);
                    urlDrawable.setGravity(Gravity.TOP);
                }
            }


            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();
        }

        /***
         * Get the Drawable from URL
         *
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                if(drawable!= null) {
                    drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0
                            + drawable.getIntrinsicHeight());
                }
                else
                {
                    drawable.setBounds(0,0,0,0);

                }
                return drawable;
            } catch (Exception e) {
                Log.e("Exception: ", e.getMessage());
                return null;
            }
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}


