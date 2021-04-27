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
package org.apache.commons.geometry.euclidean.twod.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.EuclideanTestUtils;
import org.apache.commons.geometry.euclidean.twod.Line;
import org.apache.commons.geometry.euclidean.twod.LineConvexSubset;
import org.apache.commons.geometry.euclidean.twod.Lines;
import org.apache.commons.geometry.euclidean.twod.Segment;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.geometry.euclidean.twod.path.AbstractLinePathConnector.ConnectableLineSubset;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class AbstractLinePathConnectorTest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    private static final Line Y_AXIS = Lines.fromPointAndAngle(Vector2D.ZERO, PlaneAngleRadians.PI_OVER_TWO,
            TEST_PRECISION);

    private final TestConnector connector = new TestConnector();

    @Test
    public void testConnectAll_emptyCollection() {
        // act
        final List<LinePath> paths = connector.connectAll(Collections.emptyList());

        // assert
        Assert.assertEquals(0, paths.size());
    }

    @Test
    public void testConnectAll_singleInfiniteLine() {
        // arrange
        final LineConvexSubset segment = Y_AXIS.span();

        // act
        final List<LinePath> paths = connector.connectAll(Collections.singletonList(segment));

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertEquals(1, path.getElements().size());
        Assert.assertSame(segment, path.getStart());
    }

    @Test
    public void testConnectAll_singleHalfInfiniteLine_noEndPoint() {
        // arrange
        final LineConvexSubset segment = Y_AXIS.rayFrom(Vector2D.ZERO);

        // act
        final List<LinePath> paths = connector.connectAll(Collections.singletonList(segment));

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertEquals(1, path.getElements().size());
        Assert.assertSame(segment, path.getStart());
    }

    @Test
    public void testConnectAll_singleHalfInfiniteLine_noStartPoint() {
        // arrange
        final LineConvexSubset segment = Y_AXIS.reverseRayTo(Vector2D.ZERO);

        // act
        final List<LinePath> paths = connector.connectAll(Collections.singletonList(segment));

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertEquals(1, path.getElements().size());
        Assert.assertSame(segment, path.getStart());
    }

    @Test
    public void testConnectAll_disjointSegments() {
        // arrange
        final LineConvexSubset a = Y_AXIS.segment(Vector2D.of(0, 1), Vector2D.of(0, 2));
        final LineConvexSubset b = Y_AXIS.segment(Vector2D.of(0, -1), Vector2D.ZERO);

        final List<LineConvexSubset> segments = Arrays.asList(a, b);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(2, paths.size());

        assertFinitePath(paths.get(0), Vector2D.of(0, -1), Vector2D.ZERO);
        assertFinitePath(paths.get(1), Vector2D.of(0, 1), Vector2D.of(0, 2));
    }

    @Test
    public void testConnectAll_singleClosedPath() {
        // arrange
        final LinePath input = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(1, 1), Vector2D.ZERO, Vector2D.of(1, 0))
                .close();

        final List<LineConvexSubset> segments = new ArrayList<>(input.getElements());
        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        assertFinitePath(paths.get(0),
                Vector2D.ZERO, Vector2D.of(1, 0), Vector2D.of(1, 1), Vector2D.ZERO);
    }

    @Test
    public void testConnectAll_multipleClosedPaths() {
        // arrange
        final LinePath a = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(1, 1), Vector2D.ZERO, Vector2D.of(1, 0))
                .close();

        final LinePath b = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(0, 1), Vector2D.of(-1, 0), Vector2D.of(-0.5, 0))
                .close();

        final LinePath c = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(1, 3), Vector2D.of(0, 2), Vector2D.of(1, 2))
                .close();

        final List<LineConvexSubset> segments = new ArrayList<>();
        segments.addAll(a.getElements());
        segments.addAll(b.getElements());
        segments.addAll(c.getElements());

        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(3, paths.size());

        assertFinitePath(paths.get(0),
                Vector2D.of(-1, 0), Vector2D.of(-0.5, 0), Vector2D.of(0, 1), Vector2D.of(-1, 0));

        assertFinitePath(paths.get(1),
                Vector2D.ZERO, Vector2D.of(1, 0), Vector2D.of(1, 1), Vector2D.ZERO);

        assertFinitePath(paths.get(2),
                Vector2D.of(0, 2), Vector2D.of(1, 2), Vector2D.of(1, 3), Vector2D.of(0, 2));
    }

    @Test
    public void testConnectAll_singleOpenPath() {
        // arrange
        final LinePath input = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(1, 1), Vector2D.ZERO, Vector2D.of(1, 0))
                .build();

        final List<LineConvexSubset> segments = new ArrayList<>(input.getElements());
        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        assertFinitePath(paths.get(0),
                Vector2D.of(1, 1), Vector2D.ZERO, Vector2D.of(1, 0));
    }

    @Test
    public void testConnectAll_mixOfOpenConnectedAndInfinite() {
        // arrange
        final LineConvexSubset inputYInf = Y_AXIS.reverseRayTo(Vector2D.ZERO);
        final LineConvexSubset inputXInf = Lines.fromPoints(Vector2D.ZERO, Vector2D.Unit.MINUS_X, TEST_PRECISION)
                .rayFrom(Vector2D.ZERO);

        final LinePath closedPath = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(0, 2), Vector2D.of(1, 2), Vector2D.of(1, 3))
                .close();

        final LinePath openPath = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(-1, 3), Vector2D.of(0, 1), Vector2D.of(1, 1))
                .build();

        final List<LineConvexSubset> segments = new ArrayList<>();
        segments.add(inputYInf);
        segments.add(inputXInf);
        segments.addAll(closedPath.getElements());
        segments.addAll(openPath.getElements());

        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(3, paths.size());

        assertFinitePath(paths.get(0),
                Vector2D.of(-1, 3), Vector2D.of(0, 1), Vector2D.of(1, 1));

        final LinePath infPath = paths.get(1);
        Assert.assertTrue(infPath.isInfinite());
        Assert.assertEquals(2, infPath.getElements().size());
        Assert.assertSame(inputYInf, infPath.getElements().get(0));
        Assert.assertSame(inputXInf, infPath.getElements().get(1));

        assertFinitePath(paths.get(2),
                Vector2D.of(0, 2), Vector2D.of(1, 2), Vector2D.of(1, 3), Vector2D.of(0, 2));
    }

    @Test
    public void testConnectAll_pathWithSinglePoint() {
        // arrange
        final Vector2D p0 = Vector2D.ZERO;

        final List<LineConvexSubset> segments = Collections.singletonList(Lines.fromPointAndAngle(p0, 0, TEST_PRECISION).segment(p0, p0));

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        assertFinitePath(paths.get(0), p0, p0);
    }

    @Test
    public void testConnectAll_pathWithPointLikeConnectedSegments() {
        // arrange
        final Vector2D p0 = Vector2D.ZERO;
        final Vector2D p1 = Vector2D.of(1, 0);
        final Vector2D p2 = Vector2D.of(1, 1);

        final Vector2D almostP0 = Vector2D.of(-1e-20, -1e-20);
        final Vector2D almostP1 = Vector2D.of(1 - 1e-15, 0);

        final LinePath input = LinePath.builder(TEST_PRECISION)
                .appendVertices(p0, p1)
                .append(Lines.fromPointAndAngle(p1, 0.25 * PlaneAngleRadians.PI, TEST_PRECISION).segment(p1, p1))
                .append(Lines.fromPointAndAngle(p1, -0.25 * PlaneAngleRadians.PI, TEST_PRECISION).segment(almostP1, almostP1))
                .append(p2)
                .append(p0)
                .append(Lines.fromPointAndAngle(Vector2D.ZERO, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)
                        .segment(almostP0, almostP0))
                .build();

        final List<LineConvexSubset> segments = new ArrayList<>(input.getElements());
        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        assertFinitePath(paths.get(0), p0, p1, almostP1, p1, p2, p0, almostP0);
    }

    @Test
    public void testConnectAll_flatLineRegion() {
        // arrange
        final Vector2D p0 = Vector2D.ZERO;
        final Vector2D p1 = Vector2D.of(1, 0);

        final Segment seg0 = Lines.segmentFromPoints(p0, p1, TEST_PRECISION);
        final Segment seg1 = Lines.segmentFromPoints(p1, p0, TEST_PRECISION);
        final LineConvexSubset seg2 = Lines.fromPointAndAngle(p1, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION).segment(p1, p1);
        final LineConvexSubset seg3 = Lines.fromPointAndAngle(p0, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION).segment(p0, p0);

        final List<LineConvexSubset> segments = new ArrayList<>(Arrays.asList(seg0, seg1, seg2, seg3));
        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertSame(seg0, path.getElements().get(0));
        Assert.assertSame(seg2, path.getElements().get(1));
        Assert.assertSame(seg1, path.getElements().get(2));
        Assert.assertSame(seg3, path.getElements().get(3));
    }

    @Test
    public void testConnectAll_singlePointRegion() {
        // arrange
        final Vector2D p0 = Vector2D.of(1, 0);

        final LineConvexSubset seg0 = Lines.fromPointAndAngle(p0, 0.0, TEST_PRECISION).segment(p0, p0);
        final LineConvexSubset seg1 = Lines.fromPointAndAngle(p0, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION).segment(p0, p0);
        final LineConvexSubset seg2 = Lines.fromPointAndAngle(p0, PlaneAngleRadians.PI, TEST_PRECISION).segment(p0, p0);
        final LineConvexSubset seg3 = Lines.fromPointAndAngle(p0, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION).segment(p0, p0);

        final List<LineConvexSubset> segments = new ArrayList<>(Arrays.asList(seg0, seg1, seg2, seg3));
        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertSame(seg2, path.getElements().get(0));
        Assert.assertSame(seg3, path.getElements().get(1));
        Assert.assertSame(seg0, path.getElements().get(2));
        Assert.assertSame(seg1, path.getElements().get(3));
    }

    @Test
    public void testConnectAll_pathWithPointLikeUnconnectedSegments() {
        // arrange
        final Vector2D p0 = Vector2D.ZERO;
        final Vector2D p1 = Vector2D.of(1, 0);

        final LineConvexSubset seg0 = Lines.fromPointAndAngle(p1, 0.0, TEST_PRECISION).segment(p1, p1);
        final LineConvexSubset seg1 = Lines.fromPointAndAngle(p1, 0.25 * PlaneAngleRadians.PI, TEST_PRECISION).segment(p1, p1);
        final LineConvexSubset seg2 = Lines.fromPointAndAngle(p0, 0, TEST_PRECISION).segment(p0, p0);

        final List<LineConvexSubset> segments = new ArrayList<>(Arrays.asList(seg0, seg1, seg2));

        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(2, paths.size());

        final LinePath path0 = paths.get(0);
        Assert.assertEquals(1, path0.getElements().size());
        Assert.assertSame(seg2, path0.getElements().get(0));

        final LinePath path1 = paths.get(1);
        Assert.assertEquals(2, path1.getElements().size());
        Assert.assertSame(seg0, path1.getElements().get(0));
        Assert.assertSame(seg1, path1.getElements().get(1));
    }

    @Test
    public void testConnectAll_pathStartingWithPoint() {
        // arrange
        final Vector2D p0 = Vector2D.ZERO;
        final Vector2D p1 = Vector2D.of(1, 0);
        final Vector2D p2 = Vector2D.of(1, 1);

        final LineConvexSubset seg0 = Lines.fromPointAndAngle(p0, PlaneAngleRadians.PI, TEST_PRECISION).segment(p0, p0);
        final LineConvexSubset seg1 = Lines.segmentFromPoints(p0, p1, TEST_PRECISION);
        final LineConvexSubset seg2 = Lines.segmentFromPoints(p1, p2, TEST_PRECISION);

        final List<LineConvexSubset> segments = new ArrayList<>(Arrays.asList(seg0, seg1, seg2));

        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(1, paths.size());

        final LinePath path = paths.get(0);
        Assert.assertSame(seg0, path.getElements().get(0));
        Assert.assertSame(seg1, path.getElements().get(1));
        Assert.assertSame(seg2, path.getElements().get(2));
    }

    @Test
    public void testConnectAll_intersectingPaths() {
        // arrange
        final LinePath a = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(-1, 1), Vector2D.of(0.5, 0), Vector2D.of(-1, -1))
                .build();

        final LinePath b = LinePath.builder(TEST_PRECISION)
                .appendVertices(Vector2D.of(1, 1), Vector2D.of(-0.5, 0), Vector2D.of(1, -1))
                .build();

        final List<LineConvexSubset> segments = new ArrayList<>();
        segments.addAll(a.getElements());
        segments.addAll(b.getElements());

        shuffle(segments);

        // act
        final List<LinePath> paths = connector.connectAll(segments);

        // assert
        Assert.assertEquals(2, paths.size());

        assertFinitePath(paths.get(0),
                Vector2D.of(-1, 1), Vector2D.of(0.5, 0), Vector2D.of(-1, -1));

        assertFinitePath(paths.get(1),
                Vector2D.of(1, 1), Vector2D.of(-0.5, 0), Vector2D.of(1, -1));
    }

    @Test
    public void testInstancesCanBeReused() {
        // arrange
        final LineConvexSubset a = Lines.segmentFromPoints(Vector2D.ZERO, Vector2D.Unit.PLUS_X, TEST_PRECISION);
        final LineConvexSubset b = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.Unit.PLUS_Y, TEST_PRECISION);

        // act
        final List<LinePath> firstPaths = connector.connectAll(Collections.singletonList(a));
        final List<LinePath> secondPaths = connector.connectAll(Collections.singletonList(b));

        // assert
        Assert.assertEquals(1, firstPaths.size());
        Assert.assertEquals(1, secondPaths.size());

        Assert.assertSame(a, firstPaths.get(0).getElements().get(0));
        Assert.assertSame(b, secondPaths.get(0).getElements().get(0));
    }

    @Test
    public void testAdd() {
        // arrange
        final LineConvexSubset a = Lines.segmentFromPoints(Vector2D.ZERO, Vector2D.Unit.PLUS_X, TEST_PRECISION);
        final LineConvexSubset b = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(1, 1), TEST_PRECISION);
        final LineConvexSubset c = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(2, 0), TEST_PRECISION);

        // act
        connector.add(Arrays.asList(a, b));
        connector.add(Collections.singletonList(c));

        final List<LinePath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(2, paths.size());

        assertFinitePath(paths.get(0), Vector2D.ZERO, Vector2D.Unit.PLUS_X, Vector2D.of(2, 0));
        assertFinitePath(paths.get(1), Vector2D.Unit.PLUS_X, Vector2D.of(1, 1));
    }

    @Test
    public void testConnect() {
        // arrange
        final LineConvexSubset a = Lines.segmentFromPoints(Vector2D.ZERO, Vector2D.Unit.PLUS_X, TEST_PRECISION);
        final LineConvexSubset b = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(1, 1), TEST_PRECISION);
        final LineConvexSubset c = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(2, 0), TEST_PRECISION);

        // act
        connector.connect(Arrays.asList(a, b));
        connector.connect(Collections.singletonList(c));

        final List<LinePath> paths = connector.connectAll();

        // assert
        Assert.assertEquals(2, paths.size());

        assertFinitePath(paths.get(0), Vector2D.ZERO, Vector2D.Unit.PLUS_X, Vector2D.of(1, 1));
        assertFinitePath(paths.get(1), Vector2D.Unit.PLUS_X, Vector2D.of(2, 0));
    }

    @Test
    public void testConnectableSegment_hashCode() {
        // arrange
        final LineConvexSubset segA = Lines.segmentFromPoints(Vector2D.ZERO, Vector2D.Unit.PLUS_X, TEST_PRECISION);
        final LineConvexSubset segB = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(1, 1), TEST_PRECISION);

        final ConnectableLineSubset a = new ConnectableLineSubset(segA);

        // act
        final int hash = a.hashCode();

        // assert
        Assert.assertEquals(hash, a.hashCode());

        Assert.assertNotEquals(hash, new ConnectableLineSubset(segB).hashCode());
        Assert.assertNotEquals(hash, new ConnectableLineSubset(Vector2D.Unit.PLUS_X).hashCode());

        Assert.assertEquals(hash, new ConnectableLineSubset(segA).hashCode());
    }

    @Test
    public void testConnectableSegment_equals() {
        // arrange
        final LineConvexSubset segA = Lines.segmentFromPoints(Vector2D.ZERO, Vector2D.Unit.PLUS_X, TEST_PRECISION);
        final LineConvexSubset segB = Lines.segmentFromPoints(Vector2D.Unit.PLUS_X, Vector2D.of(1, 1), TEST_PRECISION);

        final ConnectableLineSubset a = new ConnectableLineSubset(segA);

        // act/assert
        Assert.assertEquals(a, a);

        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals(new Object()));

        Assert.assertNotEquals(a, new ConnectableLineSubset(segB));
        Assert.assertNotEquals(a, new ConnectableLineSubset(Vector2D.Unit.PLUS_X));

        Assert.assertEquals(a, new ConnectableLineSubset(segA));
    }

    private static List<LineConvexSubset> shuffle(final List<LineConvexSubset> segments) {
        return shuffle(segments, 1);
    }

    private static List<LineConvexSubset> shuffle(final List<LineConvexSubset> segments, final int seed) {
        Collections.shuffle(segments, new Random(seed));

        return segments;
    }

    private static void assertFinitePath(final LinePath path, final Vector2D... vertices) {
        Assert.assertFalse(path.isInfinite());
        Assert.assertTrue(path.isFinite());

        assertPathVertices(path, vertices);
    }

    private static void assertPathVertices(final LinePath path, final Vector2D... vertices) {
        final List<Vector2D> expectedVertices = Arrays.asList(vertices);
        final List<Vector2D> actualVertices = path.getVertexSequence();

        final String msg = "Expected path vertices to equal " + expectedVertices + " but was " + actualVertices;
        Assert.assertEquals(msg, expectedVertices.size(), actualVertices.size());

        for (int i = 0; i < expectedVertices.size(); ++i) {
            EuclideanTestUtils.assertCoordinatesEqual(expectedVertices.get(i), actualVertices.get(i), TEST_EPS);
        }
    }

    private static class TestConnector extends AbstractLinePathConnector {

        @Override
        protected ConnectableLineSubset selectConnection(final ConnectableLineSubset incoming, final List<ConnectableLineSubset> outgoing) {
            // just choose the first element
            return outgoing.get(0);
        }
    }
}
