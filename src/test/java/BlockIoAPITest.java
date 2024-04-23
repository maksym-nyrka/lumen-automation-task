import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BlockIoAPITest {
    private String apiKey;
    private String address;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://block.io/api/v2";

        apiKey = "a18c-7e80-80ac-fe53";
        address = "2N3P9jAr9kbiNpovFDr6Po3xDo6NZqhot4b";
    }

    @Test(enabled = false)
    public void testGetNewAddress() {
        String label = "myaddress4";

        Response response = given()
                .queryParam("api_key", apiKey)
                .queryParam("label", label)
                .when()
                .get("/get_new_address")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.network", equalTo("BTCTEST"))
                .body("data.user_id", notNullValue())
                .body("data.address", notNullValue())
                .body("data.label", equalTo(label))
                .extract()
                .response();

        System.out.println("Response Body: " + response.asString());
    }

    @Test
    public void testGetAddressBalance() {
        double balanceBefore = getBalance(address);

        double transactionAmount = 0.00001;
        makeTransaction(transactionAmount);

        double balanceAfter = getBalance(address);

        Assert.assertEquals(balanceAfter, balanceBefore + transactionAmount,
                "Balance after should be equal to balanceBefore + transactionAmount");
    }

    @Test
    public void testGetTransactions() {
        Response response = given()
                .queryParam("api_key", apiKey)
                .queryParam("type", "received")
                .queryParam("addresses", address)
                .when()
                .get("/get_transactions")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.network", equalTo("BTCTEST"))
                .body("data.txs.size()", greaterThan(0))
                .body("data.txs[0].txid", notNullValue())
                .body("data.txs[0].from_green_address", equalTo(false))
                .body("data.txs[0].time", notNullValue())
                .body("data.txs[0].confirmations", greaterThan(0))
                .body("data.txs[0].amounts_received.size()", greaterThan(0))
                .body("data.txs[0].amounts_received[0].recipient", equalTo(address))
                .body("data.txs[0].amounts_received[0].amount", notNullValue())
                .body("data.txs[0].senders.size()", greaterThan(0))
                .body("data.txs[0].confidence", equalTo(1.0f))
                .extract()
                .response();

        Map<String, ?> lastTransaction = response.path("data.txs.find { it.amounts_received[0].recipient == '" + address + "'}");

        System.out.println("Last Transaction Details:");
        lastTransaction.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    private void makeTransaction(double transactionAmount) {
        //here transaction for transactionAmount is made manually
    }

    private Double getBalance(String address) {
        Response response = given()
                .queryParam("api_key", apiKey)
                .queryParam("addresses", address)
                .when()
                .get("/get_address_balance")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.network", equalTo("BTCTEST"))
                .body("data.balances[0].address", equalTo(address))
                .extract()
                .response();

        String balanceStr = response.path("data.balances[0].available_balance");
        System.out.println("Account Balance: " + balanceStr);

        return Double.parseDouble(balanceStr);
    }

}
