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
package org.apache.commons.geometry.euclidean.twod;

import java.util.List;

import org.apache.commons.geometry.core.GeometryTestUtils;
import org.apache.commons.geometry.core.partitioning.HyperplaneLocation;
import org.apache.commons.geometry.core.partitioning.Split;
import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.EuclideanTestUtils;
import org.apache.commons.geometry.euclidean.oned.Interval;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class LineConvexSubsetTest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    @Test
    public void testFromInterval_intervalArg_finite() {
        // arrange
        final DoublePrecisionContext intervalPrecision = new EpsilonDoublePrecisionContext(1e-2);
        final Interval interval = Interval.of(-1, 2, intervalPrecision);

        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final Segment segment = (Segment) Lines.subsetFromInterval(line, interval);

        // assert
        final double side = 1.0 / Math.sqrt(2);
        checkFinite(segment, Vector2D.of(-side, -side), Vector2D.of(2 * side, 2 * side));

        Assert.assertSame(TEST_PRECISION, segment.getPrecision());
    }

    @Test
    public void testFromInterval_intervalArg_full() {
        // arrange
        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final LineConvexSubset span = Lines.subsetFromInterval(line, Interval.full());

        // assert
        GeometryTestUtils.assertNegativeInfinity(span.getSubspaceStart());
        GeometryTestUtils.assertPositiveInfinity(span.getSubspaceEnd());

        Assert.assertNull(span.getStartPoint());
        Assert.assertNull(span.getEndPoint());

        Assert.assertSame(Interval.full(), span.getInterval());
        Assert.assertSame(TEST_PRECISION, span.getPrecision());
    }

    @Test
    public void testFromInterval_intervalArg_positiveHalfSpace() {
        // arrange
        final DoublePrecisionContext intervalPrecision = new EpsilonDoublePrecisionContext(1e-2);
        final Interval interval = Interval.min(-1, intervalPrecision);

        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final Ray ray = (Ray) Lines.subsetFromInterval(line, interval);

        // assert
        Assert.assertEquals(-1.0, ray.getSubspaceStart(), TEST_EPS);
        GeometryTestUtils.assertPositiveInfinity(ray.getSubspaceEnd());

        final double side = 1.0 / Math.sqrt(2);

        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.of(-side, -side), ray.getStartPoint(), TEST_EPS);
        Assert.assertNull(ray.getEndPoint());

        checkInterval(interval, ray.getInterval());
        Assert.assertSame(TEST_PRECISION, ray.getPrecision());
    }

    @Test
    public void testFromInterval_intervalArg_negativeHalfSpace() {
        // arrange
        final DoublePrecisionContext intervalPrecision = new EpsilonDoublePrecisionContext(1e-2);
        final Interval interval = Interval.max(2, intervalPrecision);

        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final ReverseRay halfLine = (ReverseRay) Lines.subsetFromInterval(line, interval);

        // assert
        GeometryTestUtils.assertNegativeInfinity(halfLine.getSubspaceStart());
        Assert.assertEquals(2, halfLine.getSubspaceEnd(), TEST_EPS);

        final double side = 1.0 / Math.sqrt(2);

        Assert.assertNull(halfLine.getStartPoint());
        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.of(2 * side, 2 * side), halfLine.getEndPoint(), TEST_EPS);

        checkInterval(interval, halfLine.getInterval());
        Assert.assertSame(TEST_PRECISION, halfLine.getPrecision());
    }

    @Test
    public void testFromInterval_doubleArgs_finite() {
        // arrange
        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final Segment segment = (Segment) Lines.subsetFromInterval(line, -1, 2);

        // assert
        final double side = 1.0 / Math.sqrt(2);
        checkFinite(segment, Vector2D.of(-side, -side), Vector2D.of(2 * side, 2 * side));

        Assert.assertSame(TEST_PRECISION, segment.getPrecision());
    }

    @Test
    public void testFromInterval_doubleArgs_full() {
        // arrange
        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final LineConvexSubset span = Lines.subsetFromInterval(line, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        // assert
        GeometryTestUtils.assertNegativeInfinity(span.getSubspaceStart());
        GeometryTestUtils.assertPositiveInfinity(span.getSubspaceEnd());

        Assert.assertNull(span.getStartPoint());
        Assert.assertNull(span.getEndPoint());

        Assert.assertSame(TEST_PRECISION, span.getPrecision());
    }

    @Test
    public void testFromInterval_doubleArgs_positiveHalfSpace() {
        // arrange
        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final Ray ray = (Ray) Lines.subsetFromInterval(line, -1, Double.POSITIVE_INFINITY);

        // assert
        Assert.assertEquals(-1.0, ray.getSubspaceStart(), TEST_EPS);
        GeometryTestUtils.assertPositiveInfinity(ray.getSubspaceEnd());

        final double side = 1.0 / Math.sqrt(2);

        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.of(-side, -side), ray.getStartPoint(), TEST_EPS);
        Assert.assertNull(ray.getEndPoint());

        Assert.assertSame(TEST_PRECISION, ray.getPrecision());
    }

    @Test
    public void testFromInterval_doubleArgs_negativeHalfSpace() {
        // arrange
        final Line line = Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(1, 1), TEST_PRECISION);

        // act
        final ReverseRay halfLine = (ReverseRay) Lines.subsetFromInterval(line, 2, Double.NEGATIVE_INFINITY);

        // assert
        GeometryTestUtils.assertNegativeInfinity(halfLine.getSubspaceStart());
        Assert.assertEquals(2, halfLine.getSubspaceEnd(), TEST_EPS);

        final double side = 1.0 / Math.sqrt(2);

        Assert.assertNull(halfLine.getStartPoint());
        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.of(2 * side, 2 * side), halfLine.getEndPoint(), TEST_EPS);

        Assert.assertSame(TEST_PRECISION, halfLine.getPrecision());
    }

    @Test
    public void testFromInterval_doubleArgs_invalid() {
        // arrange
        final Line line = Lines.fromPointAndAngle(Vector2D.ZERO, 0, TEST_PRECISION);

        // act/assert
        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, 0, Double.NaN);
        }, IllegalArgumentException.class, "Invalid line subset interval: 0.0, NaN");

        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, Double.NaN, 0.0);
        }, IllegalArgumentException.class, "Invalid line subset interval: NaN, 0.0");

        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }, IllegalArgumentException.class, "Invalid line subset interval: Infinity, Infinity");

        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        }, IllegalArgumentException.class, "Invalid line subset interval: -Infinity, -Infinity");

        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, Double.POSITIVE_INFINITY, Double.NaN);
        }, IllegalArgumentException.class, "Invalid line subset interval: Infinity, NaN");

        GeometryTestUtils.assertThrows(() -> {
            Lines.subsetFromInterval(line, Double.NaN, Double.NEGATIVE_INFINITY);
        }, IllegalArgumentException.class, "Invalid line subset interval: NaN, -Infinity");
    }

    @Test
    public void testToConvex() {
        // arrange
        final Line line = Lines.fromPoints(Vector2D.of(-1, 0), Vector2D.of(4, 5), TEST_PRECISION);
        final LineConvexSubset sub = Lines.subsetFromInterval(line, 1, 2);

        // act
        final List<LineConvexSubset> segments = sub.toConvex();

        // assert
        Assert.assertEquals(1, segments.size());
        Assert.assertSame(sub, segments.get(0));
    }

    @Test
    public void testIntersection_line() {
        // arrange
        final Segment aSeg = Lines.segmentFromPoints(Vector2D.of(1, 0), Vector2D.of(2, 0), TEST_PRECISION);
        final Segment bSeg = Lines.segmentFromPoints(Vector2D.of(-1, -1), Vector2D.of(1, 1), TEST_PRECISION);

        final Line xAxis = Lines.fromPointAndAngle(Vector2D.ZERO, 0.0, TEST_PRECISION);
        final Line yAxis = Lines.fromPointAndAngle(Vector2D.ZERO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION);
        final Line angledLine = Lines.fromPoints(Vector2D.of(1, 1), Vector2D.of(2, 0), TEST_PRECISION);

        // act/assert
        Assert.assertNull(aSeg.intersection(xAxis));
        Assert.assertNull(aSeg.intersection(yAxis));

        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.ZERO, bSeg.intersection(xAxis), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.ZERO, bSeg.intersection(yAxis), TEST_EPS);

        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.of(1, 1), bSeg.intersection(angledLine), TEST_EPS);
    }

    @Test
    public void testIntersection_lineSegment() {
        // arrange
        final Segment a = Lines.segmentFromPoints(Vector2D.of(1, 0), Vector2D.of(2, 0), TEST_PRECISION);
        final Segment b = Lines.segmentFromPoints(Vector2D.of(-1, -1), Vector2D.of(1, 1), TEST_PRECISION);
        final Segment c = Lines.segmentFromPoints(Vector2D.of(-1, 0), Vector2D.ZERO, TEST_PRECISION);
        final Segment d = Lines.segmentFromPoints(Vector2D.of(0, 3), Vector2D.of(3, 0), TEST_PRECISION);

        // act/assert
        Assert.assertNull(a.intersection(a));
        Assert.assertNull(a.intersection(c));
        Assert.assertNull(a.intersection(b));

        EuclideanTestUtils.assertCoordinatesEqual(Vector2D.ZERO, b.intersection(c), TEST_EPS);

        Assert.assertNull(b.intersection(d));
        Assert.assertNull(d.intersection(b));
    }

    @Test
    public void testSplit_finite() {
        // arrange
        final Vector2D start = Vector2D.of(1, 1);
        final Vector2D end = Vector2D.of(3, 2);
        final Vector2D middle = start.lerp(end, 0.5);

        final Segment sub = Lines.segmentFromPoints(start, end, TEST_PRECISION);

        // act/assert
        final Split<LineConvexSubset> both = sub.split(Lines.fromPointAndDirection(middle, Vector2D.of(1, -2), TEST_PRECISION));
        checkFinite(both.getMinus(), middle, end);
        checkFinite(both.getPlus(), start, middle);

        final Split<LineConvexSubset> bothReversed = sub.split(Lines.fromPointAndDirection(middle, Vector2D.of(-1, 2), TEST_PRECISION));
        checkFinite(bothReversed.getMinus(), start, middle);
        checkFinite(bothReversed.getPlus(), middle, end);

        final Split<LineConvexSubset> minusOnlyOrthogonal = sub.split(Lines.fromPointAndDirection(start, Vector2D.of(1, -2), TEST_PRECISION));
        Assert.assertSame(sub, minusOnlyOrthogonal.getMinus());
        Assert.assertNull(minusOnlyOrthogonal.getPlus());

        final Split<LineConvexSubset> minusOnlyParallel = sub.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertSame(sub, minusOnlyParallel.getMinus());
        Assert.assertNull(minusOnlyParallel.getPlus());

        final Split<LineConvexSubset> plusOnlyOrthogonal = sub.split(Lines.fromPointAndDirection(end, Vector2D.of(1, -2), TEST_PRECISION));
        Assert.assertNull(plusOnlyOrthogonal.getMinus());
        Assert.assertSame(sub, plusOnlyOrthogonal.getPlus());

        final Split<LineConvexSubset> plusOnlyParallel = sub.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(-2, -1), TEST_PRECISION));
        Assert.assertNull(plusOnlyParallel.getMinus());
        Assert.assertSame(sub, plusOnlyParallel.getPlus());

        final Split<LineConvexSubset> hyper = sub.split(Lines.fromPointAndDirection(start, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertNull(hyper.getMinus());
        Assert.assertNull(hyper.getPlus());
    }

    @Test
    public void testSplit_full() {
        // arrange
        final Vector2D p1 = Vector2D.of(1, 1);
        final Vector2D p2 = Vector2D.of(3, 2);
        final Vector2D middle = p1.lerp(p2, 0.5);

        final Line line = Lines.fromPoints(p1, p2, TEST_PRECISION);

        final LineConvexSubset seg = Lines.subsetFromInterval(line, Interval.full());

        // act/assert
        final Split<LineConvexSubset> both = seg.split(Lines.fromPointAndDirection(middle, Vector2D.of(1, -2), TEST_PRECISION));
        checkInfinite(both.getMinus(), line,  middle, null);
        checkInfinite(both.getPlus(), line, null, middle);

        final Split<LineConvexSubset> bothReversed = seg.split(Lines.fromPointAndDirection(middle, Vector2D.of(-1, 2), TEST_PRECISION));
        checkInfinite(bothReversed.getMinus(), line,  null, middle);
        checkInfinite(bothReversed.getPlus(), line, middle, null);

        final Split<LineConvexSubset> minusOnlyParallel = seg.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertSame(seg, minusOnlyParallel.getMinus());
        Assert.assertNull(minusOnlyParallel.getPlus());

        final Split<LineConvexSubset> plusOnlyParallel = seg.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(-2, -1), TEST_PRECISION));
        Assert.assertNull(plusOnlyParallel.getMinus());
        Assert.assertSame(seg, plusOnlyParallel.getPlus());

        final Split<LineConvexSubset> hyper = seg.split(Lines.fromPointAndDirection(p1, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertNull(hyper.getMinus());
        Assert.assertNull(hyper.getPlus());
    }

    @Test
    public void testSplit_positiveHalfSpace() {
        // arrange
        final Vector2D p1 = Vector2D.of(1, 1);
        final Vector2D p2 = Vector2D.of(3, 2);
        final Vector2D middle = p1.lerp(p2, 0.5);

        final Line line = Lines.fromPoints(p1, p2, TEST_PRECISION);

        final LineConvexSubset sub = Lines.subsetFromInterval(line, Interval.min(line.toSubspace(p1).getX(), TEST_PRECISION));

        // act/assert
        final Split<LineConvexSubset> both = sub.split(Lines.fromPointAndDirection(middle, Vector2D.of(1, -2), TEST_PRECISION));
        checkInfinite(both.getMinus(), line,  middle, null);
        checkFinite(both.getPlus(), p1, middle);

        final Split<LineConvexSubset> bothReversed = sub.split(Lines.fromPointAndDirection(middle, Vector2D.of(-1, 2), TEST_PRECISION));
        checkFinite(bothReversed.getMinus(), p1, middle);
        checkInfinite(bothReversed.getPlus(), line, middle, null);

        final Split<LineConvexSubset> minusOnlyParallel = sub.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertSame(sub, minusOnlyParallel.getMinus());
        Assert.assertNull(minusOnlyParallel.getPlus());

        final Split<LineConvexSubset> minusOnlyOrthogonal = sub.split(Lines.fromPointAndDirection(p1, Vector2D.of(1, -2), TEST_PRECISION));
        Assert.assertSame(sub, minusOnlyOrthogonal.getMinus());
        Assert.assertNull(minusOnlyOrthogonal.getPlus());

        final Split<LineConvexSubset> plusOnlyParallel = sub.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(-2, -1), TEST_PRECISION));
        Assert.assertNull(plusOnlyParallel.getMinus());
        Assert.assertSame(sub, plusOnlyParallel.getPlus());

        final Split<LineConvexSubset> hyper = sub.split(Lines.fromPointAndDirection(p1, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertNull(hyper.getMinus());
        Assert.assertNull(hyper.getPlus());
    }

    @Test
    public void testSplit_negativeHalfSpace() {
        // arrange
        final Vector2D p1 = Vector2D.of(1, 1);
        final Vector2D p2 = Vector2D.of(3, 2);
        final Vector2D middle = p1.lerp(p2, 0.5);

        final Line line = Lines.fromPoints(p1, p2, TEST_PRECISION);

        final LineConvexSubset seg = Lines.subsetFromInterval(line, Interval.max(line.toSubspace(p2).getX(), TEST_PRECISION));

        // act/assert
        final Split<LineConvexSubset> both = seg.split(Lines.fromPointAndDirection(middle, Vector2D.of(1, -2), TEST_PRECISION));
        checkFinite(both.getMinus(), middle, p2);
        checkInfinite(both.getPlus(), line, null, middle);

        final Split<LineConvexSubset> bothReversed = seg.split(Lines.fromPointAndDirection(middle, Vector2D.of(-1, 2), TEST_PRECISION));
        checkInfinite(bothReversed.getMinus(), line, null, middle);
        checkFinite(bothReversed.getPlus(), middle, p2);

        final Split<LineConvexSubset> minusOnlyParallel = seg.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertSame(seg, minusOnlyParallel.getMinus());
        Assert.assertNull(minusOnlyParallel.getPlus());

        final Split<LineConvexSubset> plusOnlyParallel = seg.split(Lines.fromPointAndDirection(Vector2D.ZERO, Vector2D.of(-2, -1), TEST_PRECISION));
        Assert.assertNull(plusOnlyParallel.getMinus());
        Assert.assertSame(seg, plusOnlyParallel.getPlus());

        final Split<LineConvexSubset> plusOnlyOrthogonal = seg.split(Lines.fromPointAndDirection(p2, Vector2D.of(1, -2), TEST_PRECISION));
        Assert.assertNull(plusOnlyOrthogonal.getMinus());
        Assert.assertSame(seg, plusOnlyOrthogonal.getPlus());

        final Split<LineConvexSubset> hyper = seg.split(Lines.fromPointAndDirection(p1, Vector2D.of(2, 1), TEST_PRECISION));
        Assert.assertNull(hyper.getMinus());
        Assert.assertNull(hyper.getPlus());
    }

    private static void checkInterval(final Interval expected, final Interval actual) {
        Assert.assertEquals(expected.getMin(), actual.getMin(), TEST_EPS);
        Assert.assertEquals(expected.getMax(), actual.getMax(), TEST_EPS);
    }

    private static void checkFinite(final LineConvexSubset segment, final Vector2D start, final Vector2D end) {
        checkFinite(segment, start, end, TEST_PRECISION);
    }

    private static void checkFinite(final LineConvexSubset segment, final Vector2D start, final Vector2D end, final DoublePrecisionContext precision) {
        Assert.assertFalse(segment.isInfinite());

        EuclideanTestUtils.assertCoordinatesEqual(start, segment.getStartPoint(), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(end, segment.getEndPoint(), TEST_EPS);

        final Line line = segment.getLine();
        Assert.assertEquals(HyperplaneLocation.ON, line.classify(segment.getStartPoint()));
        Assert.assertEquals(HyperplaneLocation.ON, line.classify(segment.getEndPoint()));

        Assert.assertEquals(line.toSubspace(segment.getStartPoint()).getX(), segment.getSubspaceStart(), TEST_EPS);
        Assert.assertEquals(line.toSubspace(segment.getEndPoint()).getX(), segment.getSubspaceEnd(), TEST_EPS);

        Assert.assertSame(precision, segment.getPrecision());
        Assert.assertSame(precision, line.getPrecision());
    }

    private static void checkInfinite(final LineConvexSubset segment, final Line line, final Vector2D start, final Vector2D end) {
        checkInfinite(segment, line, start, end, TEST_PRECISION);
    }

    private static void checkInfinite(final LineConvexSubset segment, final Line line, final Vector2D start, final Vector2D end,
                                      final DoublePrecisionContext precision) {

        Assert.assertTrue(segment.isInfinite());

        Assert.assertEquals(line, segment.getLine());

        if (start == null) {
            Assert.assertNull(segment.getStartPoint());
        } else {
            EuclideanTestUtils.assertCoordinatesEqual(start, segment.getStartPoint(), TEST_EPS);
            Assert.assertEquals(line.toSubspace(segment.getStartPoint()).getX(), segment.getSubspaceStart(), TEST_EPS);
        }

        if (end == null) {
            Assert.assertNull(segment.getEndPoint());
        } else {
            EuclideanTestUtils.assertCoordinatesEqual(end, segment.getEndPoint(), TEST_EPS);
            Assert.assertEquals(line.toSubspace(segment.getEndPoint()).getX(), segment.getSubspaceEnd(), TEST_EPS);
        }

        Assert.assertSame(precision, segment.getPrecision());
        Assert.assertSame(precision, line.getPrecision());
    }
}
