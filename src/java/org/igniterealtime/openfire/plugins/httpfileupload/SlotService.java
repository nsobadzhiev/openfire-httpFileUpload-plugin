package org.igniterealtime.openfire.plugins.httpfileupload;

public interface SlotService {

    void setUploadServiceHost(String host);
    void setSlotCreationTimeout(int timeout);
    Slot createSlot(String user, String uploadId);
}
