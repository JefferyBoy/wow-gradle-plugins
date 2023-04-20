package com.github.jeffery.aspectj;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Pattern pattern = Pattern.compile("^.+\\u0024AjcClosure\\d+$");
        Pattern pointCutPattern = Pattern.compile("\\u0024AjcClosure\\d+\\.class$");

        String text = "23251$AjcClosure223242.class";
        text = text.replaceAll(pointCutPattern.pattern(), "");
        System.out.println(text);
        boolean ok = pointCutPattern.matcher(text).matches();
        System.out.println("ok = " + ok);
    }
}