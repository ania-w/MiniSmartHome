/**
 * Blebox API
 *
 * @see <a href=https://technical.blebox.eu/archives/dimmerBoxAPI/>dimmerBox api</a>
 */

package Devices;

import Exceptions.InvalidDimmerRequestException;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *  Blebox Dimmerbox
 */
public class Dimmer extends Device {

    private static final String UPDATE_ENDPOINT = "/api/ota/update";
    private static final String SET_ENDPOINT = "/api/dimmer/set";

    private final HttpClient httpClient = HttpClients.createDefault();
    private String ip;

    public Dimmer() {
        super("Dimmer");
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    public void setLightIntensity() {
        var request = new HttpPost("http://" + ip + SET_ENDPOINT);

        var gson = new Gson();
        var brightnessRequest = new BrightnessRequest(7, (int) (data.get("lightIntensity") * 2.55), false, false);

        try {
            request.setEntity(new StringEntity(gson.toJson(brightnessRequest)));
        } catch (UnsupportedEncodingException e) {
            throw new InvalidDimmerRequestException("Cannot process request: " + brightnessRequest);
        }

        try {

             HttpResponse response = httpClient.execute(request);

             EntityUtils.consume(response.getEntity());

             request.releaseConnection();

             if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                 throw new InvalidDimmerRequestException(response.getEntity().getContent().toString());

        } catch (IOException e) {
            throw new InvalidDimmerRequestException("Cannot execute request.");
        }
    }


    //TODO
    public boolean updateFirmware() throws IOException {
        var request = new HttpGet("http://" + ip + UPDATE_ENDPOINT);

        var response = httpClient.execute(request);

        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }


    private class BrightnessRequest {
        requestParams dimmer;

        public BrightnessRequest(int loadType, int desiredBrightness, boolean overloaded, boolean overheated) {
            this.dimmer = new requestParams(loadType, desiredBrightness, overloaded, overheated);
        }

        class requestParams {
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
