package com.github.jeffery.aspectj

import org.junit.Test

import java.util.regex.Pattern

class MyTest {
    @Test
    void test() {
        Pattern pattern = Pattern.compile("^.+\\u0024AjcClosure\\d+\$");
        boolean ok = pattern.matcher("23251$AjcClosure223242").matches();
        System.out.println("ok = " + ok);
    }
}
