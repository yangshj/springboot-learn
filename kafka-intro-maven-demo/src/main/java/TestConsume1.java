/**
 * 一个分区，同时只能被一个消费者消息
 */
public class TestConsume1 {

    public static void main(String[] args) throws InterruptedException {
        consumeMessage();
        Thread.sleep(100000);
    }


    static void consumeMessage() {
        ConsumeMessageThread t1 = new ConsumeMessageThread();
        t1.setName("Thread1");
        t1.start();
    }
}
