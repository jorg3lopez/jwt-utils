package jwt.utils.external.cli;

import java.util.Arrays;

public class MainCli implements Runnable {

    public static void main(String[] args) {
        Arrays.stream(args).forEach(System.out::println);
    }

    @Override
    public void run() {

    }
}
