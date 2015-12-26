import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hak.wymi.coinbase.pojos.CoinbaseOrder;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import com.hak.wymi.utility.passwordstrength.PasswordStrengthChecker;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws JAXBException {
        System.out.println(JSONConverter.getJSON(PasswordStrengthChecker.getStrengthResults("hello WORorLd!"), true));
    }

    public static void mai1n(String[] args) throws JAXBException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = "{\"id\":\"2fcfed2e-76ae-5a0d-b6ca-39dad48f88a8\",\"type\":\"wallet:orders:paid\",\"data\":{\"resource\":{\"id\":\"7c1d2bed-8466-5e3c-8d6a-40a31cacf8f2\",\"code\":\"NWQ91NEY\",\"type\":\"order\",\"name\":\"Points\",\"description\":\"$1.00 = 10,000 AL\",\"amount\":{\"amount\":\"1.00\",\"currency\":\"USD\"},\"receipt_url\":\"https://www.coinbase.com/orders/6be3cf17d21e115496334650f08bd556/receipt\",\"resource\":\"order\",\"resource_path\":\"/v2/orders/7c1d2bed-8466-5e3c-8d6a-40a31cacf8f2\",\"status\":\"paid\",\"bitcoin_amount\":{\"amount\":\"0.00010000\",\"currency\":\"BTC\"},\"payout_amount\":null,\"bitcoin_address\":\"mjHmZt3jaDrzKumWfq6Coa9cp1iP9XESwj\",\"refund_address\":\"mpgaw11Bzq7GJ5C4rBPRGU89GRS8FrfJAa\",\"bitcoin_uri\":\"bitcoin:mjHmZt3jaDrzKumWfq6Coa9cp1iP9XESwj?amount=0.0001\\u0026r=https://sandbox.coinbase.com/r/56644016192f316de700002c\",\"notifications_url\":null,\"paid_at\":\"2015-12-06T14:16:45Z\",\"mispaid_at\":null,\"expires_at\":\"2015-12-06T14:18:02Z\",\"metadata\":{\"custom\":\"daveTest\"},\"created_at\":\"2015-12-06T14:03:02Z\",\"updated_at\":\"2015-12-06T14:16:45Z\",\"customer_info\":null,\"transaction\":{\"id\":\"346c64e3-6ab9-5c82-8e54-5e83d4245eab\",\"resource\":\"transaction\",\"resource_path\":\"/v2/accounts/225477ee-93d2-542f-8ca5-1db25f08e32e/transactions/346c64e3-6ab9-5c82-8e54-5e83d4245eab\"},\"mispayments\":[],\"refunds\":[]}},\"user\":{\"id\":\"d5885b04-1f80-5de5-aebe-fba2abba8cb5\",\"resource\":\"user\",\"resource_path\":\"/v2/users/d5885b04-1f80-5de5-aebe-fba2abba8cb5\"},\"account\":{\"id\":\"225477ee-93d2-542f-8ca5-1db25f08e32e\",\"resource\":\"account\",\"resource_path\":\"/v2/accounts/225477ee-93d2-542f-8ca5-1db25f08e32e\"},\"delivery_attempts\":0,\"created_at\":\"2015-12-06T14:16:46Z\",\"resource\":\"notification\",\"resource_path\":\"/v2/notifications/2fcfed2e-76ae-5a0d-b6ca-39dad48f88a8\"}";
            JsonNode jsonResposne = mapper.readTree(jsonInString);
            JsonNode dataNode = jsonResposne.get("data");
            if (dataNode != null) {
                JsonNode resourceNode = dataNode.get("resource");
                if (resourceNode != null) {
                    try {
                        CoinbaseOrder order = mapper.treeToValue(resourceNode, CoinbaseOrder.class);
                        System.out.println(order);
                    } catch (JsonProcessingException e) {
                        System.out.println(e);
                    }
                } else {
                    System.out.println("a");
                }
            } else {
                System.out.println("b");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class User {

        private String name;
        private int age;
        private List<String> messages;

        public User() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }
    }
}
