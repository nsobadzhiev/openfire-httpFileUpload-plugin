package chat.voiceup.openfire.plugin.httpfileupload;

import com.google.gson.Gson;
import org.jivesoftware.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class VoiceSlotService implements SlotService {

    private String uploadServiceHost;
    private String uploadServiceResourcePath = "create-slot";
    private int slotCreationTimeout = 15000;

    @Override
    public Slot createSlot(String user, String uploadId) {
        String slotJson;
        try {
            slotJson = requestSlot(slotCreationUrl(user, uploadId), 15000);
        } catch (MalformedURLException e) {
            Log.error(e.toString());
            return null;
        }
        return parseSlot(slotJson);
    }

    public String getUploadServiceHost() {
        return uploadServiceHost;
    }

    public int getSlotCreationTimeout() {
        return slotCreationTimeout;
    }

    @Override
    public void setUploadServiceHost(String uploadServiceHost) {
        this.uploadServiceHost = uploadServiceHost;
    }

    @Override
    public void setSlotCreationTimeout(int timeout) {
        this.slotCreationTimeout = timeout;
    }

    private String requestSlot(URL url, int timeout) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Log.error(ex.toString());
        } catch (IOException ex) {
            Log.error(ex.toString());
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Log.error(ex.toString());
                }
            }
        }
        return null;
    }

    private URL slotCreationUrl(String user, String uploadId) throws MalformedURLException {
        final String fullPath = getUploadServiceHost() + "/" + uploadServiceResourcePath + "?user=" + user + "&uploadId=" + uploadId;
        return new URL(fullPath);
    }

    private Slot parseSlot(String response) {
        Slot slot = new Gson().fromJson(response, Slot.class);
        return slot;
    }

}
