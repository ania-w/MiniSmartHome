
import Exceptions.InvalidCollectionNameException;
import Threads.MainThread;


public class App  {

    public static void main(String... args) throws InvalidCollectionNameException {

        // TODO: schedule updates
        // TODO: testy
        var thread=new MainThread();
        thread.run();

    }
}
