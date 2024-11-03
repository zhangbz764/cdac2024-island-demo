package utils;

import igeo.ICurve;
import igeo.IPoint;
import wblut.geom.*;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/3
 * @time 15:15
 */
public class UtilsZBZ {
    public static final WB_GeometryFactory wbgf = new WB_GeometryFactory();

    /**
     * IPoint -> WB_Point
     *
     * @param point input IPoint
     * @return wblut.geom.WB_Point
     */
    public static WB_Point IPointToWB_Point(final IPoint point) {
        return new WB_Point(point.x(), point.y(), point.z());
    }

    /**
     * ICurve -> WB_Geometry (WB_PolyLine, WB_Polygon, WB_Segment)
     *
     * @param curve input ICurve
     * @return wblut.geom.WB_Geometry2D
     */
    public static WB_Geometry2D ICurveToWB(final ICurve curve) {
        if (curve.cpNum() > 2 && !curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createPolyLine(points);
        } else if (curve.cpNum() > 2 && curve.isClosed()) {
            WB_Point[] points = new WB_Point[curve.cpNum()];
            for (int i = 0; i < curve.cpNum(); i++) {
                points[i] = new WB_Point(curve.cp(i).x(), curve.cp(i).y(), curve.cp(i).z());
            }
            return wbgf.createSimplePolygon(points);
        } else if (curve.cpNum() == 2) {
            WB_Point start = new WB_Point(curve.cp(0).x(), curve.cp(0).y(), curve.cp(0).z());
            WB_Point end = new WB_Point(curve.cp(1).x(), curve.cp(1).y(), curve.cp(1).z());
            return new WB_Segment(start, end);
        } else {
            System.out.println("***MAYBE OTHER TYPE OF GEOMETRY***");
            return null;
        }
    }

    /**
     * get the whole length of the polyline (replace the method in HE_Mesh)
     *
     * @param poly polyline / polygon
     * @return double
     */
    public static double getPolyLength(final WB_PolyLine poly) {
        double plLength = 0;
        if (poly instanceof WB_Polygon) {
            WB_Polygon polygon = (WB_Polygon) poly;
            if (polygon.getNumberOfHoles() > 0) {
                // shell
                for (int i = 0; i < polygon.getNumberOfShellPoints() - 1; i++) {
                    plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
                }
                // holes
                int[] npc = polygon.getNumberOfPointsPerContour();
                int currNum = npc[0];
                for (int i = 1; i < npc.length; i++) {
                    for (int j = 0; j < npc[i] - 1; j++) {
                        plLength += poly.getPoint(currNum).getDistance2D(poly.getPoint(currNum + 1));
                        currNum++;
                    }
                    currNum++;
                }
            } else {
                for (int i = 0; i < polygon.getNumberOfShellPoints() - 1; i++) {
                    plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
                }
            }
        } else {
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                plLength += poly.getPoint(i).getDistance2D(poly.getPoint(i + 1));
            }
        }
        return plLength;
    }

    /**
     * given distance. get the point along the polyline (replace the method in HE_Mesh)
     *
     * @param poly polyline
     * @param dist distance
     * @return wblut.geom.WB_Point
     */
    public static double[] getPointOnPolyEdge(final WB_PolyLine poly, final double dist) {
        if (dist <= 0) {
            return new double[]{
                    poly.getPoint(0).xd(), poly.getPoint(0).yd(),
                    poly.getSegment(0).getDirection().xd(),
                    poly.getSegment(0).getDirection().yd(),
            };
        } else if (dist >= getPolyLength(poly)) {
            return new double[]{
                    poly.getPoint(poly.getNumberOfPoints() - 1).xd(), poly.getPoint(poly.getNumberOfPoints() - 1).yd(),
                    poly.getSegment(poly.getNumberOfPoints() - 2).getDirection().xd(),
                    poly.getSegment(poly.getNumberOfPoints() - 2).getDirection().yd(),
            };
        } else {
            double distTemp = dist;
            int finalIndex = 0;

            for (int i = 0; i < poly.getNumberSegments(); i++) {
                double segLength = poly.getSegment(i).getLength();
                if (distTemp > segLength) {
                    distTemp -= segLength;
                } else {
                    finalIndex = i;
                    break;
                }
            }
            WB_Vector v = new WB_Vector(poly.getPoint(finalIndex), poly.getPoint(finalIndex + 1));
            v.normalizeSelf();
            WB_Point p = poly.getPoint(finalIndex).add(v.scale(distTemp));

            return new double[]{
                    p.xd(), p.yd(),
                    poly.getSegment(finalIndex).getDirection().xd(),
                    poly.getSegment(finalIndex).getDirection().yd(),
            };
        }
    }

    /**
     * find the point is on which polygon edge (-1)
     *
     * @param p    input point
     * @param poly input polygon
     * @return int[] - indices of result segment
     */
    public static int[] pointOnWhichEdgeIndices(final WB_Point p, final WB_PolyLine poly) {
        int[] result = new int[]{-1, -1};
        if (poly instanceof WB_Polygon) {
            // polygon
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                WB_Segment seg = new WB_Segment(poly.getPoint(i), poly.getPoint(i + 1));
                if (pointOnSegment(p, seg)) {
                    result[0] = i;
                    result[1] = (i + 1) % (poly.getNumberOfPoints() - 1);
                    if (i != poly.getNumberSegments() - 1 && p.getDistance2D(poly.getPoint(result[1])) < 0.0001) {
                        // if it's not the last segment and the point is on the end of the segment
                        // then move on to next
                        result[0] = (i + 1) % (poly.getNumberOfPoints() - 1);
                        result[1] = (i + 2) % (poly.getNumberOfPoints() - 1);
                    }
                    break;
                }
            }
        } else {
            // polyline
            for (int i = 0; i < poly.getNumberOfPoints() - 1; i++) {
                WB_Segment seg = new WB_Segment(poly.getPoint(i), poly.getPoint(i + 1));
                if (pointOnSegment(p, seg)) {
                    result[0] = i;
                    result[1] = i + 1;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * check a point is on a segment (float error included)
     *
     * @param p   input point
     * @param seg input segment
     * @return boolean
     */
    public static boolean pointOnSegment(final WB_Point p, final WB_Segment seg) {
        double crossValue = WB_CoordOp2D.cross2D(seg.getDirection(), p.sub(seg.getOrigin()));
        if (Math.abs(crossValue) < 0.0001) {
            double minX = Math.min(seg.getOrigin().xd(), seg.getEndpoint().xd());
            double maxX = Math.max(seg.getOrigin().xd(), seg.getEndpoint().xd());
            double minY = Math.min(seg.getOrigin().yd(), seg.getEndpoint().yd());
            double maxY = Math.max(seg.getOrigin().yd(), seg.getEndpoint().yd());
            return minX <= p.xd() && p.xd() <= maxX && minY <= p.yd() && p.yd() <= maxY;
        } else {
            return false;
        }
    }
}
