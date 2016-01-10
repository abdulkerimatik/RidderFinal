package tsp.com.ridder;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

/**
 * Created by Onder Altintas on 31.05.2015.
 * Any kind of device operations occur here, getting device id, getting state, writing file etc.
 * You know what I mean.
 */
public class RidderDeviceOperations {
    RidderActivity ridderActivity;

    public RidderDeviceOperations(RidderActivity ridderActivity) {
        this.ridderActivity = ridderActivity;
    }

    public String getDeviceId() {
        String deviceId = "2299";
        final TelephonyManager tm = (TelephonyManager) this.ridderActivity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(this.ridderActivity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceId = deviceUuid.toString().replace("-","");
        Log.d("device id",deviceId);
        return deviceId;
    }

    public void writeToFile(String fileName, String data) throws java.io.FileNotFoundException {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ridderActivity.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public String readFromFile(String fileName) {

        String ret = "";

        try {
            InputStream inputStream = ridderActivity.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public boolean deleteFromFile(String fileName)
    {
        boolean deleted =true;
        File file = new File(fileName);
        if(file.exists()) deleted = file.delete();
        return deleted;
    }
}
