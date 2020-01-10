package com.kubra.prepay.messaging.sqs;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsQueueListener {

  @SqsListener(value = "${sqs.queueName}")
  public void mapping(Dto dto) {
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println("Field1: " + dto.getField1());
    System.out.println("Field2: " + dto.getField2());
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
  }
}

