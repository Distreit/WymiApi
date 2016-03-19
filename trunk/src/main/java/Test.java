import com.fasterxml.jackson.databind.ObjectMapper;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = "ACf6048c95f03953818bd7615fcba2eaf6";
    public static final String AUTH_TOKEN = "f1a5985ead50e4939ad0a51e8a04a667";

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(getRandomString(i));
        }
    }

    public static String getRandomString(int length) {
        final StringBuilder buffer = new StringBuilder(getRandomString());
        while (buffer.length() < length) {
            buffer.append(getRandomString());
        }

        return buffer.substring(0, length);
    }

    public static String getRandomString() {
        return (new BigInteger(130, new Random())).toString(36).replaceAll("[0o1il]", "");
    }

    public static void main1(String[] args) {
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        // Build a filter for the MessageList
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Body", "Your code is 1234" + new DateTime()));
        params.add(new BasicNameValuePair("To", "2111154893556968234"));
        params.add(new BasicNameValuePair("From", "+12674605243"));

        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        Message message = null;
        try {
            message = messageFactory.create(params);
            System.out.println(message.toJSON());
            message.getStatus();
        } catch (TwilioRestException e) {
            System.out.println(JSONConverter.getJSON(e, true));
            e.printStackTrace();
        }
    }
}
