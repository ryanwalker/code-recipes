# Spring Cloud SQS

You'll need the following dependency:

```
# Gradle
implementation "org.springframework.cloud:spring-cloud-starter-aws-messaging:${springBootVersion}"

# Maven
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-aws-messaging</artifactId>
  <version>2.1.2.RELEASE</version>
</dependency>
```

You'll need to configure you SQS queue in AWS:

- Use standard queue vs FIFO queue (most likely)
- Make sure to set `Receive Message Wait Time` to 20s to enable long polling (and save $$$).

![image](https://user-images.githubusercontent.com/2091062/72176302-565b6880-339b-11ea-93e0-cb874821d7b5.png)
