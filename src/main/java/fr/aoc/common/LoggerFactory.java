package fr.aoc.common;

import org.slf4j.Logger;

public class LoggerFactory {

    public static Logger getLogger() {
        return org.slf4j.LoggerFactory.getLogger(getCallerClassname());
    }

    public static String getCallerClassname() {
        var stack = Thread.currentThread().getStackTrace();
        var factoryClassName = LoggerFactory.class.getName();

        for (int i = 3; i < stack.length; i++) {
            if (!factoryClassName.equals(stack[i].getClassName())) return stack[i].getClassName();
        }

        throw new UnsupportedOperationException();
    }

}
