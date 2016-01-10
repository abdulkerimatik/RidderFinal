package tr.edu.metu.ii.sm.tsp.domain;

/**
 * Created by omer.dogan on 27/05/2015.
 */
public class ArticleItem {

    String itemId;
    String title;
    String content;
    String itemUrl;
    String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArticleItem() {
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public ArticleItem(String itemId, String title, String content, String itemUrl, String imageUrl) {
        this.itemId = itemId;
        this.title = title;
        this.content = content;
        this.itemUrl = itemUrl;
        this.imageUrl = imageUrl;
    }

    public ArticleItem(String itemId, String title, String content, String itemUrl) {
        this.itemId = itemId;
        this.title = title;

        this.content = content;
        this.itemUrl = itemUrl;
    }

    public ArticleItem(String itemId, String title, String content) {
        this.itemId = itemId;
        this.title = title;
        this.content = content;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
