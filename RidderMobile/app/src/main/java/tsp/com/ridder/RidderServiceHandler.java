package tsp.com.ridder;

import android.app.DownloadManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Onder on 31.05.2015.
 */
public class RidderServiceHandler {
    private final String serviceUrl = "http://46.101.152.220/webservice/webservice.php?";
    RidderActivity ridderActivity;
    String deviceId;

    public RidderServiceHandler(RidderActivity ridderActivity, String deviceId) {
        this.ridderActivity = ridderActivity;
        this.deviceId = deviceId;
    }

    public void registerRidderService() {
        String url = serviceUrl + "method=register&deviceId=" + this.deviceId;
        Log.d("url:", url);
        try {
            doHttpRequestSync(url);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        }
    }

    public RidderCategories loadCategories() {
        RidderCategories ridderCategories = new RidderCategories();
        String result = "";
        try {
            String url = serviceUrl + "method=usercategories&deviceId=" + deviceId;
            Log.d("url:", url);
            result = doHttpRequestSync(url);
            JSONObject categoriesJsonObject = new JSONObject(result);
            ridderCategories.categories = categoriesJsonObject.getJSONArray("categories");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ridderCategories;
    }

    public void saveCategories(RidderCategories ridderCategories) {
        String categoriesString = "";
        try {
            for (int i = 0; i < ridderCategories.categories.length(); i++) {
                JSONObject category = (JSONObject) ridderCategories.categories.get(i);
                String userSubscribed = category.getString("userSubscribed");
                if (userSubscribed == "true") {
                    categoriesString += category.getString("categoryId") + "-";
                }
            }
            categoriesString = categoriesString.substring(0, categoriesString.length()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String url = serviceUrl + "method=savecategories&deviceId=" + deviceId + "&categories=" + categoriesString;
            Log.d("kategori save",url);
            doHttpRequestSync(url);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void loadEntries() {
        ArrayList<RidderEntry> newEntries = new ArrayList<RidderEntry>();
        String result = "";
        try {
            String url = serviceUrl + "method=entries&deviceId=" + deviceId;
            Log.d("url:", url);
            result = doHttpRequestSync(url);
            JSONObject entriesJsonObject = new JSONObject(result);
            JSONArray entriesJsonArray = entriesJsonObject.getJSONArray("entries");
            JSONObject entryUnnecessary = (JSONObject) entriesJsonArray.get(0);
            JSONObject entryJsonObjectId = new JSONObject(entryUnnecessary.getString("_id"));
            String id = entryJsonObjectId.getString("$id");
            String title = entryUnnecessary.getString("Title");
            String entryUrl = entryUnnecessary.getString("EntryUrl");
            String imageUrl = entryUnnecessary.getString("ImageUrl");
            String summary = entryUnnecessary.getString("Summary")=="null"?"":entryUnnecessary.getString("Summary");
            String category = entryUnnecessary.getString("Category");
            RidderEntry entry = new RidderEntry(id,title,entryUrl,imageUrl,summary,category);
            newEntries.add(entry);
            ridderActivity.entriesReady(newEntries);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void markEntry(RidderEntry entry, boolean interested)
    {
        String result = "";
        String interestedString = interested?"true":"false";
        try {
            String url = serviceUrl + "method=markentry&deviceId=" + deviceId+"&entryId="+entry.itemId+"&interested="+interestedString;
            Log.d("markentry:", url);
            result = doHttpRequestSync(url);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        }
}


    private String doHttpRequestSync(String url) throws java.io.IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        String result = sb.toString();
        return result;
    }
}
