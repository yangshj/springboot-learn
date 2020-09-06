import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;

/**
 * ģ������
 */
public class ConsumeMessageThread extends Thread {
    @Override
    public void run() {
        Consumer<String, String> consumer = KafkaCreator.createConsumer();
        // ѭ��������Ϣ
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
