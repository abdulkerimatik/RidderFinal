package tsp.com.ridder;

/**
 * Onder Altintas 30.5.2015
 */
public class RidderEntry {
    public String itemId;
    public String title;
    public String entryUrl;
    public String imageUrl;
    public String summary;
    public String category;

    public RidderEntry() {
        this.itemId = "";
        this.title = "";
        this.entryUrl = "";
        this.imageUrl = "";
        this.summary = "";
        this.category = "";
    }

    public RidderEntry(String itemId, String title, String entryUrl, String imageUrl, String summary, String category) {
        this.itemId = itemId;
        this.title = title;
        this.entryUrl = entryUrl;
        this.imageUrl = imageUrl;
        this.summary = summary;
        this.category = category;
    }

    public String toString() {
        String result = "<div style='height:900px;width:900px;overflow:hidden;'><img src='" + this.imageUrl + "' style='height:900px;width:900px;'></div>" +"<div><b>" + this.title + "</b></div>" +  "<div><p>" + this.summary + "</p></div>";
        return result;
    }
}
