import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hak.wymi.coinbase.CoinbaseResponse;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws JAXBException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonInString = "{\"order\":{\"id\":\"HM2QJO1D\",\"uuid\":\"1a66a742-6441-5fd7-9cea-624e2defb02e\",\"resource_path\":\"/v2/orders/1a66a742-6441-5fd7-9cea-624e2defb02e\",\"metadata\":null,\"created_at\":\"2015-11-29T11:00:33-08:00\",\"status\":\"completed\",\"event\":{\"type\":\"completed\"},\"total_btc\":{\"cents\":10000.0,\"currency_iso\":\"BTC\"},\"total_native\":{\"cents\":100.0,\"currency_iso\":\"USD\"},\"total_payout\":{\"cents\":0.0,\"currency_iso\":\"USD\"},\"custom\":\"ORDER_NUMBER\",\"receive_address\":\"mqFx5oBuEea4RmtYmweoXw3q16oG7STkFs\",\"button\":{\"type\":\"buy_now\",\"subscription\":false,\"repeat\":null,\"name\":\"Points\",\"description\":\"$1.00 = 10,000 AL\",\"id\":\"34dad72e6dcf7941a29d06788b2ca057\",\"uuid\":\"d0a2f1be-a865-5d4d-a794-52d0eaef42c5\",\"resource_path\":\"/v2/checkouts/d0a2f1be-a865-5d4d-a794-52d0eaef42c5\"},\"refund_address\":\"mh2aamK9uxRhqhi8fZRKW8Ez1FNzZ3nepH\",\"transaction\":{\"id\":\"565b4b73e2989546620000af\",\"hash\":null,\"confirmations\":0}}}";
            CoinbaseResponse response = mapper.readValue(jsonInString, CoinbaseResponse.class);
            System.out.println(response);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
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
