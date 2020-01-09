package com.kubra.prepay.messaging.sqs;

import java.util.Map;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

@Component
public class SqsQueueListener {

  @MessageMapping("${sqs.queueName}")
  public void mapping(String json, @Headers Map<String, String> sqsHeaders) {
    System.out.println(sqsHeaders.get("yourHeaderName"));
    System.out.println("JSON Message: " + json);
  }
}

