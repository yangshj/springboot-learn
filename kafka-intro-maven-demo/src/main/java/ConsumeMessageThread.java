import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;

/**
 * 模拟消费
 */
public class ConsumeMessageThread extends Thread {
    @Override
    public void run() {
        Consumer<String, String> consumer = KafkaCreator.createConsumer();
        // 循环消费消息
        while (true) {
            //subscribe topic and consume message
            consumer.subscribe(Collections.singletonList(KafkaConstants.TOPIC));
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(10));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.println(this.getName() + " Consumer consume message:" + consumerRecord.value());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
