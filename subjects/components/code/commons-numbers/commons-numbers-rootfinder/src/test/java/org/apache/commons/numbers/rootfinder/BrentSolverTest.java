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
package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link BrentSolver} class.
 */
public class BrentSolverTest {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;
    private static final double DEFAULT_RELATIVE_ACCURACY = 1e-14;
    private static final double DEFAULT_FUNCTION_ACCURACY = 1e-15;

    @Test
    public void testSinZero() {
        // The sinus function is behaved well around the root at pi. The second
        // order derivative is zero, which means linar approximating methods will
        // still converge quadratically.
        final DoubleUnaryOperator func = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        double result;
        MonitoredFunction f;

        // Somewhat benign interval. The function is monotonous.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 3, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 7);

        // Larger and somewhat less benign interval. The function is grows first.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 1, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 8);
    }

    @Test
    public void testQuinticZero() {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // Around the root of 0 the function is well behaved, with a second derivative
        // of zero a 0.
        // The other roots are less well to find, in particular the root at 1, because
        // the function grows fast for x>1.
        // The function has extrema (first derivative is zero) at 0.27195613 and 0.82221643,
        // intervals containing these values are harder for the solvers.
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        double result;
        MonitoredFunction f;

        // Symmetric bracket around 0. Test whether solvers can handle hitting
        // the root in the first iteration.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.2, 0.2);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 3);

        // 1 iterations on i586 JDK 1.4.1.
        // Asymmetric bracket around 0. Contains extremum.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.1, 0.3);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);
        // 5 iterations on i586 JDK 1.4.1.
        Assertions.assertTrue(f.getCallsCount() <= 7);

        // Large bracket around 0. Contains two extrema.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.3, 0.45);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);
        // 6 iterations on i586 JDK 1.4.1.
        Assertions.assertTrue(f.getCallsCount() <= 8);

        // Benign bracket around 0.5, function is monotonous.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.3, 0.7);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);
        // 6 iterations on i586 JDK 1.4.1.
        Assertions.assertTrue(f.getCallsCount() <= 9);

        // Less benign bracket around 0.5, contains one extremum.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.2, 0.6);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 10);

        // Large, less benign bracket around 0.5, contains both extrema.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.05, 0.95);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);

        // Relatively benign bracket around 1, function is monotonous. Fast growth for x>1
        // is still a problem.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.85, 1.25);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);

        // Less benign bracket around 1 with extremum.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.8, 1.2);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);

        // Large bracket around 1. Monotonous.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.85, 1.75);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 13);

        // Large bracket around 1. Interval contains extremum.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.55, 1.45);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 10);
    }

    @Test
    public void testTooManyCalls() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        double result;
        MonitoredFunction f;

        // Very large bracket around 1 for testing fast growth behavior.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.85, 5);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 15);

        try {
            f = new MonitoredFunction(func, 10);
            result = solver.findRoot(f, 0.85, 5);
            Assertions.fail("Expected too many calls condition");
        } catch (IllegalStateException ex) {
            // Expected.
            // Ensure expected error condition.
            Assertions.assertNotEquals(-1, ex.getMessage().indexOf("too many calls"));
        }
    }

    @Test
    public void testRootEndpoints() {
        final DoubleUnaryOperator f = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        // Endpoint is root.
        double result = solver.findRoot(f, Math.PI, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, 3, Math.PI);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, Math.PI, 3.5, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, 3, 3.07, Math.PI);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
    }

    @Test
    public void testBadEndpoints() {
        final DoubleUnaryOperator f = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);
        try {  // Bad interval.
            solver.findRoot(f, 1, -1);
            Assertions.fail("Expecting bad interval condition");
        } catch (SolverException ex) {
            // Ensure expected error condition.
            Assertions.assertNotEquals(-1, ex.getMessage().indexOf(" > "));
        }
        try {  // No bracketing.
            solver.findRoot(f, 1, 1.5);
            Assertions.fail("Expecting non-bracketing condition");
        } catch (SolverException ex) {
            // Ensure expected error condition.
            Assertions.assertNotEquals(-1, ex.getMessage().indexOf("No bracketing"));
        }
        try {  // No bracketing.
            solver.findRoot(f, 1, 1.2, 1.5);
            Assertions.fail("Expecting non-bracketing condition");
        } catch (SolverException ex) {
            // Ensure expected error condition.
            Assertions.assertNotEquals(-1, ex.getMessage().indexOf("No bracketing"));
        }
    }

    @Test
    public void testBadInitialGuess() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        try {
            // Invalid guess (it *is* a root, but outside of the range).
            double result = solver.findRoot(func, 0.0, 7.0, 0.6);
            Assertions.fail("an out of range condition was expected");
        } catch (SolverException ex) {
            // Ensure expected error condition.
            Assertions.assertNotEquals(-1, ex.getMessage().indexOf("out of range"));
        }
    }

    @Test
    public void testInitialGuess() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);
        double result;
        MonitoredFunction f;

        // No guess.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        final int referenceCallsCount = f.getCallsCount();
        Assertions.assertTrue(referenceCallsCount >= 13);

        // Bad guess.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 0.61, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() > referenceCallsCount);

        // Good guess.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 0.9999990001, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() < referenceCallsCount);

        // Perfect guess.
        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 1.0, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertEquals(1, f.getCallsCount());
    }
}
