package boilerplate.stack.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Config {

    @Bean
    public Queue queue() {
        return new Queue("default-queue");
    }

    @Profile("consumer")
    private static class ConsumerConfig {

        @Bean
        public Consumer consumer1() {
            return new Consumer(1);
        }

        @Bean
        public Consumer consumer2() {
            return new Consumer(2);
        }

    }

    @Profile("producer")
    @Bean
    public Producer producer() {
        return new Producer();
    }

}