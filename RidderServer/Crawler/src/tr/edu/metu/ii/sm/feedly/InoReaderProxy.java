package tr.edu.metu.ii.sm.feedly;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tr.edu.metu.ii.sm.tsp.domain.ArticleItem;
import tr.edu.metu.ii.sm.tsp.domain.Subscription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by omer.dogan on 27/05/2015.
 */
public class InoReaderProxy {

    String username;
    String password;

    String authToken;

    public InoReaderProxy(String username, String password) {
        this.username = username;
        this.password = password;
        this.authToken = null;
    }

    public boolean authenticate() throws IOException {
        String authUrl = "https://www.inoreader.com/accounts/ClientLogin?Email=" + this.username + "&Passwd=" + this.password;

        ArrayList<String> responseLines = sendRequest(authUrl);

        for(String line:responseLines)
        {
            String[] lineParts = line.split("=");
            if (lineParts.length==2 && lineParts[0].equals("Auth"))
            {
                this.authToken = lineParts[1];
                return true;
            }
        }

        return false;
    }

public ArrayList<Subscription> getSubscriptions() throws IOException, ParseException {
    ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();

    if (this.authToken!=null)
    {
        String url = "https://www.inoreader.com/reader/api/0/subscription/list";

        ArrayList<String> responseLines = sendRequest(url);
        for(String line:responseLines)
        {
            JSONParser parser=new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(line);

            JSONArray subscriptionsInResponse = (JSONArray)obj.get("subscriptions");
            for(Object subscriptionInResponse:subscriptionsInResponse)
            {
                JSONObject subscriptionObject = (JSONObject)subscriptionInResponse;

                Subscription subscription = new Subscription();
                subscription.setFeedId(subscriptionObject.get("id").toString());
                subscription.setHtmlUrl(subscriptionObject.get("htmlUrl").toString());
                subscription.setIconUrl(subscriptionObject.get("iconUrl").toString());
                subscription.setTitle(subscriptionObject.get("title").toString());

                JSONArray categories = (JSONArray)subscriptionObject.get("categories");

                for(Object categoryObject:categories)
                {
                    JSONObject category = (JSONObject)categoryObject;
                    subscription.addCategory(category.get("id").toString(), category.get("label").toString());
                }

                subscriptions.add(subscription);
            }
        }
    }

    return subscriptions;
}

    public ArrayList<ArticleItem> getItems(String streamId) throws IOException, ParseException {
        ArrayList<ArticleItem> items = new ArrayList<ArticleItem>();

        if (this.authToken!=null)
        {
            String url = "https://www.inoreader.com/reader/api/0/stream/contents/" + streamId;

            ArrayList<String> responseLines = sendRequest(url);
            for(String line:responseLines)
            {
                JSONParser parser=new JSONParser();
                JSONObject obj = (JSONObject)parser.parse(line);

                JSONArray itemsInResponse = (JSONArray)obj.get("items");
                for(Object itemInResponse:itemsInResponse)
                {
                    JSONObject itemObject = (JSONObject)itemInResponse;

                    ArticleItem item = new ArticleItem();
                    item.setItemId(itemObject.get("id").toString());
                    item.setTitle(itemObject.get("title").toString());

                    JSONObject summary = (JSONObject)itemObject.get("summary");

                    item.setContent(summary.get("content").toString());

                    items.add(item);
                }
            }
        }

        return items;
    }

    private ArrayList<String> sendRequest(String url) throws IOException {

        ArrayList<String> lines = new ArrayList<String>();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        String headerValue = "GoogleLogin auth=" + this.authToken;
        request.addHeader(new BasicHeader("Authorization",headerValue));

        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String line = "";
        while ((line = rd.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }
}
