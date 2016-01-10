package tsp.com.ridder;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Onder Altintas 30.5.2015
 */
public class RidderPocketHandler {
    private RidderActivity ridderActivity;
    private final String platformConsumerKey = "41120-ebb9836de9227a27fc72435c";
    private final String oauthRequestUrl = "https://getpocket.com/v3/oauth/request";
    private final String accessTokenRequestUrl = "https://getpocket.com/v3/oauth/authorize";
    private final String addPocketRequestUrl = "https://getpocket.com/v3/add";
    private String requestToken;
    private String accessToken;

    public RidderPocketHandler(RidderActivity ridderActivity) {
        this.ridderActivity = ridderActivity;

    }

    public void sendToPocket(String url, String title, String category) {
        String result = "";
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>(5);
            params.add(new BasicNameValuePair("url", url));
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("tags", category));
            params.add(new BasicNameValuePair("consumer_key", this.platformConsumerKey));
            params.add(new BasicNameValuePair("access_token", this.accessToken));
            result = doHttpRequestSync(this.addPocketRequestUrl, params);
        } catch (java.io.IOException exception) {
            result = exception.getMessage();
            ridderActivity.makeToast(exception.getMessage());
        }
    }

    public void handlePocketFirstSteps() {
        this.requestToken = getOauthRequestToken();
        Log.d("requestToken=",requestToken);
        RidderDeviceOperations ridderDeviceOperations = new RidderDeviceOperations(this.ridderActivity);
        try {
            ridderDeviceOperations.writeToFile("requestTokenForPocket.txt", requestToken);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String url = "https://getpocket.com/auth/authorize?request_token=" + this.requestToken + "&redirect_uri=pocketapp41120:authorizationFinished";
        ridderActivity.openBrowserActivity(url);
    }

    public String getAccessToken() {
        String userCredentials = "";
        RidderDeviceOperations ridderDeviceOperations = new RidderDeviceOperations(this.ridderActivity);
        this.requestToken = ridderDeviceOperations.readFromFile("requestTokenForPocket.txt");
        Log.d("RequestToken ", this.requestToken);
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("consumer_key", platformConsumerKey));
            params.add(new BasicNameValuePair("code", requestToken));
            userCredentials = doHttpRequestSync(this.accessTokenRequestUrl, params);
            ridderActivity.makeToast("Successfully logged to Pocket");
        } catch (java.io.IOException exception) {
            userCredentials = exception.getMessage();
            ridderActivity.makeToast(exception.getMessage());
        }

        this.accessToken = userCredentials.substring(userCredentials.indexOf("=") + 1, userCredentials.indexOf("&"));
        return this.accessToken;
    }

    public void logout(){
        RidderDeviceOperations ridderDeviceOperations = new RidderDeviceOperations(this.ridderActivity);
        ridderDeviceOperations.deleteFromFile("requestTokenForPocket.txt");
        handlePocketFirstSteps();
    }

    private String getOauthRequestToken() {
        String requestToken = "";
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("consumer_key", platformConsumerKey));
            params.add(new BasicNameValuePair("redirect_uri", "pocketapp41120:authorizationFinished"));
            requestToken = doHttpRequestSync(oauthRequestUrl, params);
        } catch (java.io.IOException exception) {
            requestToken = exception.getMessage();
            ridderActivity.makeToast(exception.getMessage());
        }
        return requestToken.replace("code=", "").replace(" ","").replace("\n", "").replace("\r", "");
    }

    private String doHttpRequestSync(String url, List<NameValuePair> params) throws java.io.IOException {
        HttpClient httpclient = createHttpClient();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = httpclient.execute(httppost);
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        String result = sb.toString();
        return result;
    }

    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr, params);
    }
}
