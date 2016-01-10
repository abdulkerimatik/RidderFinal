package tr.edu.metu.ii.sm.tsp.domain;

import java.util.ArrayList;

/**
 * Created by omer.dogan on 27/05/2015.
 */
public class Subscription {

    String feedId;
    String title;
    String htmlUrl;
    String iconUrl;

    ArrayList<Category> categories;

    public Subscription() {
        this.feedId = "";
        this.title = "";
        this.htmlUrl = "";
        this.iconUrl = "";
        this.categories=new ArrayList<>();
    }

    public Subscription(String feedId, String title, String htmlUrl, String iconUrl) {
        this.feedId = feedId;
        this.title = title;
        this.htmlUrl = htmlUrl;
        this.iconUrl = iconUrl;
        this.categories=new ArrayList<>();
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category)
    {
        categories.add(category);
    }

    public void addCategory(String categoryId, String categoryLabel)
    {
        this.addCategory(new Category(categoryId, categoryLabel));
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
