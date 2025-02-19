package com.github.davidmoten.rtree2.geometry;

import org.junit.Test;

import static com.github.davidmoten.rtree2.geometry.Intersects.geometryIntersectsPolygon;
import static org.junit.Assert.*;

public final class PolygonTest {

    private static final double PRECISION = 0.00001;
    private static final double[] SIMPLE_SQUARE = {-1, -1, -1, 1, 1, 1, 1, -1};
    private static final double[] SIMPLE_SQUARE_CLOSED = {-1, -1, -1, 1, 1, 1, 1, -1, -1, -1};
    private static final double[] SIMPLE_SQUARE_DUPLICATES = {-3, 0, -3, 0, -3, 0, 1, 4, 2, -5, -2, -10, -2, -10};

    @Test
    public void testDoesIntersectHorizontalLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-2, 0, 2, 0);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectVerticalLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(0.5, -5.0, 0.5, 10.1);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectArbitraryLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-1.2, 5.0, 0.5, -2.5);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectArbitraryLineClosedPolygon() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE_CLOSED);
        Line b = Geometries.line(-1.2, 5.0, 0.5, -2.5);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesIntersectContainedLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-0.2, 0.5, 0.2, -0.5);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectHorizontalLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-0.5, 5, 0.5, 5);
        assertFalse(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectVerticalLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-4, 0, -4, 5);
        assertFalse(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectArbitraryLine() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(0.1, 2.2, 10.7, 3.1);
        assertFalse(a.intersects(b));
    }

    @Test
    public void testLineIsNotInfinite() {
        // Check that line is treated like a segment rather than an infinite line
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(0.5, 5.0, 0.5, 10.1);
        assertFalse(geometryIntersectsPolygon.test(b, a));
    }

    @Test
    public void testDoesIntersectPoint() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE_DUPLICATES);
        Point b = Geometries.point(0.5, 1.2);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testEdgeDoesTouchPoint() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Point b = Geometries.point(0.5, 1.0);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testLastEdgeDoesTouchPoint() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Point b = Geometries.point(0.3, -1);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectPoint() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE_DUPLICATES);
        Point b = Geometries.point(-2.5, 3.2);
        assertFalse(geometryIntersectsPolygon.test(b, a));
    }

    @Test
    public void testOddPoints() {
        assertThrows(IllegalArgumentException.class, () -> Geometries.polygon(new double[] {1, 1, 1, 0, 0, 0, 5}));
    }

    @Test
    public void testNotEnoughPoints() {
        assertThrows(IllegalArgumentException.class, () -> Geometries.polygon(new double[] {1, 1, 1, 0}));
    }

    @Test
    public void testNotEnoughPointsClosedLine() {
        assertThrows(IllegalArgumentException.class, () -> Geometries.polygon(new double[] {1, 1, 1, 0, 1, 1}));
    }

    @Test
    public void testNotEnoughPointsAfterDuplicates() {
        assertThrows(IllegalArgumentException.class, () -> Geometries.polygon(new double[] {1, 1, 1, 1, 1, 0}));
    }

    @Test
    public void testDoublePrecision() {
        assertTrue(Geometries.polygon(SIMPLE_SQUARE).isDoublePrecision());
    }

    @Test
    public void testEqualsTrue() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Polygon b = Geometries.polygon(SIMPLE_SQUARE);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testEqualsFalse() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Polygon b = Geometries.polygon(SIMPLE_SQUARE_DUPLICATES);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testEqualsOtherType() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE);
        Line b = Geometries.line(-1.0, -1.0, -1.0, 1.0);
        assertNotEquals(a, b);
    }

    @Test
    public void testPolygonMbr() {
        Polygon a = Geometries.polygon(SIMPLE_SQUARE_DUPLICATES);
        Rectangle mbr = a.mbr();
        assertEquals(-3, mbr.x1(), PRECISION);
        assertEquals(-10, mbr.y1(), PRECISION);
        assertEquals(2, mbr.x2(), PRECISION);
        assertEquals(4, mbr.y2(), PRECISION);
    }

    @Test
    public void testUnsupportedOperations() {
        Polygon p = Geometries.polygon(SIMPLE_SQUARE);
        Polygon p2 = Geometries.polygon(SIMPLE_SQUARE_DUPLICATES);
        Rectangle r = Geometries.rectangle(0, 0, 1, 1);
        Circle c = Geometries.circle(1, 10, 5);
        assertThrows(UnsupportedOperationException.class, () -> geometryIntersectsPolygon.test(r, p));
        assertThrows(UnsupportedOperationException.class, () -> geometryIntersectsPolygon.test(c, p));
        assertThrows(UnsupportedOperationException.class, () -> p.distance(r));
        assertThrows(UnsupportedOperationException.class, () -> geometryIntersectsPolygon.test(p, p2));
    }

    @Test
    public void testConvexPolygonLimitation() {
        assertThrows(UnsupportedOperationException.class, () ->
                Geometries.polygon(new double[] {0, 0, 0, 1, 0.5, 0.5, 1, 1, 1, 0}));
    }
}
