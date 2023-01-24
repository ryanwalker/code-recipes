package subscription.management.backend.tests.chargebee.customers;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;

public class PostCustomerToKSMTask implements Runnable {

  private int count;
  private Customer customer;
  private String apiKey;

  public PostCustomerToKSMTask(int count, Customer customer, String apiKey) {
    this.count = count;
    this.customer = customer;
    this.apiKey = apiKey;
  }

  @Override
  public void run() {
    RequestSpecification request = RestAssured.given();
    request.header("Content-Type", "application/json");
    request.header("X-SM-ADMIN", apiKey);

    try {
      request.body(customer.toJsonString());
    } catch (JSONException e) {
      System.out.println("BAD: " + count + " " + customer + " JSON problem");
      return;
    }

    Response response = request.post();

    if (response.getStatusCode() == 200 ) {
      System.out.print(count + " ");
      if (count % 100 == 0) {
        System.out.println(".");
      }
      System.out.flush(); // had to do this for print (not println) to show in logs
    } else {
      System.out.println("\nBAD: " + count + " " + customer + " " + response.body().prettyPrint());
    }
  }
}
