import java.util.Collections;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;

@Configuration
public class SqsConfig {

  @Bean
  public QueueMessageHandlerFactory queueMessageHandlerFactory() {
    final MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
    messageConverter.setStrictContentTypeMatch(false);

    final QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
    factory.setArgumentResolvers(
        Collections.singletonList(new PayloadMethodArgumentResolver(messageConverter)));

    return factory;
  }
}
