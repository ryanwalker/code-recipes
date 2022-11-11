package dates;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Dates {
  
  
  public void epochTimestampLongToZonedDateTime() {
    long epochTimestamp = ZonedDateTime.now().toEpochSecond();
    Instant instant = Instant.ofEpochMilli(epochTimestamp);
    ZonedDateTime createAt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
  }
  
  public void zonedDateTimeToEpochSecondLong() {
    long epochTimestamp = ZonedDateTime.now().toEpochSecond();
  }

}
