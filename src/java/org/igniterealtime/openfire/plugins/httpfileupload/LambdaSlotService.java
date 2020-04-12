package org.igniterealtime.openfire.plugins.httpfileupload;

import com.amazonaws.services.lambda.*;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;

import java.nio.charset.Charset;

public class LambdaSlotService implements SlotService {

    private String lambdaName;
    private int slotCreationTimeout = 15000;

    private AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().build();

    @Override
    public void setUploadServiceHost(String host) {
        lambdaName = host;
    }

    @Override
    public void setSlotCreationTimeout(int timeout) {
        slotCreationTimeout = timeout;
    }

    @Override
    public Slot createSlot(String user, String uploadId) {
        InvokeRequest request = new InvokeRequest();
        request.setFunctionName(lambdaName);
        LambdaSlotRequest slotRequest = new LambdaSlotRequest(user, uploadId);
        request.setPayload(new Gson().toJson(slotRequest));
        request.setSdkClientExecutionTimeout(slotCreationTimeout);
        InvokeResult response = lambdaClient.invoke(request);
        String resultJSON = response.getPayload().toString();
        return new Gson().fromJson(resultJSON, Slot.class);
    }
}
