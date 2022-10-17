
import Exceptions.InvalidCollectionNameException;
import Threads.MainThread;


public class App  {

    public static void main(String... args) throws InvalidCollectionNameException {

        var thread=new MainThread();
        thread.run();

    }
}
