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
package org.apache.commons.geometry.spherical.twod;

import org.apache.commons.geometry.core.GeometryTestUtils;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import org.apache.commons.geometry.spherical.SphericalTestUtils;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class Transform2STest {

    private static final double TEST_EPS = 1e-10;

    @Test
    public void testIdentity() {
        // act
        final Transform2S t = Transform2S.identity();

        // assert
        Assert.assertTrue(t.preservesOrientation());
        Assert.assertArrayEquals(new double[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0
        }, t.getEuclideanTransform().toArray(), 0);

        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_I, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_K, t.apply(Point2S.PLUS_K), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testRotation() {
        // arrange
        final Transform2S aroundPole = Transform2S.createRotation(Point2S.PLUS_K, PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S aroundX = Transform2S.createRotation(Vector3D.Unit.PLUS_X, -PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S aroundY = Transform2S.createRotation(
                QuaternionRotation.fromAxisAngle(Vector3D.Unit.PLUS_Y, PlaneAngleRadians.PI_OVER_TWO));

        // act/assert
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, aroundPole.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_I, aroundPole.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, aroundPole.apply(Point2S.PLUS_K), TEST_EPS);
        checkInverse(aroundPole);

        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_I, aroundX.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.MINUS_K, aroundX.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, aroundX.apply(Point2S.PLUS_K), TEST_EPS);
        checkInverse(aroundX);

        SphericalTestUtils.assertPointsEq(Point2S.MINUS_K, aroundY.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, aroundY.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_I, aroundY.apply(Point2S.PLUS_K), TEST_EPS);
        checkInverse(aroundY);
    }

    @Test
    public void testMultipleRotations() {
        // act
        final Transform2S t = Transform2S.identity()
                .rotate(Point2S.PLUS_K, PlaneAngleRadians.PI_OVER_TWO)
                .rotate(Vector3D.Unit.PLUS_X, -PlaneAngleRadians.PI_OVER_TWO)
                .rotate(QuaternionRotation.fromAxisAngle(Vector3D.Unit.PLUS_Y, PlaneAngleRadians.PI_OVER_TWO));

        // assert
        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_I, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, t.apply(Point2S.PLUS_K), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testMultiply() {
        // act
        final Transform2S t = Transform2S.identity()
                .multiply(Transform2S.createRotation(Point2S.PLUS_K, PlaneAngleRadians.PI_OVER_TWO))
                .multiply(Transform2S.createRotation(Point2S.PLUS_J, PlaneAngleRadians.PI_OVER_TWO));

        // assert
        SphericalTestUtils.assertPointsEq(Point2S.MINUS_K, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_I, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, t.apply(Point2S.PLUS_K), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testPremultiply() {
        // act
        final Transform2S t = Transform2S.identity()
                .premultiply(Transform2S.createRotation(Point2S.PLUS_K, PlaneAngleRadians.PI_OVER_TWO))
                .premultiply(Transform2S.createRotation(Point2S.PLUS_J, PlaneAngleRadians.PI_OVER_TWO));

        // assert
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_I, t.apply(Point2S.PLUS_K), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testReflection_point() {
        // arrange
        final Point2S a = Point2S.of(1, 1);
        final Point2S b = Point2S.of(-1, 1);

        final Point2S c = Point2S.of(1, PlaneAngleRadians.PI - 1);
        final Point2S d = Point2S.of(-1, PlaneAngleRadians.PI - 1);

        // act
        final Transform2S t = Transform2S.createReflection(Point2S.PLUS_I);

        // assert
        Assert.assertFalse(t.preservesOrientation());

        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_I, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_J, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, t.apply(Point2S.PLUS_K), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI - 1, 1), t.apply(a), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI + 1, 1), t.apply(b), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI - 1, PlaneAngleRadians.PI - 1), t.apply(c), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI + 1, PlaneAngleRadians.PI - 1), t.apply(d), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testReflection_vector() {
        // arrange
        final Point2S a = Point2S.of(1, 1);
        final Point2S b = Point2S.of(-1, 1);

        final Point2S c = Point2S.of(1, PlaneAngleRadians.PI - 1);
        final Point2S d = Point2S.of(-1, PlaneAngleRadians.PI - 1);

        // act
        final Transform2S t = Transform2S.createReflection(Vector3D.Unit.PLUS_Y);

        // assert
        Assert.assertFalse(t.preservesOrientation());

        SphericalTestUtils.assertPointsEqual(Point2S.PLUS_I, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_J, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, t.apply(Point2S.PLUS_K), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(b, t.apply(a), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(a, t.apply(b), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(d, t.apply(c), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(c, t.apply(d), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testDoubleReflection() {
        // arrange
        final Point2S a = Point2S.of(1, 1);
        final Point2S b = Point2S.of(-1, 1);

        final Point2S c = Point2S.of(1, PlaneAngleRadians.PI - 1);
        final Point2S d = Point2S.of(-1, PlaneAngleRadians.PI - 1);

        // act
        final Transform2S t = Transform2S.identity()
                .reflect(Point2S.PLUS_I)
                .reflect(Vector3D.Unit.PLUS_Y);

        // assert
        Assert.assertTrue(t.preservesOrientation());

        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_I, t.apply(Point2S.PLUS_I), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.MINUS_J, t.apply(Point2S.PLUS_J), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.PLUS_K, t.apply(Point2S.PLUS_K), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI + 1, 1), t.apply(a), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI - 1, 1), t.apply(b), TEST_EPS);

        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI + 1, PlaneAngleRadians.PI - 1), t.apply(c), TEST_EPS);
        SphericalTestUtils.assertPointsEqual(Point2S.of(PlaneAngleRadians.PI - 1,  PlaneAngleRadians.PI - 1), t.apply(d), TEST_EPS);

        checkInverse(t);
    }

    @Test
    public void testHashcode() {
        // arrange
        final Transform2S a = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S b = Transform2S.createRotation(Point2S.PLUS_J, PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S c = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI);
        final Transform2S d = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI_OVER_TWO);

        // act
        final int hash = a.hashCode();

        // assert
        Assert.assertEquals(hash, a.hashCode());

        Assert.assertNotEquals(hash, b.hashCode());
        Assert.assertNotEquals(hash, c.hashCode());

        Assert.assertEquals(hash, d.hashCode());
    }

    @Test
    public void testEquals() {
        // arrange
        final Transform2S a = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S b = Transform2S.createRotation(Point2S.PLUS_J, PlaneAngleRadians.PI_OVER_TWO);
        final Transform2S c = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI);
        final Transform2S d = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI_OVER_TWO);

        // act/assert
        Assert.assertEquals(a, a);

        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals(new Object()));

        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(a, c);

        Assert.assertEquals(a, d);
        Assert.assertEquals(d, a);
    }

    @Test
    public void testToString() {
        // arrange
        final Transform2S t = Transform2S.identity();

        // act
        final String str = t.toString();

        // assert
        GeometryTestUtils.assertContains("Transform2S", str);
        GeometryTestUtils.assertContains("euclideanTransform= [", str);
    }

    private static void checkInverse(final Transform2S t) {
        final Transform2S inv = t.inverse();

        // test non-pole points
        for (double az = -PlaneAngleRadians.TWO_PI; az <= 2 * PlaneAngleRadians.TWO_PI; az += 0.2) {
            for (double p = 0.1; p < PlaneAngleRadians.PI; p += 0.2) {

                final Point2S pt = Point2S.of(az, p);

                SphericalTestUtils.assertPointsEqual(pt, inv.apply(t.apply(pt)), TEST_EPS);
                SphericalTestUtils.assertPointsEqual(pt, t.apply(inv.apply(pt)), TEST_EPS);
            }
        }

        // test poles
        SphericalTestUtils.assertVectorsEqual(Vector3D.Unit.PLUS_Z,
                inv.apply(t.apply(Point2S.of(1, 0))).getVector(), TEST_EPS);
        SphericalTestUtils.assertVectorsEqual(Vector3D.Unit.PLUS_Z,
                t.apply(inv.apply(Point2S.of(-1, 0))).getVector(), TEST_EPS);

        SphericalTestUtils.assertVectorsEqual(Vector3D.Unit.MINUS_Z,
                inv.apply(t.apply(Point2S.of(1, PlaneAngleRadians.PI))).getVector(), TEST_EPS);
        SphericalTestUtils.assertVectorsEqual(Vector3D.Unit.MINUS_Z,
                t.apply(inv.apply(Point2S.of(-1, PlaneAngleRadians.PI))).getVector(), TEST_EPS);
    }
}
