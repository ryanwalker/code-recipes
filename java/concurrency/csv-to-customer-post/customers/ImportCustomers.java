package subscription.management.backend.tests.chargebee.customers;

import com.google.common.io.Resources;
import com.opencsv.CSVReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ImportCustomers {

  private static final String KSM_BASE_URL = "";
  private static final String API_KEY = "";

  private static BufferedWriter successBufferedWriter = null;
  private static BufferedWriter failureBufferedWriter = null;
  private static CSVReader csvReader = null;

  BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
  ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 1l, TimeUnit.MINUTES,
      queue);
  
  @BeforeAll
  public static void setupOnce() throws IOException {
    RestAssured.baseURI = KSM_BASE_URL + "/admin/customers";
    
    URL successFileUrl = Resources.getResource("success.txt");
    URL failureFileUrl = Resources.getResource("failure.txt");

    FileWriter successFileWriter = new FileWriter(successFileUrl.getPath(), true);
    FileWriter failureFileWriter = new FileWriter(failureFileUrl.getPath(), true);

    successBufferedWriter = new BufferedWriter(successFileWriter);
    failureBufferedWriter = new BufferedWriter(failureFileWriter);

    URL customersFileUrl = Resources.getResource("customers.csv");

    FileReader fileReader = new FileReader(customersFileUrl.getPath());
    csvReader = new CSVReader(fileReader);

  }

  @Test
  public void importCustomers() throws IOException, JSONException {
    executor.prestartAllCoreThreads();
    String[] row = csvReader.readNext();
    int count = 1;
    while ((row != null)) {
      Customer customer = new Customer.CustomerBuilder()
          .id(row[0])
          .excludeFromUsageGathering(Boolean.parseBoolean(row[1]))
          .excludeFromUsageGateringInvoiceCreation(Boolean.parseBoolean(row[2]))
          .build();
      
      postCustomer(customer, count++);

      row = csvReader.readNext();
    }
    executor.shutdown();
    try {
      executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void postCustomer(Customer customer, int count) throws JSONException, IOException {
    boolean offer = queue.offer(new PostCustomerToKSMTask(count, customer, API_KEY));

//    RequestSpecification request = RestAssured.given();
//    request.header("Content-Type", "application/json");
//    request.header("X-SM-ADMIN", API_KEY);
//
//    request.body(customer.toJsonString());
//
//    Response response = request.post();
//
//    if (response.getStatusCode() == 200 ) {
//      System.out.println("GOOD: " + count + " " + customer);
//      successBufferedWriter.write(customer.getId());
//      successBufferedWriter.newLine();
//      successBufferedWriter.flush();
//    } else {
//      System.out.println(count + " " + customer + " " + response.body().prettyPrint());
//      failureBufferedWriter.write(customer.getId());
//      failureBufferedWriter.newLine();
//      failureBufferedWriter.flush();
//    }
  }

}
