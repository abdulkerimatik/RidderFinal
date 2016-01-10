package tsp.com.ridder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class SettingsActivity extends Activity {

    private LinearLayout switch_Layout;
    private String deviceId;
    private RidderCategories ridderCategories;
    private RidderActivity ridderActivity;
    private RidderServiceHandler ridderServiceHandler;
    private RidderCategoriesHandler ridderCategoriesHandler;
    private RidderDeviceOperations ridderDeviceOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deviceId = getIntent().getSerializableExtra("DeviceId").toString();
        ridderServiceHandler = new RidderServiceHandler(this.ridderActivity,deviceId);
        ridderCategoriesHandler=new RidderCategoriesHandler();
        this.ridderCategories = ridderServiceHandler.loadCategories();
        setContentView(R.layout.activity_settings);
        try {
            createSwitch();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createSwitch() throws JSONException {
        switch_Layout = (LinearLayout) findViewById(R.id.switchLayout);
        for (int i = 0; i < this.ridderCategories.categories.length(); i++) {
            Switch switchItem = new Switch(this);
            switchItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            JSONObject category = (JSONObject)this.ridderCategories.categories.get(i);
            switchItem.setText(category.getString("categoryName"));
            boolean switchOpen = (category.getString("userSubscribed") == "true");
            switchItem.setChecked(switchOpen);
            switchItem.getLayoutParams().width = switch_Layout.getLayoutParams().width;
            switchItem.getLayoutParams().height = 120;
            switch_Layout.addView(switchItem);

            switchItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        String buttonName = buttonView.getText().toString();
                        for (int i = 0; i < ridderCategories.categories.length(); i++) {
                            try {
                                JSONObject category = (JSONObject)ridderCategories.categories.get(i);
                                if(buttonName==category.getString("categoryName"))
                                {
                                    String isCheckedVal = isChecked?"true":"false";
                                    category.put("userSubscribed",isCheckedVal);
                                    ridderCategories.categories.put(i,category);
                                    if(ridderCategoriesHandler.isAllFalse(ridderCategories))
                                    {
                                        Toast.makeText(getApplicationContext(), "You should choose at least one category", Toast.LENGTH_SHORT).show();
                                    }else {
                                        ridderServiceHandler.saveCategories(ridderCategories);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), buttonView.getText() + " ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), buttonView.getText() + " OFF", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("me", "settings");
        setResult(RESULT_OK, intent);
        if(ridderCategoriesHandler.isAllFalse(ridderCategories))
        {
            Toast.makeText(getApplicationContext(), "You should choose at least one category!!!", Toast.LENGTH_LONG).show();
        }
        else {
            super.onBackPressed();
        }
    }
}
