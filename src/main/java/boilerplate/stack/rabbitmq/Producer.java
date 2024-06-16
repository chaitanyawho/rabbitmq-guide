package boilerplate.stack.rabbitmq;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class Producer {

    AtomicInteger count = new AtomicInteger(0);
    @Value("${submission.count}")
    private int limit;

    @Autowired
    private RabbitTemplate template;
    @Autowired
    private Queue queue;

    // produces one submission every second
    @Scheduled(initialDelay = 1000, fixedRateString = "${submission.interval}")
    public void send() {
        if (count.get() >= limit) {
            System.exit(0);
        }

        String message = "Submission#" + count.incrementAndGet()
            // a random number of seconds that this message will take to process (grade)
            + "..." + Math.random() * 120;

        template.convertAndSend(queue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");

    }
}
