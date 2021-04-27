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

import java.util.List;

import org.apache.commons.geometry.core.GeometryTestUtils;
import org.apache.commons.geometry.core.RegionLocation;
import org.apache.commons.geometry.core.partitioning.HyperplaneSubset;
import org.apache.commons.geometry.core.partitioning.Split;
import org.apache.commons.geometry.core.partitioning.SplitLocation;
import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.spherical.SphericalTestUtils;
import org.apache.commons.geometry.spherical.oned.AngularInterval;
import org.apache.commons.geometry.spherical.oned.RegionBSPTree1S;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class EmbeddedTreeSubGreatCircleTest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    private static final GreatCircle XY_CIRCLE = GreatCircles.fromPoleAndU(
            Vector3D.Unit.PLUS_Z, Vector3D.Unit.PLUS_X, TEST_PRECISION);

    @Test
    public void testCtor_default() {
        // act
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(XY_CIRCLE);

        // assert
        Assert.assertFalse(sub.isFull());
        Assert.assertTrue(sub.isEmpty());
        Assert.assertTrue(sub.isFinite());
        Assert.assertFalse(sub.isInfinite());

        Assert.assertEquals(0, sub.getSize(), TEST_EPS);
        Assert.assertNull(sub.getCentroid());

        for (double az = 0; az <= PlaneAngleRadians.TWO_PI; az += 0.5) {
            for (double p = 0; p <= PlaneAngleRadians.PI; p += 0.5) {
                checkClassify(sub, RegionLocation.OUTSIDE, Point2S.of(az, p));
            }
        }
    }

    @Test
    public void testCtor_boolean_true() {
        // act
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(XY_CIRCLE, true);

        // assert
        Assert.assertTrue(sub.isFull());
        Assert.assertFalse(sub.isEmpty());
        Assert.assertTrue(sub.isFinite());
        Assert.assertFalse(sub.isInfinite());

        Assert.assertEquals(PlaneAngleRadians.TWO_PI, sub.getSize(), TEST_EPS);
        Assert.assertNull(sub.getCentroid());

        for (double az = 0; az < PlaneAngleRadians.TWO_PI; az += 0.1) {
            checkClassify(sub, RegionLocation.INSIDE, Point2S.of(az, PlaneAngleRadians.PI_OVER_TWO));
        }

        checkClassify(sub, RegionLocation.OUTSIDE,
                Point2S.PLUS_K, Point2S.of(0, PlaneAngleRadians.PI_OVER_TWO + 0.1),
                Point2S.MINUS_K, Point2S.of(0, PlaneAngleRadians.PI_OVER_TWO - 0.1));
    }

    @Test
    public void testCtor_boolean_false() {
        // act
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(XY_CIRCLE, false);

        // assert
        Assert.assertFalse(sub.isFull());
        Assert.assertTrue(sub.isEmpty());
        Assert.assertTrue(sub.isFinite());
        Assert.assertFalse(sub.isInfinite());

        Assert.assertEquals(0, sub.getSize(), TEST_EPS);
        Assert.assertNull(sub.getCentroid());

        for (double az = 0; az <= PlaneAngleRadians.TWO_PI; az += 0.5) {
            for (double p = 0; p <= PlaneAngleRadians.PI; p += 0.5) {
                checkClassify(sub, RegionLocation.OUTSIDE, Point2S.of(az, p));
            }
        }
    }

    @Test
    public void testCtor_tree() {
        // arrange
        final RegionBSPTree1S tree = RegionBSPTree1S.fromInterval(AngularInterval.of(1, 2, TEST_PRECISION));

        // act
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(XY_CIRCLE, tree);

        // assert
        Assert.assertFalse(sub.isFull());
        Assert.assertFalse(sub.isEmpty());
        Assert.assertTrue(sub.isFinite());
        Assert.assertFalse(sub.isInfinite());

        Assert.assertEquals(1, sub.getSize(), TEST_EPS);
        SphericalTestUtils.assertPointsEq(Point2S.of(1.5, PlaneAngleRadians.PI_OVER_TWO),
                sub.getCentroid(), TEST_EPS);

        checkClassify(sub, RegionLocation.INSIDE, Point2S.of(1.5, PlaneAngleRadians.PI_OVER_TWO));

        checkClassify(sub, RegionLocation.BOUNDARY,
                Point2S.of(1, PlaneAngleRadians.PI_OVER_TWO), Point2S.of(2, PlaneAngleRadians.PI_OVER_TWO));

        checkClassify(sub, RegionLocation.OUTSIDE,
                Point2S.of(0.5, PlaneAngleRadians.PI_OVER_TWO), Point2S.of(2.5, PlaneAngleRadians.PI_OVER_TWO),
                Point2S.of(1.5, 1), Point2S.of(1.5, PlaneAngleRadians.PI - 1));
    }

    @Test
    public void testTransform() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_K, Point2S.MINUS_I, TEST_PRECISION);
        final RegionBSPTree1S region = RegionBSPTree1S.empty();
        region.add(AngularInterval.of(PlaneAngleRadians.PI, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        region.add(AngularInterval.of(0, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));

        final Transform2S t = Transform2S.createRotation(Point2S.PLUS_I, PlaneAngleRadians.PI_OVER_TWO)
                .reflect(Point2S.of(-0.25 * PlaneAngleRadians.PI,  PlaneAngleRadians.PI_OVER_TWO));

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, region);

        // act
        final EmbeddedTreeGreatCircleSubset result = sub.transform(t);

        // assert
        final List<GreatArc> arcs = result.toConvex();
        Assert.assertEquals(2, arcs.size());

        checkArc(arcs.get(0), Point2S.MINUS_I, Point2S.MINUS_J);
        checkArc(arcs.get(1), Point2S.PLUS_I, Point2S.PLUS_J);
    }

    @Test
    public void testSplit_full() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, true);

        final GreatCircle splitter = GreatCircles.fromPole(Vector3D.of(-1, 0, 1), TEST_PRECISION);

        // act
        final Split<EmbeddedTreeGreatCircleSubset> split = sub.split(splitter);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        final EmbeddedTreeGreatCircleSubset minus = split.getMinus();
        Assert.assertSame(sub.getCircle(), minus.getCircle());

        final List<GreatArc> minusArcs = minus.toConvex();
        Assert.assertEquals(1, minusArcs.size());
        checkArc(minusArcs.get(0), Point2S.MINUS_J, Point2S.PLUS_J);

        checkClassify(minus, RegionLocation.OUTSIDE, Point2S.MINUS_I);
        checkClassify(minus, RegionLocation.INSIDE, Point2S.PLUS_I);

        final EmbeddedTreeGreatCircleSubset plus = split.getPlus();
        Assert.assertSame(sub.getCircle(), plus.getCircle());

        final List<GreatArc> plusArcs = plus.toConvex();
        Assert.assertEquals(1, plusArcs.size());
        checkArc(plusArcs.get(0), Point2S.PLUS_J, Point2S.MINUS_J);

        checkClassify(plus, RegionLocation.INSIDE, Point2S.MINUS_I);
        checkClassify(plus, RegionLocation.OUTSIDE, Point2S.PLUS_I);
    }

    @Test
    public void testSplit_empty() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, false);

        final GreatCircle splitter = GreatCircles.fromPole(Vector3D.of(-1, 0, 1), TEST_PRECISION);

        // act
        final Split<EmbeddedTreeGreatCircleSubset> split = sub.split(splitter);

        // assert
        Assert.assertEquals(SplitLocation.NEITHER, split.getLocation());

        final EmbeddedTreeGreatCircleSubset minus = split.getMinus();
        Assert.assertNull(minus);

        final EmbeddedTreeGreatCircleSubset plus = split.getPlus();
        Assert.assertNull(plus);
    }

    @Test
    public void testSplit_both() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_J, Point2S.PLUS_K, TEST_PRECISION);

        final RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(0, 1, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI + 1, PlaneAngleRadians.PI + 2, TEST_PRECISION));

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, tree);

        final GreatCircle splitter = GreatCircles.fromPole(Vector3D.of(0, 1, 1), TEST_PRECISION);

        // act
        final Split<EmbeddedTreeGreatCircleSubset> split = sub.split(splitter);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        final EmbeddedTreeGreatCircleSubset minus = split.getMinus();
        Assert.assertSame(sub.getCircle(), minus.getCircle());
        final List<GreatArc> minusArcs = minus.toConvex();
        Assert.assertEquals(2, minusArcs.size());
        checkArc(minusArcs.get(0), Point2S.of(1.5 * PlaneAngleRadians.PI, 0.25 * PlaneAngleRadians.PI), Point2S.MINUS_J);
        checkArc(minusArcs.get(1), Point2S.of(1.5 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO + 1),
                Point2S.of(0.5 * PlaneAngleRadians.PI, (1.5 * PlaneAngleRadians.PI) - 2));

        final EmbeddedTreeGreatCircleSubset plus = split.getPlus();
        Assert.assertSame(sub.getCircle(), plus.getCircle());
        final List<GreatArc> plusArcs = plus.toConvex();
        Assert.assertEquals(2, plusArcs.size());
        checkArc(plusArcs.get(0), Point2S.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO), Point2S.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO - 1));
        checkArc(plusArcs.get(1), Point2S.of(0, 0), Point2S.of(1.5 * PlaneAngleRadians.PI, 0.25 * PlaneAngleRadians.PI));
    }

    @Test
    public void testSplit_minus() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_J, Point2S.PLUS_K, TEST_PRECISION);
        final RegionBSPTree1S tree = AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI, TEST_PRECISION).toTree();

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, tree);

        final GreatCircle splitter = GreatCircles.fromPole(Vector3D.Unit.from(-1, 0, -1), TEST_PRECISION);

        // act
        final Split<EmbeddedTreeGreatCircleSubset> split = sub.split(splitter);

        // assert
        Assert.assertEquals(SplitLocation.MINUS, split.getLocation());

        final EmbeddedTreeGreatCircleSubset minus = split.getMinus();
        Assert.assertSame(sub, minus);

        final EmbeddedTreeGreatCircleSubset plus = split.getPlus();
        Assert.assertNull(plus);
    }

    @Test
    public void testSplit_plus() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_J, Point2S.PLUS_K, TEST_PRECISION);
        final RegionBSPTree1S tree = AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI, TEST_PRECISION).toTree();

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, tree);

        final GreatCircle splitter = GreatCircles.fromPole(Vector3D.Unit.PLUS_Z, TEST_PRECISION);

        // act
        final Split<EmbeddedTreeGreatCircleSubset> split = sub.split(splitter);

        // assert
        Assert.assertEquals(SplitLocation.PLUS, split.getLocation());

        final EmbeddedTreeGreatCircleSubset minus = split.getMinus();
        Assert.assertNull(minus);

        final EmbeddedTreeGreatCircleSubset plus = split.getPlus();
        Assert.assertSame(sub, plus);
    }

    @Test
    public void testSplit_parallelAndAntiparallel() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final RegionBSPTree1S tree = AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI, TEST_PRECISION).toTree();

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle, tree);

        // act/assert
        Assert.assertEquals(SplitLocation.NEITHER,
                sub.split(GreatCircles.fromPole(Vector3D.Unit.PLUS_Z, TEST_PRECISION)).getLocation());
        Assert.assertEquals(SplitLocation.NEITHER,
                sub.split(GreatCircles.fromPole(Vector3D.Unit.MINUS_Z, TEST_PRECISION)).getLocation());
    }

    @Test
    public void testAdd_arc() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.MINUS_K, Point2S.MINUS_J, TEST_PRECISION);
        final GreatCircle closeCircle = GreatCircles.fromPoints(Point2S.MINUS_K,
                Point2S.of((1.5 * PlaneAngleRadians.PI) - 1e-11, PlaneAngleRadians.PI_OVER_TWO), TEST_PRECISION);

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle);

        // act
        sub.add(circle.arc(Point2S.of(1.5 * PlaneAngleRadians.PI, 0.75 * PlaneAngleRadians.PI), Point2S.MINUS_J));
        sub.add(closeCircle.arc(Point2S.PLUS_J, Point2S.of(1.5 * PlaneAngleRadians.PI, 0.75 * PlaneAngleRadians.PI)));

        // assert
        final List<GreatArc> arcs = sub.toConvex();

        Assert.assertEquals(1, arcs.size());
        checkArc(arcs.get(0), Point2S.PLUS_J, Point2S.MINUS_J);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_arc_differentCircle() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.MINUS_K, Point2S.MINUS_J, TEST_PRECISION);
        final GreatCircle otherCircle = GreatCircles.fromPoints(Point2S.MINUS_K,
                Point2S.of((1.5 * PlaneAngleRadians.PI) - 1e-2, PlaneAngleRadians.PI_OVER_TWO), TEST_PRECISION);

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle);

        // act/assert
        sub.add(otherCircle.arc(Point2S.PLUS_J, Point2S.of(1.5 * PlaneAngleRadians.PI, 0.75 * PlaneAngleRadians.PI)));
    }

    @Test
    public void testAdd_subGreatCircle() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.MINUS_K, Point2S.MINUS_J, TEST_PRECISION);
        final GreatCircle closeCircle = GreatCircles.fromPoints(Point2S.MINUS_K,
                Point2S.of((1.5 * PlaneAngleRadians.PI) - 1e-11, PlaneAngleRadians.PI_OVER_TWO), TEST_PRECISION);

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle);

        final RegionBSPTree1S regionA = RegionBSPTree1S.empty();
        regionA.add(AngularInterval.of(PlaneAngleRadians.PI, 1.25 * PlaneAngleRadians.PI, TEST_PRECISION));
        regionA.add(AngularInterval.of(0.25 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));

        final RegionBSPTree1S regionB = RegionBSPTree1S.empty();
        regionB.add(AngularInterval.of(1.5 * PlaneAngleRadians.PI, 0.25 * PlaneAngleRadians.PI, TEST_PRECISION));

        // act
        sub.add(new EmbeddedTreeGreatCircleSubset(circle, regionA));
        sub.add(new EmbeddedTreeGreatCircleSubset(closeCircle, regionB));

        // assert
        final List<GreatArc> arcs = sub.toConvex();

        Assert.assertEquals(2, arcs.size());
        checkArc(arcs.get(0), Point2S.of(PlaneAngleRadians.PI_OVER_TWO, 0), Point2S.of(PlaneAngleRadians.PI_OVER_TWO, 0.25 * PlaneAngleRadians.PI));
        checkArc(arcs.get(1), Point2S.PLUS_J, Point2S.MINUS_J);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_subGreatCircle_otherCircle() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.MINUS_K, Point2S.MINUS_J, TEST_PRECISION);
        final GreatCircle otherCircle = GreatCircles.fromPoints(Point2S.MINUS_K,
                Point2S.of((1.5 * PlaneAngleRadians.PI) - 1e-5, PlaneAngleRadians.PI_OVER_TWO), TEST_PRECISION);

        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle);

        // act/assert
        sub.add(new EmbeddedTreeGreatCircleSubset(otherCircle, RegionBSPTree1S.full()));
    }

    @Test
    public void testToString() {
        // arrange
        final GreatCircle circle = GreatCircles.fromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final EmbeddedTreeGreatCircleSubset sub = new EmbeddedTreeGreatCircleSubset(circle);

        // act
        final String str = sub.toString();

        // assert
        GeometryTestUtils.assertContains("EmbeddedTreeGreatCircleSubset[", str);
        GeometryTestUtils.assertContains("circle= GreatCircle[", str);
        GeometryTestUtils.assertContains("region= RegionBSPTree1S[", str);
    }

    private static void checkClassify(final HyperplaneSubset<Point2S> sub, final RegionLocation loc, final Point2S... pts) {
        for (final Point2S pt : pts) {
            Assert.assertEquals("Unexpected location for point " + pt, loc, sub.classify(pt));
        }
    }

    private static void checkArc(final GreatArc arc, final Point2S start, final Point2S end) {
        SphericalTestUtils.assertPointsEq(start, arc.getStartPoint(), TEST_EPS);
        SphericalTestUtils.assertPointsEq(end, arc.getEndPoint(), TEST_EPS);
    }
}
