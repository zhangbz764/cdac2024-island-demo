package islandImport;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/4
 * @time 10:08
 */
public class IslandBridge {
    private static final double maxLength = 300;

    private WB_Point start;
    private WB_Point end;
    private WB_Segment[] bridgeSide;

    private WB_Vector dir;

    private double width = 30;

    /* ------------- constructor ------------- */

    public IslandBridge(WB_Point start, WB_Point end) {
        this.start = start;
        this.end = end;

        this.dir = new WB_Vector(start, end);
        dir.normalizeSelf();

        WB_Vector left = new WB_Vector(-dir.yd(), dir.xd());
        WB_Vector right = new WB_Vector(dir.yd(), -dir.xd());
        WB_Segment leftSide = new WB_Segment(start.add(left.scale(width * 0.5)), end.add(left.scale(width * 0.5)));
        WB_Segment rightSide = new WB_Segment(start.add(right.scale(width * 0.5)), end.add(right.scale(width * 0.5)));

        this.bridgeSide = new WB_Segment[]{leftSide, rightSide};
    }

    /* ------------- member function ------------- */


    /* ------------- setter & getter ------------- */

    public double getWidth() {
        return width;
    }

    public WB_Point getStart() {
        return start;
    }

    public WB_Point getEnd() {
        return end;
    }

    public WB_Segment[] getBridgeSide() {
        return bridgeSide;
    }

    public WB_Vector getDir() {
        return dir;
    }

    /* ------------- draw ------------- */

    public void drawArea(PApplet app) {
        app.beginShape();

        app.vertex(bridgeSide[0].getOrigin().xf(), bridgeSide[0].getOrigin().yf());
        app.vertex(bridgeSide[1].getOrigin().xf(), bridgeSide[1].getOrigin().yf());
        app.vertex(bridgeSide[1].getEndpoint().xf(), bridgeSide[1].getEndpoint().yf());
        app.vertex(bridgeSide[0].getEndpoint().xf(), bridgeSide[0].getEndpoint().yf());
        app.vertex(bridgeSide[0].getOrigin().xf(), bridgeSide[0].getOrigin().yf());

        app.endShape();
    }

}
