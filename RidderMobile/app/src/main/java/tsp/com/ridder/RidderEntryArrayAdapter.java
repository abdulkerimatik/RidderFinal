package tsp.com.ridder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.text.Html;

import java.util.ArrayList;

/**
 * Onder Altintas 30.5.2015
 */
public class RidderEntryArrayAdapter<RidderEntry> extends ArrayAdapter<RidderEntry> {

    Context mContext;
    int layoutResourceID;
    int resource;
    ArrayList<RidderEntry> data = null;

    //constructor
    public RidderEntryArrayAdapter(Context mContext, int resource, int layoutResourceID, ArrayList<RidderEntry> data) {
        super(mContext, layoutResourceID, data);
        this.layoutResourceID = layoutResourceID;
        this.resource = resource;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            //inflate the layout
            LayoutInflater inflater = ((RidderActivity) mContext).getLayoutInflater();
            convertView = inflater.inflate(this.resource, parent, false);
        }
        //movie item based on the position
        RidderEntry content = (RidderEntry) this.data.get(position);
        String htmlString = content.toString();

        //get the TextView and then set the text(movie name) and tag(show_Movie.i) values
        TextView textView = (TextView) convertView.findViewById(R.id.newsCard);
        URLImageParser imageParser = new URLImageParser(textView, mContext);
        textView.setText(Html.fromHtml(htmlString, imageParser, null));
        return textView;
    }
}



