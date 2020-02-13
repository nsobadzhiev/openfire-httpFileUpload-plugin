package org.igniterealtime.openfire.plugins.httpfileupload;

public interface SlotService {

    public void setUploadServiceHost(String host);
    public Slot createSlot(String user, String uploadId);
}
