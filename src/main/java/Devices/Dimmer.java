package Devices;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Blebox Dimmerbox
 *  TODO: rest of the endpoints
 */
public class Dimmer {

    private static final String STATE_ENDPOINT="/api/device/state";
    private static final String UPDATE_ENDPOINT="/api/ota/update";
    private static final String SET_ENDPOINT="/api/dimmer/set";


    private HttpClient httpClient=HttpClients.createDefault();
    private String ip;

    public Dimmer(String ip){
        this.ip=ip;
    }

    /**
     * Get basic info from dimmer: device name, type etc
     * @return json string
     * @throws IOException
     */
    public String getDimmerState() throws IOException {
        HttpGet request = new HttpGet("http://"+ip+STATE_ENDPOINT);

        HttpResponse response = httpClient.execute(request);

        return EntityUtils.toString(response.getEntity());
    }

    /**
     *  Set dimmer light intensity
     * @param desiredBrightness 0-100 range
     * @return json string
     * @throws IOException
     */
    public String setLightIntensity(int desiredBrightness) throws IOException {
        HttpPost request = new HttpPost("http://"+ip+SET_ENDPOINT);

        // Convert request body to json
        Gson gson=new Gson();
        setBrightnessRequest brightnessRequest=new setBrightnessRequest(7,(int)(desiredBrightness*2.55),false,false);
        StringEntity entity=new StringEntity(gson.toJson(brightnessRequest));
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);

       return EntityUtils.toString(response.getEntity());
    }

    public void updateFirmware() throws IOException {
        HttpPost request = new HttpPost("http://"+ip+UPDATE_ENDPOINT);

        httpClient.execute(request);
    }

    private class setBrightnessRequest{
        requestParams dimmer;

        public setBrightnessRequest(int loadType, int desiredBrightness, boolean overloaded, boolean overheated) {
            this.dimmer = new requestParams(loadType,desiredBrightness,overloaded,overheated);
        }

        class requestParams{
            int loadType;
            int desiredBrightness;
            boolean overloaded;
            boolean overheated;

            public requestParams(int loadType, int desiredBrightness, boolean overloaded, boolean overheated) {
                this.loadType = loadType;
                this.desiredBrightness = desiredBrightness;
                this.overloaded = overloaded;
                this.overheated = overheated;
            }
        }
    }


}
