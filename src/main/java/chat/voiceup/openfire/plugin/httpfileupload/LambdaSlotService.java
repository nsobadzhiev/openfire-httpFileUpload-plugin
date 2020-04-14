package chat.voiceup.openfire.plugin.httpfileupload;

import com.amazonaws.services.lambda.*;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaSlotService implements SlotService {

    private String lambdaName;
    private int slotCreationTimeout = 15000;

    private AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().build();

    private static final Logger Log = LoggerFactory.getLogger( LambdaSlotService.class );

    @Override
    public void setUploadServiceHost(String host) {
        lambdaName = host;
    }

    @Override
    public void setSlotCreationTimeout(int timeout) {
        slotCreationTimeout = timeout;
    }

    @Override
    public Slot createSlot(String user, String uploadId) throws SlotCreationException {
        InvokeRequest request = new InvokeRequest();
        request.setFunctionName(lambdaName);
        LambdaSlotRequest slotRequest = new LambdaSlotRequest(user, uploadId);
        request.setPayload(new Gson().toJson(slotRequest));
        request.setSdkClientExecutionTimeout(slotCreationTimeout);
        InvokeResult response = lambdaClient.invoke(request);
        int statusCode = response.getStatusCode();
        String resultJSON = new String(response.getPayload().array());
        if (statusCode < 200 || statusCode >= 300) {
            String error = response.getFunctionError();
            Log.error("Failed to create slot {}", error);
            throw new SlotCreationException(error, statusCode);
        } else {
            Log.debug("Received a slot {}", resultJSON);
        }
        return new Gson().fromJson(resultJSON, Slot.class);
    }
}
