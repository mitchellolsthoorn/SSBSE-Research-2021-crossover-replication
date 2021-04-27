/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.geometry.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import org.junit.Assert;

/** Class containing various geometry-related test utilities.
 */
public final class GeometryTestUtils {

    private GeometryTestUtils() {}

    /** Asserts that the given value is positive infinity.
     * @param value
     */
    public static void assertPositiveInfinity(final double value) {
        final String msg = "Expected value to be positive infinity but was " + value;
        Assert.assertTrue(msg, Double.isInfinite(value));
        Assert.assertTrue(msg, value > 0);
    }

    /** Asserts that the given value is negative infinity..
     * @param value
     */
    public static void assertNegativeInfinity(final double value) {
        final String msg = "Expected value to be negative infinity but was " + value;
        Assert.assertTrue(msg, Double.isInfinite(value));
        Assert.assertTrue(msg, value < 0);
    }

    /** Asserts that the given Runnable throws an exception of the given type.
     * @param r the Runnable instance
     * @param exceptionType the expected exception type
     */
    public static void assertThrows(final Runnable r, final Class<?> exceptionType) {
        assertThrows(r, exceptionType, (String) null);
    }

    /** Asserts that the given Runnable throws an exception of the given type. If
     * {@code message} is not null, the exception message is asserted to equal the
     * given value.
     * @param r the Runnable instance
     * @param exceptionType the expected exception type
     * @param message the expected exception message; ignored if null
     */
    public static void assertThrows(final Runnable r, final Class<?> exceptionType, final String message) {
        try {
            r.run();
            Assert.fail("Operation should have thrown an exception");
        } catch (final Exception exc) {
            final Class<?> actualType = exc.getClass();

            Assert.assertTrue("Expected exception of type " + exceptionType.getName() + " but was " + actualType.getName(),
                    exceptionType.isAssignableFrom(actualType));

            if (message != null) {
                Assert.assertEquals(message, exc.getMessage());
            }
        }
    }

    /** Asserts that the given Runnable throws an exception of the given type. If
     * {@code pattern} is not null, the exception message is asserted to match the
     * given regex.
     * @param r the Runnable instance
     * @param exceptionType the expected exception type
     * @param pattern regex pattern to match; ignored if null
     */
    public static void assertThrows(final Runnable r, final Class<?> exceptionType, final Pattern pattern) {
        try {
            r.run();
            Assert.fail("Operation should have thrown an exception");
        } catch (final Exception exc) {
            final Class<?> actualType = exc.getClass();

            Assert.assertTrue("Expected exception of type " + exceptionType.getName() + " but was " + actualType.getName(),
                    exceptionType.isAssignableFrom(actualType));

            if (pattern != null) {
                final String message = exc.getMessage();

                final String err = "Expected exception message to match /" + pattern + "/ but was [" + message + "]";
                Assert.assertTrue(err, pattern.matcher(message).matches());
            }
        }
    }

    /** Assert that a string contains a given substring value.
     * @param substr
     * @param actual
     */
    public static void assertContains(final String substr, final String actual) {
        final String msg = "Expected string to contain [" + substr + "] but was [" + actual + "]";
        Assert.assertTrue(msg, actual.contains(substr));
    }

    /**
     * Serializes and then recovers an object from a byte array. Returns the deserialized object.
     *
     * @param obj  object to serialize and recover
     * @return  the recovered, deserialized object
     */
    public static Object serializeAndRecover(final Object obj) {
        try {
            // serialize the Object
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream so = new ObjectOutputStream(bos);
            so.writeObject(obj);

            // deserialize the Object
            final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            final ObjectInputStream si = new ObjectInputStream(bis);
            return si.readObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
