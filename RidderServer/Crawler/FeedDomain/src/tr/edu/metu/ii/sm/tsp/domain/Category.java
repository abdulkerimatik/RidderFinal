package tr.edu.metu.ii.sm.tsp.domain;

/**
 * Created by omer.dogan on 27/05/2015.
 */
public class Category {

    String categoryId;
    String categoryLabel;

    public Category(String categoryId, String categoryLabel) {
        this.categoryId = categoryId;
        this.categoryLabel = categoryLabel;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }
}
