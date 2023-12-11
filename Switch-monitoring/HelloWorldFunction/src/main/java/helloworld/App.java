package helloworld;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class App implements RequestHandler<Object, String> {

    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        String json = ""+input;
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonElement state = element.getAsJsonObject().get("state");
        JsonElement reported = state.getAsJsonObject().get("reported");
        JsonElement Switch = state.getAsJsonObject().get("Switch");
        String flag_info = reported.getAsJsonObject().get("flag").getAsString();
        double flag = Double.valueOf(flag_info);

        final String AccessKey="AKIARIF2RYSED52GIXFA";
        final String SecretKey="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";
        final String topicArn="arn:aws:sns:ap-northeast-2:086290580616:Switch_INFO_topic";

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AccessKey, SecretKey);
        AmazonSNS sns = AmazonSNSClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials( new AWSStaticCredentialsProvider(awsCreds) )
                .build();

        final String msg = "Your Switch is turn " + Switch;
        final String subject = "Switch Changed";
        if (flag == 1) {
            PublishRequest publishRequest = new PublishRequest(topicArn, msg, subject);
            PublishResult publishResponse = sns.publish(publishRequest);
        }

        return subject+ Switch;
    }
}
