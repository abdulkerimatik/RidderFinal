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
 * depreciated cause of existence of RidderEntryArrayAdapter, delete this.
 * We look front, we don't look back.
 */
public class RidderArrayAdapter<T> extends ArrayAdapter<T> {

    Context mContext;
    int layoutResourceID;
    int resource;
    ArrayList<T> data = null;

    //constructor
    public RidderArrayAdapter(Context mContext, int resource, int layoutResourceID, ArrayList<T> data) {
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
        String content = (String) data.get(position);

        //get the TextView and then set the text(movie name) and tag(show_Movie.i) values
        TextView textView = (TextView) convertView.findViewById(R.id.newsCard);
        URLImageParser imageParser = new URLImageParser(textView, mContext);
        textView.setText(Html.fromHtml(content, null, null));
        return textView;
    }
}



