
public class TestSend {

    public static void main(String[] args) throws InterruptedException {
        sendMessage();
        Thread.sleep(100000);
    }

    static void sendMessage() {
       SendMessageThread t = new SendMessageThread();
       t.start();
    }


}
