package helloworld;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Event, String> {

    public String handleRequest(final Event event, final Context context) {
        AWSIotData iotData = AWSIotDataClientBuilder.standard().build();

        String payload = getPayload(event.tags);

        UpdateThingShadowRequest updateThingShadowRequest  =
                new UpdateThingShadowRequest()
                        .withThingName(event.device)
                        .withPayload(ByteBuffer.wrap(payload.getBytes()));

        UpdateThingShadowResult result = iotData.updateThingShadow(updateThingShadowRequest);
        byte[] bytes = new byte[result.getPayload().remaining()];
        result.getPayload().get(bytes);
        String output = new String(bytes);

        return output;
    }

    private String getPayload(ArrayList<Tag> tags) {
        String tagstr = "";
        for (int i=0; i < tags.size(); i++) {
            if (i !=  0) tagstr += ", ";
            tagstr += String.format("\"%s\" : \"%s\"", tags.get(i).tagName, tags.get(i).tagValue);
        }
        return String.format("{ \"state\": { \"desired\": { %s } } }", tagstr);
    }
}

class Event {
    public String device;
    public ArrayList<Tag> tags;

    public Event() {
        tags = new ArrayList<Tag>();
    }
}

class Tag {
    public String tagName;
    public String tagValue;

    @JsonCreator
    public Tag() {
    }

    public Tag(String n, String v) {
        tagName = n;
        tagValue = v;
    }
}

