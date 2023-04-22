import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;

import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle.header;

public class Main {
    public static void main(String[] args) {
        LogReader lr = new LogReader();
        lr.getLastKnownPosition();
        lr.notificationTest();

    }
}