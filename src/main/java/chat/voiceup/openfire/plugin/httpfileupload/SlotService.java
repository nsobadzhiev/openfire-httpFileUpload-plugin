package chat.voiceup.openfire.plugin.httpfileupload;

public interface SlotService {

    void setUploadServiceHost(String host);
    void setSlotCreationTimeout(int timeout);
    Slot createSlot(String user, String uploadId) throws SlotCreationException;
}
