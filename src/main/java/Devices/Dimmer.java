/**
 *   Blebox API
 *   @see <a href=https://technical.blebox.eu/archives/dimmerBoxAPI/>dimmerBox api</a>
 *
 */

package Devices;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 *  Blebox Dimmerbox
 *  TODO: rest of the endpoints
 */
public class Dimmer {

    private static final String STATE_ENDPOINT="/api/device/state";
    private static final String UPDATE_ENDPOINT="/api/ota/update";
    private static final String SET_ENDPOINT="/api/dimmer/set";

    private final HttpClient httpClient=HttpClients.createDefault();
    private final String ip;
    private int desiredBrightness;

    public Dimmer(String ip){
        this.ip=ip;
    }
    public Dimmer(String ip, int desiredBrightness){
        this.ip=ip;
        this.desiredBrightness=desiredBrightness;
    }

    /**
     * Read basic info from dimmer: device name, type etc
     */
    public String readInfo() throws IOException {
        var request = new HttpGet("http://"+ip+STATE_ENDPOINT);

        var response = httpClient.execute(request);

        return EntityUtils.toString(response.getEntity());
    }

    /**
     *  Set dimmer light intensity
     * @param desiredBrightness 0-100 range
     */
    public void setLightIntensity(int desiredBrightness) throws IOException {
        var request = new HttpPost("http://"+ip+SET_ENDPOINT);

        // Convert request body to json
        var gson=new Gson();
        var brightnessRequest=new setBrightnessRequest(7,(int)(desiredBrightness*2.55),false,false);
        var entity=new StringEntity(gson.toJson(brightnessRequest));
        request.setEntity(entity);

        var response = httpClient.execute(request);

        EntityUtils.toString(response.getEntity());
    }

    /**
     * Set light intensity with the data from constructor
     */
    public void setLightIntensity() throws IOException {
        setLightIntensity(desiredBrightness);
    }

    public void updateFirmware() throws IOException {
        var request = new HttpPost("http://"+ip+UPDATE_ENDPOINT);

        httpClient.execute(request);
    }

    // Can not be static!!
    // Making it static results in hanging the whole thread after 3 executions
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
