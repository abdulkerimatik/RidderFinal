package tsp.com.ridder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.UnsupportedEncodingException;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URLEncoder;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Onder Altintas 31.5.2015
 */
public class RidderActivity extends Activity {

    private ArrayList<RidderEntry> entries;
    private ArrayAdapter<RidderEntry> arrayAdapter;
    RidderServiceHandler ridderServiceHandler;
    RidderDeviceOperations ridderDeviceOperations;
    RidderPocketHandler ridderPocketHandler;
    RidderCategoriesHandler ridderCategoriesHandler;
    String deviceId;
    private int i;

    @InjectView(R.id.frame)
    SwipeFlingAdapterView flingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //TextView tv = (TextView)findViewById(R.id.newsCard);
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridder);
        ButterKnife.inject(this);
        initializeRidder();

    }

    private void initializeRidder() {
        Intent intent = getIntent();
        entries = new ArrayList<RidderEntry>();
        arrayAdapter = new RidderEntryArrayAdapter<RidderEntry>(this, R.layout.item, R.id.newsCard, entries);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                entries.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (RidderEntry) dataObject
                //makeToast(RidderActivity.this, "Left!");
                RidderEntry dislikedEntry = (RidderEntry) dataObject;
                ridderServiceHandler.markEntry(dislikedEntry,false);
                makeToast(RidderActivity.this, "Throwing Awaaay!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //makeToast(RidderActivity.this, "Right!");
                RidderEntry likedEntry = (RidderEntry) dataObject;
                makePocketToast(RidderActivity.this, "Going to Pocket!");
                ridderServiceHandler.markEntry(likedEntry,true);
                ridderPocketHandler.sendToPocket(likedEntry.entryUrl,likedEntry.title,likedEntry.category);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
//                RidderEntry newEntry = new RidderEntry("1", "Hamza!", "www.gog.com", "http://cdn1.ouchpress.com/thumbs/celebrities/3/435393-scarlett-johansson-150.jpg", "bir gün birt şeydehdıehdıehd", "game");
//                entries.add(newEntry);
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
                ridderServiceHandler.loadEntries();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(MyActivity.this, "Clicked!");
                //not necessary for ridder project.
            }
        });
        ridderDeviceOperations = new RidderDeviceOperations(this);
        this.deviceId = ridderDeviceOperations.getDeviceId();
        ridderPocketHandler = new RidderPocketHandler(this);
        ridderServiceHandler = new RidderServiceHandler(this, deviceId);
        ridderCategoriesHandler = new RidderCategoriesHandler();
        if (intent.getDataString() == null) {
            ridderPocketHandler.handlePocketFirstSteps();
        } else {
            String accessToken = ridderPocketHandler.getAccessToken();
            Log.d("Son token:",accessToken);
            ridderServiceHandler.registerRidderService();
            RidderCategories ridderCategories = ridderServiceHandler.loadCategories();
            boolean isFirstTimeRidder = RidderCategoriesHandler.isAllFalse(ridderCategories);
            if(isFirstTimeRidder) {
                ridderCategoriesHandler.openSettingsPage(this,this.deviceId);
            }
        }




        ridderServiceHandler.loadEntries();
    }

    public void makeToast(Context ctx, String s) {
        //Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) findViewById(R.id.customToast));
        TextView textViewToChange = (TextView) view.findViewById(R.id.bottomTextView);
        textViewToChange.setText(s);
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.show();
    }

    public void makePocketToast(Context ctx, String s) {
        //Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_pocket_toast_layout,
                (ViewGroup) findViewById(R.id.customToast));
        TextView textViewToChange = (TextView) view.findViewById(R.id.bottomTextView);
        textViewToChange.setText(s);
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.show();
    }

    public void makeToast(String s) {
        this.makeToast(RidderActivity.this, s);
    }

    public void makePocketToast(String s) {
        this.makePocketToast(RidderActivity.this, s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ridder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.e("Clicked item id: ", "" + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ridderCategoriesHandler.openSettingsPage(this,this.deviceId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ridderServiceHandler.loadEntries();
        makeToast("Categories are saved");
        Log.d("categories saved!!","yaşasın");
    }

    public void entriesReady(ArrayList<RidderEntry> newEntries) {
        ////this.entries = newEntries;
        for (int l = 0; l < newEntries.size(); l++) {
            entries.add((RidderEntry) newEntries.get(l));
        }
        arrayAdapter.notifyDataSetChanged();
        Log.d("LIST", "notified");
        i++;
    }

    public void openBrowserActivity(String url) {
        String encodedUrl = "";
        try {
            encodedUrl = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse(url));
        Log.d("encodedUrl", encodedUrl.toString());
        Log.d("normalUrl", url);
        startActivity(intent);

    }



//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        this.bypassPocketAuth = true;
//        this.makeToast("INTEEENT");
//    }
}
