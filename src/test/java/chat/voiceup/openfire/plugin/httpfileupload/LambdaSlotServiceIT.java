package chat.voiceup.openfire.plugin.httpfileupload;

import org.junit.Assert;
import org.junit.Test;

public class LambdaSlotServiceIT {

    @Test
    public void testCreatingSlot() {
        SlotService slotService = new LambdaSlotService();
        slotService.setUploadServiceHost("VoiceStorageLambda");
        Slot slot = slotService.createSlot("testuser", "testuploadid");
        Assert.assertNotNull(slot);
    }
}
