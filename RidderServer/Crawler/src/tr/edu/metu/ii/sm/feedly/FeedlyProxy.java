package tr.edu.metu.ii.sm.feedly;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tr.edu.metu.ii.sm.tsp.domain.ArticleItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by omer.dogan on 27/05/2015.
 */
public class FeedlyProxy {

    public ArrayList<String> getFeedIds(String category, int numberOfResults) throws IOException, ParseException {
        ArrayList<String> feedIds = new ArrayList<String>();

        String searchUrl = "http://feedly.com//v3/search/feeds?query=" + category + "&count=" + numberOfResults;

        ArrayList<String> responseLines = sendRequest(searchUrl);

        for(String line:responseLines)
        {
            JSONParser parser=new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(line);

            JSONArray results = (JSONArray)obj.get("results");

            for (Object resultObject:results)
            {
                JSONObject result = (JSONObject)resultObject;
                String feedId = result.get("feedId").toString();

                feedIds.add(feedId);
            }
        }

        return feedIds;
    }

    public ArrayList<ArticleItem> getItems(String streamId) throws IOException, ParseException {
        ArrayList<ArticleItem> items = new ArrayList<ArticleItem>();


            String url = "http://feedly.com/v3/streams/contents?streamId=" + streamId;

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
                    if (summary!=null)
                    {
                        item.setContent(summary.get("content").toString());
                    }

                    JSONArray alternate = (JSONArray)itemObject.get("alternate");
                    if (alternate!=null && alternate.size()>0)
                    {
                        item.setItemUrl(((JSONObject) alternate.get(0)).get("href").toString());
                    }

                    JSONObject visual = (JSONObject)itemObject.get("visual");
                    if (visual!=null)
                    {
                        item.setImageUrl(visual.get("url").toString());
                    }

                    items.add(item);
                }
            }


        return items;
    }

    private ArrayList<String> sendRequest(String url) throws IOException {

        ArrayList<String> lines = new ArrayList<String>();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String line = "";
        while ((line = rd.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

}
