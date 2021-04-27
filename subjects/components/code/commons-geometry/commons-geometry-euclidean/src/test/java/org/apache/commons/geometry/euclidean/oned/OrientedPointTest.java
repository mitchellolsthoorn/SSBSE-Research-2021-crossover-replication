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
package org.apache.commons.geometry.euclidean.oned;

import java.util.List;

import org.apache.commons.geometry.core.GeometryTestUtils;
import org.apache.commons.geometry.core.RegionLocation;
import org.apache.commons.geometry.core.Transform;
import org.apache.commons.geometry.core.partitioning.HyperplaneConvexSubset;
import org.apache.commons.geometry.core.partitioning.HyperplaneLocation;
import org.apache.commons.geometry.core.partitioning.Split;
import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.EuclideanTestUtils;
import org.apache.commons.numbers.core.Precision;
import org.junit.Assert;
import org.junit.Test;

public class OrientedPointTest {

    private static final double TEST_EPS = 1e-15;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    @Test
    public void testGetDirection() {
        // act/assert
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.Unit.PLUS,
                OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, TEST_PRECISION).getDirection(),
                TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.Unit.MINUS,
                OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), false, TEST_PRECISION).getDirection(),
                TEST_EPS);
    }

    @Test
    public void testReverse() {
        // act/assert
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(0), true, TEST_PRECISION).reverse(),
                0.0, false, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(-1), false, TEST_PRECISION).reverse(),
                -1.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(1), true, TEST_PRECISION).reverse(),
                1.0, false, TEST_PRECISION);

        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(0), true, TEST_PRECISION).reverse().reverse(),
                0.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(-1), false, TEST_PRECISION).reverse().reverse(),
                -1.0, false, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(1), true, TEST_PRECISION).reverse().reverse(),
                1.0, true, TEST_PRECISION);
    }

    @Test
    public void testTransform() {
        // arrange
        final AffineTransformMatrix1D scaleAndTranslate = AffineTransformMatrix1D
                .createScale(0.5)
                .translate(-10);

        final AffineTransformMatrix1D reflect = AffineTransformMatrix1D.createScale(-2);

        final OrientedPoint a = OrientedPoints.createPositiveFacing(Vector1D.of(2.0), TEST_PRECISION);
        final OrientedPoint b = OrientedPoints.createNegativeFacing(Vector1D.of(-3.0), TEST_PRECISION);

        // act/assert
        assertOrientedPoint(a.transform(scaleAndTranslate), -9.0, true, TEST_PRECISION);
        assertOrientedPoint(b.transform(scaleAndTranslate), -11.5, false, TEST_PRECISION);

        assertOrientedPoint(a.transform(reflect), -4.0, false, TEST_PRECISION);
        assertOrientedPoint(b.transform(reflect), 6.0, true, TEST_PRECISION);
    }

    @Test
    public void testTransform_locationAtInfinity() {
        // arrange
        final OrientedPoint pos = OrientedPoints.createNegativeFacing(Double.POSITIVE_INFINITY, TEST_PRECISION);
        final OrientedPoint neg = OrientedPoints.createPositiveFacing(Double.NEGATIVE_INFINITY, TEST_PRECISION);

        final Transform<Vector1D> scaleAndTranslate = AffineTransformMatrix1D.identity().scale(10.0).translate(5.0);
        final Transform<Vector1D> negate = AffineTransformMatrix1D.from(Vector1D::negate);

        // act/assert
        assertOrientedPoint(pos.transform(scaleAndTranslate), Double.POSITIVE_INFINITY, false, TEST_PRECISION);
        assertOrientedPoint(neg.transform(scaleAndTranslate), Double.NEGATIVE_INFINITY, true, TEST_PRECISION);

        assertOrientedPoint(pos.transform(negate), Double.NEGATIVE_INFINITY, true, TEST_PRECISION);
        assertOrientedPoint(neg.transform(negate), Double.POSITIVE_INFINITY, false, TEST_PRECISION);
    }

    @Test
    public void testTransform_zeroScale() {
        // arrange
        final AffineTransformMatrix1D zeroScale = AffineTransformMatrix1D.createScale(0.0);

        final OrientedPoint pt = OrientedPoints.createPositiveFacing(Vector1D.of(2.0), TEST_PRECISION);

        // act/assert
        GeometryTestUtils.assertThrows(
            () -> pt.transform(zeroScale),
            IllegalArgumentException.class, "Oriented point direction cannot be zero");
    }

    @Test
    public void testOffset_positiveFacing() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(-2.0), true, TEST_PRECISION);

        // act/assert
        Assert.assertEquals(-98.0, pt.offset(Vector1D.of(-100)), Precision.EPSILON);
        Assert.assertEquals(-0.1, pt.offset(Vector1D.of(-2.1)), Precision.EPSILON);
        Assert.assertEquals(0.0, pt.offset(Vector1D.of(-2)), Precision.EPSILON);
        Assert.assertEquals(0.99, pt.offset(Vector1D.of(-1.01)), Precision.EPSILON);
        Assert.assertEquals(1.0, pt.offset(Vector1D.of(-1.0)), Precision.EPSILON);
        Assert.assertEquals(1.01, pt.offset(Vector1D.of(-0.99)), Precision.EPSILON);
        Assert.assertEquals(2.0, pt.offset(Vector1D.of(0)), Precision.EPSILON);
        Assert.assertEquals(102, pt.offset(Vector1D.of(100)), Precision.EPSILON);
    }

    @Test
    public void testOffset_negativeFacing() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(-2.0), false, TEST_PRECISION);

        // act/assert
        Assert.assertEquals(98.0, pt.offset(Vector1D.of(-100)), Precision.EPSILON);
        Assert.assertEquals(0.1, pt.offset(Vector1D.of(-2.1)), Precision.EPSILON);
        Assert.assertEquals(0.0, pt.offset(Vector1D.of(-2)), Precision.EPSILON);
        Assert.assertEquals(-0.99, pt.offset(Vector1D.of(-1.01)), Precision.EPSILON);
        Assert.assertEquals(-1.0, pt.offset(Vector1D.of(-1.0)), Precision.EPSILON);
        Assert.assertEquals(-1.01, pt.offset(Vector1D.of(-0.99)), Precision.EPSILON);
        Assert.assertEquals(-2, pt.offset(Vector1D.of(0)), Precision.EPSILON);
        Assert.assertEquals(-102, pt.offset(Vector1D.of(100)), Precision.EPSILON);
    }

    @Test
    public void testOffset_infinityArguments() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(-2.0), true, TEST_PRECISION);

        // act/assert
        GeometryTestUtils.assertPositiveInfinity(pt.offset(Vector1D.of(Double.POSITIVE_INFINITY)));
        GeometryTestUtils.assertNegativeInfinity(pt.offset(Vector1D.of(Double.NEGATIVE_INFINITY)));
    }

    @Test
    public void testOffset_infinityLocation() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(Double.POSITIVE_INFINITY), true, TEST_PRECISION);

        // act/assert
        Assert.assertTrue(Double.isNaN(pt.offset(Vector1D.of(Double.POSITIVE_INFINITY))));
        GeometryTestUtils.assertNegativeInfinity(pt.offset(Vector1D.of(Double.NEGATIVE_INFINITY)));

        GeometryTestUtils.assertNegativeInfinity(pt.offset(Vector1D.of(0)));
    }

    @Test
    public void testClassify() {
        // arrange
        final DoublePrecisionContext smallPrecision = new EpsilonDoublePrecisionContext(1e-10);
        final DoublePrecisionContext largePrecision = new EpsilonDoublePrecisionContext(1e-1);

        final OrientedPoint smallPosFacing = OrientedPoints.fromLocationAndDirection(1.0, true, smallPrecision);
        final OrientedPoint largeNegFacing = OrientedPoints.fromLocationAndDirection(1.0, false, largePrecision);

        // act/assert
        assertClassify(HyperplaneLocation.MINUS, smallPosFacing,
                Double.NEGATIVE_INFINITY, -10, 0, 0.9, 0.99999, 1 - 1e-9);
        assertClassify(HyperplaneLocation.ON, smallPosFacing,
                1 - 1e-11, 1, 1 + 1e-11);
        assertClassify(HyperplaneLocation.PLUS, smallPosFacing,
                1 + 1e-9, 2, 10, Double.POSITIVE_INFINITY);

        assertClassify(HyperplaneLocation.PLUS, largeNegFacing,
                Double.NEGATIVE_INFINITY, -10, 0, 0.89);
        assertClassify(HyperplaneLocation.ON, largeNegFacing,
                0.91, 0.9999, 1, 1.001, 1.09);
        assertClassify(HyperplaneLocation.MINUS, largeNegFacing,
                1.11, 2, 10, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testSpan() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(1.0), false, TEST_PRECISION);

        // act
        final HyperplaneConvexSubset<Vector1D> result = pt.span();

        // assert
        Assert.assertSame(pt, result.getHyperplane());
    }

    @Test
    public void testSimilarOrientation() {
        // arrange
        final OrientedPoint negativeDir1 = OrientedPoints.fromPointAndDirection(Vector1D.of(1.0), false, TEST_PRECISION);
        final OrientedPoint negativeDir2 = OrientedPoints.fromPointAndDirection(Vector1D.of(-1.0), false, TEST_PRECISION);
        final OrientedPoint positiveDir1 = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, TEST_PRECISION);
        final OrientedPoint positiveDir2 = OrientedPoints.fromPointAndDirection(Vector1D.of(-2.0), true, TEST_PRECISION);

        // act/assert
        Assert.assertTrue(negativeDir1.similarOrientation(negativeDir1));
        Assert.assertTrue(negativeDir1.similarOrientation(negativeDir2));
        Assert.assertTrue(negativeDir2.similarOrientation(negativeDir1));

        Assert.assertTrue(positiveDir1.similarOrientation(positiveDir1));
        Assert.assertTrue(positiveDir1.similarOrientation(positiveDir2));
        Assert.assertTrue(positiveDir2.similarOrientation(positiveDir1));

        Assert.assertFalse(negativeDir1.similarOrientation(positiveDir1));
        Assert.assertFalse(positiveDir1.similarOrientation(negativeDir1));
    }

    @Test
    public void testProject() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(1.0), true, TEST_PRECISION);

        // act/assert
        Assert.assertEquals(1.0, pt.project(Vector1D.of(-1.0)).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, pt.project(Vector1D.of(0.0)).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, pt.project(Vector1D.of(1.0)).getX(), Precision.EPSILON);
        Assert.assertEquals(1.0, pt.project(Vector1D.of(100.0)).getX(), Precision.EPSILON);
    }


    @Test
    public void testEq() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-3);

        final OrientedPoint a = OrientedPoints.createPositiveFacing(0, precision);
        final OrientedPoint b = OrientedPoints.createPositiveFacing(0, TEST_PRECISION);

        final OrientedPoint c = OrientedPoints.createPositiveFacing(2e-3, precision);
        final OrientedPoint d = OrientedPoints.createNegativeFacing(0, precision);
        final OrientedPoint e = OrientedPoints.createPositiveFacing(1e-4, precision);

        // act/assert
        Assert.assertTrue(a.eq(a, precision));
        Assert.assertTrue(a.eq(b, precision));

        Assert.assertFalse(a.eq(c, precision));
        Assert.assertFalse(a.eq(d, precision));

        Assert.assertTrue(a.eq(e, precision));
        Assert.assertTrue(e.eq(a, precision));
    }

    @Test
    public void testHashCode() {
        // arrange
        final DoublePrecisionContext precisionA = new EpsilonDoublePrecisionContext(1e-10);
        final DoublePrecisionContext precisionB = new EpsilonDoublePrecisionContext(1e-15);

        final OrientedPoint a = OrientedPoints.fromPointAndDirection(Vector1D.of(3.0), true, precisionA);
        final OrientedPoint b = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), false, precisionA);
        final OrientedPoint c = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionB);

        final OrientedPoint d = OrientedPoints.fromPointAndDirection(Vector1D.of(3.0), true, precisionA);
        final OrientedPoint e = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), false, precisionA);
        final OrientedPoint f = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionB);

        // act/assert
        Assert.assertNotEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(b.hashCode(), c.hashCode());
        Assert.assertNotEquals(c.hashCode(), a.hashCode());

        Assert.assertEquals(a.hashCode(), d.hashCode());
        Assert.assertEquals(b.hashCode(), e.hashCode());
        Assert.assertEquals(c.hashCode(), f.hashCode());
    }

    @Test
    public void testEquals() {
        // arrange
        final DoublePrecisionContext precisionA = new EpsilonDoublePrecisionContext(1e-10);
        final DoublePrecisionContext precisionB = new EpsilonDoublePrecisionContext(1e-15);

        final OrientedPoint a = OrientedPoints.fromPointAndDirection(Vector1D.of(1.0), true, precisionA);
        final OrientedPoint b = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionA);

        final OrientedPoint c = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionA);
        final OrientedPoint d = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), false, precisionA);

        final OrientedPoint e = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionA);
        final OrientedPoint f = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, precisionB);

        final OrientedPoint g = OrientedPoints.fromPointAndDirection(Vector1D.of(1.0), true, precisionA);

        // act/assert
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals(new Object()));

        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(c, d);
        Assert.assertNotEquals(e, f);

        Assert.assertEquals(a, a);
        Assert.assertEquals(a, g);
        Assert.assertEquals(g, a);
    }

    @Test
    public void testToString() {
        // arrange
        final OrientedPoint pt = OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), true, TEST_PRECISION);

        // act
        final String str = pt.toString();

        // assert
        Assert.assertTrue(str.contains("OrientedPoint"));
        Assert.assertTrue(str.contains("point= (2.0)"));
        Assert.assertTrue(str.contains("direction= (1.0)"));
    }

    @Test
    public void testFromLocationAndDirection() {
        // act/assert
        assertOrientedPoint(OrientedPoints.fromLocationAndDirection(3.0, true, TEST_PRECISION),
                3.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromLocationAndDirection(2.0, false, TEST_PRECISION),
                2.0, false, TEST_PRECISION);
    }

    @Test
    public void testFromPointAndDirection_pointAndBooleanArgs() {
        // act/assert
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(3.0), true, TEST_PRECISION),
                3.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), false, TEST_PRECISION),
                2.0, false, TEST_PRECISION);
    }

    @Test
    public void testFromPointAndDirection_pointAndVectorArgs() {
        // act/assert
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(-2.0), Vector1D.of(0.1), TEST_PRECISION),
                -2.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), Vector1D.of(-10.1), TEST_PRECISION),
                2.0, false, TEST_PRECISION);
    }

    @Test
    public void testFromPointAndDirection_invalidDirection() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(0.1);

        // act/assert
        GeometryTestUtils.assertThrows(
            () -> OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), Vector1D.of(0.09), precision),
            IllegalArgumentException.class, "Oriented point direction cannot be zero");
        GeometryTestUtils.assertThrows(
            () -> OrientedPoints.fromPointAndDirection(Vector1D.of(2.0), Vector1D.of(-0.09), precision),
            IllegalArgumentException.class, "Oriented point direction cannot be zero");
    }

    @Test
    public void testCreatePositiveFacing() {
        // act/assert
        assertOrientedPoint(OrientedPoints.createPositiveFacing(Vector1D.of(-2.0), TEST_PRECISION),
                -2.0, true, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.createPositiveFacing(-4.0, TEST_PRECISION),
                -4.0, true, TEST_PRECISION);
    }

    @Test
    public void testCreateNegativeFacing() {
        // act/assert
        assertOrientedPoint(OrientedPoints.createNegativeFacing(Vector1D.of(2.0), TEST_PRECISION),
                2.0, false, TEST_PRECISION);
        assertOrientedPoint(OrientedPoints.createNegativeFacing(4, TEST_PRECISION),
                4.0, false, TEST_PRECISION);
    }

    @Test
    public void testSubset_split() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-3);

        final OrientedPoint pt = OrientedPoints.createPositiveFacing(-1.5, precision);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act/assert
        checkSplit(sub, OrientedPoints.createPositiveFacing(1.0, precision), true, false);
        checkSplit(sub, OrientedPoints.createPositiveFacing(-1.5 + 1e-2, precision), true, false);

        checkSplit(sub, OrientedPoints.createNegativeFacing(1.0, precision), false, true);
        checkSplit(sub, OrientedPoints.createNegativeFacing(-1.5 + 1e-2, precision), false, true);

        checkSplit(sub, OrientedPoints.createNegativeFacing(-1.5, precision), false, false);
        checkSplit(sub, OrientedPoints.createNegativeFacing(-1.5 + 1e-4, precision), false, false);
        checkSplit(sub, OrientedPoints.createNegativeFacing(-1.5 - 1e-4, precision), false, false);
    }

    private void checkSplit(final HyperplaneConvexSubset<Vector1D> sub, final OrientedPoint splitter, final boolean minus, final boolean plus) {
        final Split<? extends HyperplaneConvexSubset<Vector1D>> split = sub.split(splitter);

        Assert.assertSame(minus ? sub : null, split.getMinus());
        Assert.assertSame(plus ? sub : null, split.getPlus());
    }

    @Test
    public void testSubset_simpleMethods() {
        // arrange
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(2, TEST_PRECISION);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act/assert
        Assert.assertSame(pt, sub.getHyperplane());
        Assert.assertFalse(sub.isFull());
        Assert.assertFalse(sub.isEmpty());
        Assert.assertFalse(sub.isInfinite());
        Assert.assertTrue(sub.isFinite());
        Assert.assertEquals(0.0, sub.getSize(), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(2), sub.getCentroid(), TEST_EPS);

        final List<? extends HyperplaneConvexSubset<Vector1D>> list = sub.toConvex();
        Assert.assertEquals(1, list.size());
        Assert.assertSame(sub, list.get(0));
    }

    @Test
    public void testSubset_classify() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-1);
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(1, precision);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act/assert
        Assert.assertEquals(RegionLocation.BOUNDARY, sub.classify(Vector1D.of(0.95)));
        Assert.assertEquals(RegionLocation.BOUNDARY, sub.classify(Vector1D.of(1)));
        Assert.assertEquals(RegionLocation.BOUNDARY, sub.classify(Vector1D.of(1.05)));

        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.of(1.11)));
        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.of(0.89)));

        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.of(-3)));
        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.of(10)));

        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.NEGATIVE_INFINITY));
        Assert.assertEquals(RegionLocation.OUTSIDE, sub.classify(Vector1D.POSITIVE_INFINITY));
    }

    @Test
    public void testSubset_contains() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-1);
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(1, precision);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act/assert
        Assert.assertTrue(sub.contains(Vector1D.of(0.95)));
        Assert.assertTrue(sub.contains(Vector1D.of(1)));
        Assert.assertTrue(sub.contains(Vector1D.of(1.05)));

        Assert.assertFalse(sub.contains(Vector1D.of(1.11)));
        Assert.assertFalse(sub.contains(Vector1D.of(0.89)));

        Assert.assertFalse(sub.contains(Vector1D.of(-3)));
        Assert.assertFalse(sub.contains(Vector1D.of(10)));

        Assert.assertFalse(sub.contains(Vector1D.NEGATIVE_INFINITY));
        Assert.assertFalse(sub.contains(Vector1D.POSITIVE_INFINITY));
    }

    @Test
    public void testSubset_closestContained() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-1);
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(1, precision);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act/assert
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(1), sub.closest(Vector1D.NEGATIVE_INFINITY), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(1), sub.closest(Vector1D.of(0)), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(1), sub.closest(Vector1D.of(1)), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(1), sub.closest(Vector1D.of(2)), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector1D.of(1), sub.closest(Vector1D.POSITIVE_INFINITY), TEST_EPS);
    }

    @Test
    public void testSubset_transform() {
        // arrange
        final AffineTransformMatrix1D scaleAndTranslate = AffineTransformMatrix1D
                .createScale(0.5)
                .translate(-10);

        final AffineTransformMatrix1D reflect = AffineTransformMatrix1D.createScale(-2);

        final HyperplaneConvexSubset<Vector1D> a =
                OrientedPoints.createPositiveFacing(Vector1D.of(2.0), TEST_PRECISION).span();
        final HyperplaneConvexSubset<Vector1D> b =
                OrientedPoints.createNegativeFacing(Vector1D.of(-3.0), TEST_PRECISION).span();

        // act/assert
        assertOrientedPoint((OrientedPoint) a.transform(scaleAndTranslate).getHyperplane(),
                -9.0, true, TEST_PRECISION);
        assertOrientedPoint((OrientedPoint) b.transform(scaleAndTranslate).getHyperplane(),
                -11.5, false, TEST_PRECISION);

        assertOrientedPoint((OrientedPoint) a.transform(reflect).getHyperplane(), -4.0, false, TEST_PRECISION);
        assertOrientedPoint((OrientedPoint) b.transform(reflect).getHyperplane(), 6.0, true, TEST_PRECISION);
    }

    @Test
    public void testSubset_reverse() {
        // arrange
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(2.0, TEST_PRECISION);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act
        final HyperplaneConvexSubset<Vector1D> result = sub.reverse();

        // assert
        Assert.assertEquals(2.0, ((OrientedPoint) result.getHyperplane()).getLocation(), TEST_EPS);
        Assert.assertFalse(((OrientedPoint) result.getHyperplane()).isPositiveFacing());

        Assert.assertEquals(sub.getHyperplane(), result.reverse().getHyperplane());
    }

    @Test
    public void testSubset_toString() {
        // arrange
        final OrientedPoint pt = OrientedPoints.createPositiveFacing(2, TEST_PRECISION);
        final HyperplaneConvexSubset<Vector1D> sub = pt.span();

        // act
        final String str = sub.toString();

        //assert
        Assert.assertTrue(str.contains("OrientedPointConvexSubset"));
        Assert.assertTrue(str.contains("point= (2.0)"));
        Assert.assertTrue(str.contains("direction= (1.0)"));
    }

    private static void assertOrientedPoint(final OrientedPoint pt, final double location, final boolean positiveFacing,
                                            final DoublePrecisionContext precision) {
        Assert.assertEquals(location, pt.getPoint().getX(), TEST_EPS);
        Assert.assertEquals(location, pt.getLocation(), TEST_EPS);
        Assert.assertEquals(positiveFacing ? 1.0 : -1.0, pt.getDirection().getX(), TEST_EPS);
        Assert.assertEquals(positiveFacing, pt.isPositiveFacing());
        Assert.assertSame(precision, pt.getPrecision());
    }

    private static void assertClassify(final HyperplaneLocation expected, final OrientedPoint pt, final double... locations) {
        for (final double location : locations) {
            final String msg = "Unexpected classification for location " + location;

            Assert.assertEquals(msg, expected, pt.classify(location));
            Assert.assertEquals(msg, expected, pt.classify(Vector1D.of(location)));
        }
    }
}
