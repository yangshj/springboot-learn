
/**
 * һ��������ͬʱֻ�ܱ�һ����������Ϣ
 */
public class TestConsume2 {

    public static void main(String[] args) throws InterruptedException {
        consumeMessage();
        Thread.sleep(100000);
    }



    static void consumeMessage() {
        ConsumeMessageThread t2 = new ConsumeMessageThread();
        t2.setName("Thread2");
        t2.start();
    }
}
