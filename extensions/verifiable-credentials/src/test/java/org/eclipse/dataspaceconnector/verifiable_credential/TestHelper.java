package org.eclipse.dataspaceconnector.verifiable_credential;

import java.util.Objects;
import java.util.Scanner;

public class TestHelper {

    public static String readFile(String filename) {
        var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        Scanner s = new Scanner(Objects.requireNonNull(stream)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
