package tr.edu.metu.ii.sm.feedly;

import org.json.simple.parser.ParseException;
import tr.edu.metu.ii.sm.tsp.domain.ArticleItem;
import tr.edu.metu.ii.sm.tsp.mongo.MongoConnector;

import java.io.IOException;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {


        FeedlyProxy feedlyProxy = new FeedlyProxy();

        MongoConnector mongoConnector = new MongoConnector();
        ArrayList<String> categories = mongoConnector.getAllCategories();

        for(String category:categories)
        {
            ArrayList<String> feedIds =  feedlyProxy.getFeedIds(category,20);

            for(String feedId:feedIds)
            {
                System.out.println("Feed ID:" + feedId);

                ArrayList<ArticleItem> items = feedlyProxy.getItems(feedId);

                for(ArticleItem item:items)
                {
                    System.out.print("--->");
                    System.out.print("Item Id: " + item.getItemId() + " - ");
                    System.out.print("URL: " + item.getItemUrl() + " - ");
                    System.out.print("Image Url: " + item.getImageUrl() + " - ");
                    System.out.println("Item title: " + item.getTitle() + " - ");
                    //System.out.println("Item Content: " + item.getContent());

                   boolean inserted = mongoConnector.addEntry(item,feedId,category);
                    String insertMessage=inserted?"Inserted":"NOT INSERTED";
                    System.out.println("Item " + insertMessage);
                }
            }
        }




//        String email = "doganomer";
//        String password = "falcon";
//
//        InoReaderProxy proxy = new InoReaderProxy(email,password);
//
//        if (!proxy.authenticate())
//        {
//            System.out.println("Authentication failed");
//            return;
//        }
//
//        ArrayList<Subscription> subscriptions = proxy.getSubscriptions();
//
//        for(Subscription subs:subscriptions)
//        {
//            System.out.print("FeedId: " + subs.getFeedId() + " - ");
//            System.out.print("Title: " + subs.getTitle() + " - ");
//            System.out.print("HtmlUrl: " + subs.getHtmlUrl() + " - ");
//            System.out.println("IconUrl: " + subs.getIconUrl());
//
//            for(Category cat:subs.getCategories())
//            {
//                System.out.print("--->");
//                System.out.print("Category Id: " + cat.getCategoryId() + " - ");
//                System.out.println("Category Label: " + cat.getCategoryLabel() + " - ");
//            }
//
//            ArrayList<ArticleItem> items = proxy.getItems(subs.getFeedId());
//
//            for(ArticleItem item:items)
//            {
//                System.out.print("--->");
//                System.out.print("Item Id: " + item.getItemId() + " - ");
//                System.out.println("Item title: " + item.getTitle() + " - ");
//                //System.out.println("Item Content: " + item.getContent());
//            }
//        }

    }
}
