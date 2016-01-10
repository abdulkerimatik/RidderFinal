package tsp.com.ridder;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by altintas.onder on 3.6.2015.
 */
public class RidderCategoriesHandler {

    public static boolean isAllFalse(RidderCategories ridderCategories)
    {
        try {
        for (int i = 0; i < ridderCategories.categories.length(); i++) {
            JSONObject category = (JSONObject) ridderCategories.categories.get(i);
            String userSubscribed = null;

                userSubscribed = category.getString("userSubscribed");

            if (userSubscribed == "true") {
               return false;
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    public void openSettingsPage(RidderActivity ridderActivity,String deviceId){
        Intent settingsIntent = new Intent(ridderActivity, SettingsActivity.class);
        settingsIntent.putExtra("DeviceId", deviceId);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ridderActivity.startActivityForResult(settingsIntent, 0);
    }
}
