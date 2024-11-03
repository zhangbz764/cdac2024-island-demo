package org.instaaa;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac-island
 * @date 2024/11/2
 * @time 14:05
 */
public class BlobIsland {
    // BlobDetection instances
    private BlobDetection bd;
    private float blobThreshold = 0.3f;

    // island geometries
    private List<WB_PolyLine> islands; // might have multiple island

    private List<Bridge> bridges;

    private Bridge selectedBridge;

    /* ------------- constructor ------------- */

    public BlobIsland(int imgW, int imgH) {
        this.bd = new BlobDetection(imgW, imgH);
        bd.setPosDiscrimination(false);
        bd.setThreshold(blobThreshold);
    }

    /* ------------- member function ------------- */

    /**
     * update island boundary by BlobDetection
     * update bridges
     *
     * @param pixels
     * @return void
     */
    public void updateBoundaryAndBridges(int[] pixels) {
        this.islands = new ArrayList<>();

        bd.computeBlobs(pixels);
        System.out.println(bd.getBlobNb());

        Blob b;
        EdgeVertex eA;

        for (int n = 0; n < bd.getBlobNb(); n++) {
            List<WB_Point> blobPts = new ArrayList<>();

            b = bd.getBlob(n);

            for (int m = 0; m < b.getEdgeNb(); m++) {
                eA = b.getEdgeVertexA(m);
                if (eA != null) {
                    blobPts.add(new WB_Point(eA.x * bd.imgWidth, eA.y * bd.imgHeight));
                }
            }

            // last one
            EdgeVertex eB = b.getEdgeVertexB(b.getEdgeNb() - 1);
            blobPts.add(new WB_Point(eB.x * bd.imgWidth, eB.y * bd.imgHeight));

            blobPts.add(blobPts.get(0));

            islands.add(new WB_PolyLine(blobPts));
        }

        System.out.println("islands.size()  " + islands.size());
        for (WB_PolyLine island : islands) {
            System.out.println(island.getNumberOfPoints());
        }

        computeBridges(islands.get(0), 3);
    }

    /**
     * compute bridge position and direction by given number
     *
     * @param island
     * @param bridgeNum
     * @return void
     */
    private void computeBridges(WB_PolyLine island, int bridgeNum) {
        this.bridges = new ArrayList<>();

        double length = getPolyLength(island);
        double step = length / bridgeNum;

        for (int i = 0; i < bridgeNum; i++) {
            double[] posAndDir = getPointOnPolyEdge(island, step * i);
            WB_Vector dir = new WB_Vector(posAndDir[2], posAndDir[3]);
            dir.rotateAboutOrigin2DSelf(Math.PI * 0.5);

            bridges.add(new Bridge(
                    new WB_Point(posAndDir[0], posAndDir[1]), new WB_Vector(dir), 15
            ));
        }
    }

    public void updateBridgeByNewPos(double x, double y) {
        WB_Point newPos = new WB_Point(x, y);
        if (selectedBridge == null) {
            // find closest
            for (int i = 0; i < bridges.size(); i++) {
                Bridge bri = bridges.get(i);
                if (bri.getPos().getDistance2D(newPos) <= Bridge.posRadius) {
                    this.selectedBridge = bri;
                    break;
                }
            }
        } else {
            // set new pos
            WB_Point closestPoint2D = WB_GeometryOp2D.getClosestPoint2D(newPos, this.getIsland());
            int[] ids = pointOnWhichEdgeIndices(closestPoint2D, this.getIsland());
            WB_Coord direction = this.getIsland().getSegment(ids[0]).getDirection();
            WB_Vector dir = new WB_Vector(direction.xd(), direction.yd());
            dir.rotateAboutOrigin2DSelf(Math.PI * 0.5);

            selectedBridge.setPosAndDir(closestPoint2D, dir);
        }
    }

    public void clearSelectedBridge() {
        this.selectedBridge = null;
    }

    /* ------------- setter & getter ------------- */

    public List<WB_PolyLine> getAllIslands() {
        return islands;
    }

    public WB_PolyLine getIsland() {
        return islands.get(0);
    }

    /* ------------- draw ------------- */

    public void draw(PApplet app, WB_Render render) {
        if (islands != null) {
            app.pushStyle();
            app.noFill();
            app.stroke(255, 0, 0);
            app.strokeWeight(3);

            render.drawPolylineEdges(islands);

            app.popStyle();
        }

        if (bridges != null) {
            for (Bridge bridge : bridges) {
                bridge.draw(app, render);
            }
        }
    }

    /* ------------- utils ------------- */

    /**
     * get the whole length of the polyline (replace the method in HE_Mesh)
     *
     * @param poly polyline / polygon
     * @return double
     */
    private double getPolyLength(final WB_PolyLine poly) {
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
    private double[] getPointOnPolyEdge(final WB_PolyLine poly, final double dist) {
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
    private int[] pointOnWhichEdgeIndices(final WB_Point p, final WB_PolyLine poly) {
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
    private boolean pointOnSegment(final WB_Point p, final WB_Segment seg) {
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
