package com.kubra.prepay.messaging.sqs;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "sqs", ignoreUnknownFields = false)
public class SQSProperties {
  @NotBlank private String queueName;
}

