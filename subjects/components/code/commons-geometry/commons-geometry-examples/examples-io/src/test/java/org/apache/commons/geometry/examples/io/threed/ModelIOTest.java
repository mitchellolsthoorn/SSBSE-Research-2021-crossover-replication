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
package org.apache.commons.geometry.examples.io.threed;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.EuclideanTestUtils;
import org.apache.commons.geometry.euclidean.threed.BoundarySource3D;
import org.apache.commons.geometry.euclidean.threed.Planes;
import org.apache.commons.geometry.euclidean.threed.Triangle3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModelIOTest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testGetHandler() {
        // act
        ModelIOHandlerRegistry registry = ModelIO.getModelIOHandlerRegistry();

        // assert
        Assert.assertTrue(registry instanceof DefaultModelIOHandlerRegistry);
        Assert.assertSame(registry, ModelIO.getModelIOHandlerRegistry());
    }

    @Test
    public void testWriteRead_typeFromFileExtension() throws IOException {
        // act/assert
        checkWriteRead(model -> {
            File file = new File(tempFolder.getRoot(), "model.obj");

            ModelIO.write(model, file);
            return ModelIO.read(file, TEST_PRECISION);
        });
    }

    @Test
    public void testWriteRead_typeAndFile() throws IOException {
        // act/assert
        checkWriteRead(model -> {
            File file = new File(tempFolder.getRoot(), "objmodel");

            ModelIO.write(model, "OBJ", file);
            return ModelIO.read("obj", file, TEST_PRECISION);
        });
    }

    @Test
    public void testWriteRead_typeAndStream() throws IOException {
        // act/assert
        checkWriteRead(model -> {
            File file = new File(tempFolder.getRoot(), "objmodel");

            try (OutputStream out = Files.newOutputStream(file.toPath())) {
                ModelIO.write(model, "OBJ", out);
            }

            try (InputStream in = Files.newInputStream(file.toPath())) {
                return ModelIO.read("OBJ", in, TEST_PRECISION);
            }
        });
    }

    @FunctionalInterface
    private interface ModelIOFunction {
        BoundarySource3D apply(BoundarySource3D model) throws IOException;
    }

    private void checkWriteRead(ModelIOFunction fn) throws IOException {
        // arrange
        BoundarySource3D model = BoundarySource3D.from(
                Planes.triangleFromVertices(Vector3D.ZERO, Vector3D.of(1, 0, 0), Vector3D.of(0, 1, 0), TEST_PRECISION)
            );

        // act
        BoundarySource3D result = fn.apply(model);

        // assert
        List<Triangle3D> tris = result.triangleStream().collect(Collectors.toList());
        Assert.assertEquals(1, tris.size());

        Triangle3D tri = tris.get(0);
        EuclideanTestUtils.assertCoordinatesEqual(Vector3D.ZERO, tri.getPoint1(), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector3D.of(1, 0, 0), tri.getPoint2(), TEST_EPS);
        EuclideanTestUtils.assertCoordinatesEqual(Vector3D.of(0, 1, 0), tri.getPoint3(), TEST_EPS);
    }
}
