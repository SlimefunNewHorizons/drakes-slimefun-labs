package org.apache.commons.lang;

import java.util.Collection;
import java.util.Map;

/**
 * Compatibility shim for legacy Commons Lang 2 imports backed by Commons Lang 3.
 */
public final class Validate {

    private Validate() {
    }

    public static void isTrue(boolean expression) {
        org.apache.commons.lang3.Validate.isTrue(expression);
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        org.apache.commons.lang3.Validate.isTrue(expression, message, values);
    }

    /**
     * Commons Lang 3's notNull/notEmpty throw NullPointerException on a null
     * argument; legacy Commons Lang 2 always threw IllegalArgumentException.
     * Callers (including this addon's own tests) still expect the 2.x
     * contract, so the null checks below are done here instead of delegating
     * straight to lang3.
     */
    public static <T> T notNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("The validated object is null");
        }
        return object;
    }

    /** Preserve the void-returning Commons Lang 2 ABI used by ACF. */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return object;
    }

    public static <T> T[] notEmpty(T[] array, String message, Object... values) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return array;
    }

    public static <T extends Collection<?>> T notEmpty(T collection, String message, Object... values) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return collection;
    }

    public static <T extends Map<?, ?>> T notEmpty(T map, String message, Object... values) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return map;
    }

    public static <T extends CharSequence> T notEmpty(T chars, String message, Object... values) {
        if (chars == null || chars.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    public static <T> T[] noNullElements(T[] array, String message, Object... values) {
        return org.apache.commons.lang3.Validate.noNullElements(array, message, values);
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable, String message, Object... values) {
        return org.apache.commons.lang3.Validate.noNullElements(iterable, message, values);
    }

    public static void notEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("The validated array is empty");
        }
    }

    public static void notEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("The validated collection is empty");
        }
    }

    public static void notEmpty(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("The validated map is empty");
        }
    }

    public static void notEmpty(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("The validated string is empty");
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String string, String message) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void noNullElements(Object[] array) {
        if (array == null) {
            throw new IllegalArgumentException("The validated array is null");
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException("The validated array contains null element at index: " + i);
            }
        }
    }

    public static void noNullElements(Collection<?> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("The validated collection is null");
        }
        int i = 0;
        for (Object obj : collection) {
            if (obj == null) {
                throw new IllegalArgumentException("The validated collection contains null element at index: " + i);
            }
            i++;
        }
    }

    public static void noNullElements(Object[] array, String message) {
        if (array == null) {
            throw new IllegalArgumentException(message);
        }
        for (Object obj : array) {
            if (obj == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static void noNullElements(Collection<?> collection, String message) {
        if (collection == null) {
            throw new IllegalArgumentException(message);
        }
        for (Object obj : collection) {
            if (obj == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }

}
