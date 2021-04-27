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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.spherical.SphericalTestUtils;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class AbstractGreatArcPathConnectorTest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    private static final GreatCircle XY_PLANE = GreatCircles.fromPoleAndU(
            Vector3D.Unit.PLUS_Z, Vector3D.Unit.PLUS_X, TEST_PRECISION);

    private static final GreatCircle XZ_PLANE = GreatCircles.fromPoleAndU(
            Vector3D.Unit.PLUS_Y, Vector3D.Unit.PLUS_X, TEST_PRECISION);

    private final TestConnector connector = new TestConnector();

    @Test
    public void testConnectAll_emptyCollection() {
        // act
        final List<GreatArcPath> paths = connector.connectAll(Collections.emptyList());

        // assert
        Assert.assertEquals(0, paths.size());
    }

    @Test
    public void testConnectAll_singleFullArc() {
        // act
        connector.add(Collections.singletonList(XY_PLANE.span()));
        final List<GreatArcPath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(1, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(1, a.getArcs().size());
        Assert.assertSame(XY_PLANE, a.getStartArc().getCircle());
    }

    @Test
    public void testConnectAll_twoFullArcs() {
        // act
        connector.add(XZ_PLANE.span());
        final List<GreatArcPath> paths = connector.connectAll(Collections.singletonList(XY_PLANE.span()));

        // assert
        Assert.assertEquals(2, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(1, a.getArcs().size());
        Assert.assertSame(XY_PLANE, a.getStartArc().getCircle());

        final GreatArcPath b = paths.get(1);
        Assert.assertEquals(1, b.getArcs().size());
        Assert.assertSame(XZ_PLANE, b.getStartArc().getCircle());
    }

    @Test
    public void testConnectAll_singleLune() {
        // arrange
        final GreatCircle upperBound = GreatCircles.fromPoleAndU(
                Vector3D.of(0, 1, -1), Vector3D.Unit.PLUS_X, TEST_PRECISION);

        connector.add(XY_PLANE.arc(0, PlaneAngleRadians.PI));
        connector.add(upperBound.arc(PlaneAngleRadians.PI, 0));

        // act
        final List<GreatArcPath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(1, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(2, a.getArcs().size());
        Assert.assertSame(XY_PLANE, a.getStartArc().getCircle());
        Assert.assertSame(upperBound, a.getEndArc().getCircle());
    }

    @Test
    public void testConnectAll_singleLune_pathsNotOrientedCorrectly() {
        // arrange
        final GreatCircle upperBound = GreatCircles.fromPoleAndU(
                Vector3D.of(0, 1, -1), Vector3D.Unit.PLUS_X, TEST_PRECISION);

        connector.add(XY_PLANE.arc(0, PlaneAngleRadians.PI));
        connector.add(upperBound.arc(0, PlaneAngleRadians.PI));

        // act
        final List<GreatArcPath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(2, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(1, a.getArcs().size());
        Assert.assertSame(XY_PLANE, a.getStartArc().getCircle());

        final GreatArcPath b = paths.get(1);
        Assert.assertEquals(1, b.getArcs().size());
        Assert.assertSame(upperBound, b.getStartArc().getCircle());
    }

    @Test
    public void testConnectAll_largeTriangle() {
        // arrange
        final Point2S p1 = Point2S.PLUS_I;
        final Point2S p2 = Point2S.PLUS_J;
        final Point2S p3 = Point2S.PLUS_K;

        // act
        final List<GreatArcPath> paths = connector.connectAll(Arrays.asList(
                    GreatCircles.arcFromPoints(p1, p2, TEST_PRECISION),
                    GreatCircles.arcFromPoints(p2, p3, TEST_PRECISION),
                    GreatCircles.arcFromPoints(p3, p1, TEST_PRECISION)
                ));

        // assert
        Assert.assertEquals(1, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(3, a.getArcs().size());

        assertPathPoints(a, p3, p1, p2, p3);
    }

    @Test
    public void testConnectAll_smallTriangleWithDisconnectedLuneAndArc() {
        // arrange
        final Point2S p1 = Point2S.of(0, 0);
        final Point2S p2 = Point2S.of(0, 0.1 * PlaneAngleRadians.PI);
        final Point2S p3 = Point2S.of(0.1, 0.1 * PlaneAngleRadians.PI);

        final GreatArc luneEdge1 = GreatCircles.fromPoints(
                    Point2S.PLUS_J,
                    Point2S.MINUS_I,
                    TEST_PRECISION)
                .arc(0, PlaneAngleRadians.PI);
        final GreatArc luneEdge2 = GreatCircles.fromPoints(
                    Point2S.MINUS_J,
                    Point2S.of(PlaneAngleRadians.PI_OVER_TWO, 0.4 * PlaneAngleRadians.PI),
                    TEST_PRECISION)
                .arc(0, PlaneAngleRadians.PI);

        final GreatArc separateArc = GreatCircles.arcFromPoints(
                Point2S.of(-PlaneAngleRadians.PI_OVER_TWO, 0.7 * PlaneAngleRadians.PI),
                Point2S.of(-PlaneAngleRadians.PI_OVER_TWO, 0.8 * PlaneAngleRadians.PI),
                TEST_PRECISION);

        // act
        final List<GreatArcPath> paths = connector.connectAll(Arrays.asList(
                    luneEdge1,
                    GreatCircles.arcFromPoints(p2, p3, TEST_PRECISION),
                    separateArc,
                    GreatCircles.arcFromPoints(p1, p2, TEST_PRECISION),
                    GreatCircles.arcFromPoints(p3, p1, TEST_PRECISION),
                    luneEdge2
                ));

        // assert
        Assert.assertEquals(3, paths.size());

        final GreatArcPath triangle = paths.get(0);
        Assert.assertEquals(3, triangle.getArcs().size());
        assertPathPoints(triangle, p1, p2, p3, p1);

        final GreatArcPath lune = paths.get(1);
        Assert.assertEquals(2, lune.getArcs().size());
        Assert.assertSame(luneEdge1, lune.getStartArc());
        Assert.assertSame(luneEdge2, lune.getEndArc());

        final GreatArcPath separate = paths.get(2);
        Assert.assertEquals(1, separate.getArcs().size());
        Assert.assertSame(separateArc, separate.getStartArc());
    }

    @Test
    public void testConnectAll_choosesBestPointLikeConnection() {
        // arrange
        final DoublePrecisionContext precision = new EpsilonDoublePrecisionContext(1e-1);

        final Point2S p1 = Point2S.PLUS_I;
        final Point2S p2 = Point2S.of(1, PlaneAngleRadians.PI_OVER_TWO);
        final Point2S p3 = Point2S.of(1.001, 0.491 * PlaneAngleRadians.PI);
        final Point2S p4 = Point2S.of(1.001, 0.502 * PlaneAngleRadians.PI);

        connector.add(GreatCircles.arcFromPoints(p2, p3, TEST_PRECISION));
        connector.add(GreatCircles.arcFromPoints(p2, p4, TEST_PRECISION));
        connector.add(GreatCircles.arcFromPoints(p1, p2, precision));

        // act
        final List<GreatArcPath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(2, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(2, a.getArcs().size());
        assertPathPoints(a, p1, p2, p4);

        final GreatArcPath b = paths.get(1);
        Assert.assertEquals(1, b.getArcs().size());
        assertPathPoints(b, p2, p3);
    }

    @Test
    public void testConnect() {
        // arrange
        final GreatArc arcA = GreatCircles.arcFromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final GreatArc arcB = GreatCircles.arcFromPoints(Point2S.PLUS_J, Point2S.MINUS_I, TEST_PRECISION);
        final GreatArc arcC = GreatCircles.arcFromPoints(Point2S.PLUS_J, Point2S.PLUS_K, TEST_PRECISION);

        // act
        connector.connect(Arrays.asList(
                    arcB,
                    arcA
                ));

        connector.connect(Collections.singletonList(arcC));

        final List<GreatArcPath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(2, paths.size());

        final GreatArcPath a = paths.get(0);
        Assert.assertEquals(2, a.getArcs().size());
        assertPathPoints(a, Point2S.PLUS_I, Point2S.PLUS_J, Point2S.MINUS_I);

        final GreatArcPath b = paths.get(1);
        Assert.assertEquals(1, b.getArcs().size());
        assertPathPoints(b, Point2S.PLUS_J, Point2S.PLUS_K);
    }

    @Test
    public void testConnectorCanBeReused() {
        // arrange
        final GreatArc a = GreatCircles.arcFromPoints(Point2S.PLUS_I, Point2S.PLUS_J, TEST_PRECISION);
        final GreatArc b = GreatCircles.arcFromPoints(Point2S.MINUS_I, Point2S.MINUS_J, TEST_PRECISION);

        // act
        final List<GreatArcPath> path1 = connector.connectAll(Collections.singletonList(a));
        final List<GreatArcPath> path2 = connector.connectAll(Collections.singletonList(b));

        // assert
        Assert.assertEquals(1, path1.size());
        assertPathPoints(path1.get(0), Point2S.PLUS_I, Point2S.PLUS_J);

        Assert.assertEquals(1, path2.size());
        assertPathPoints(path2.get(0), Point2S.MINUS_I, Point2S.MINUS_J);
    }

    private static void assertPathPoints(final GreatArcPath path, final Point2S... points) {
        final List<Point2S> expectedPoints = Arrays.asList(points);
        final List<Point2S> actualPoints = path.getVertices();

        final String msg = "Expected path points to equal " + expectedPoints + " but was " + actualPoints;
        Assert.assertEquals(msg, expectedPoints.size(), actualPoints.size());

        for (int i = 0; i < expectedPoints.size(); ++i) {
            SphericalTestUtils.assertPointsEq(expectedPoints.get(i), actualPoints.get(i), TEST_EPS);
        }
    }

    private static class TestConnector extends AbstractGreatArcConnector {

        @Override
        protected ConnectableGreatArc selectConnection(final ConnectableGreatArc incoming,
                                                       final List<ConnectableGreatArc> outgoing) {

            // just choose the first element
            return outgoing.get(0);
        }
    }
}
