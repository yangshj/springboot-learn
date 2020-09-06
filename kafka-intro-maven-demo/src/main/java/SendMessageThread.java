import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;

/**
 * 模拟发送消息
 */
public class SendMessageThread extends Thread {

    @Override
    public void run() {
        Producer<String, String> producer = KafkaCreator.createProducer();
        for(int i=0; i<1000; i++){
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConstants.TOPIC, "hello, Kafka! " + i);
                //send message
                RecordMetadata metadata = producer.send(record).get();
                System.out.println("Record sent to partition " + metadata.partition() + " with offset " + metadata.offset());
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Error in sending record");
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }
}
