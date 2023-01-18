package subscription.management.backend.tests.chargebee.customers;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONException;
import org.json.JSONObject;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Customer {

  private String id;
  private boolean excludeFromUsageGathering;
  private boolean excludeFromUsageGateringInvoiceCreation;

  public String toJsonString() throws JSONException {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", id);
    jsonObject.put(
        "exclude_from_usage_gathering",
        excludeFromUsageGathering);
    jsonObject.put(
        "exclude_from_usage_gather_invoice_creation",
        excludeFromUsageGateringInvoiceCreation);

    return jsonObject.toString();
  }
}
