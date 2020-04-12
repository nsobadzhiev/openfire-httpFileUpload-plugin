package org.igniterealtime.openfire.plugins.httpfileupload;

public class LambdaSlotRequest {

    public LambdaSlotRequest(String user, String uploadId) {
        this.user = user;
        this.uploadId = uploadId;
    }

    public LambdaSlotRequest() {
        this.user = null;
        this.uploadId = null;
    }

    public String getUser() {
        return user;
    }

    public String getUploadId() {
        return uploadId;
    }

    private String user;
    private String uploadId;
}
