package islandBlob;

import blobDetection.Blob;
import blobDetection.BlobDetection;
import blobDetection.EdgeVertex;
import processing.core.PApplet;
import utils.UtilsZBZ;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

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

        double length = UtilsZBZ.getPolyLength(island);
        double step = length / bridgeNum;

        for (int i = 0; i < bridgeNum; i++) {
            double[] posAndDir = UtilsZBZ.getPointOnPolyEdge(island, step * i);
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
            int[] ids = UtilsZBZ.pointOnWhichEdgeIndices(closestPoint2D, this.getIsland());
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


}
