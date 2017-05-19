package workwithdatabase;

public class Utils {

    public static void catchAll(Action action) {
        try {
            action.call();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

}
