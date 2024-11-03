package islandBlob;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac-island
 * @date 2024/11/2
 * @time 14:08
 */
public class Bridge {
    // basic parameters
    private WB_Point pos;
    private WB_Vector dir;
    private double width;
    private double length = 50;

    // geometries to display
    private WB_Segment[] briGeos;
    public static float posRadius = 20;

    /* ------------- constructor ------------- */

    public Bridge(WB_Point pos, WB_Vector dir, double width) {
        this.pos = pos;
        this.dir = dir;
        this.width = width;

        createBriGeos();
    }

    /* ------------- member function ------------- */

    /**
     * create bridge geometries to display
     *
     * @param
     * @return void
     */
    private void createBriGeos() {
        this.briGeos = new WB_Segment[2];

        WB_Vector leftDir = new WB_Vector(-dir.yd(), dir.xd());
        WB_Vector rightDir = new WB_Vector(dir.yd(), -dir.xd());

        WB_Point left0 = new WB_Point(pos.add(leftDir.scale(width * 0.5)));
        WB_Point left1 = new WB_Point(left0.add(dir.scale(length)));
        WB_Point right0 = new WB_Point(pos.add(rightDir.scale(width * 0.5)));
        WB_Point right1 = new WB_Point(right0.add(dir.scale(length)));

        briGeos[0] = new WB_Segment(left0, left1);
        briGeos[1] = new WB_Segment(right0, right1);
    }

    /* ------------- setter & getter ------------- */

    public WB_Point getPos() {
        return pos;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setPosAndDir(WB_Point pos, WB_Vector dir) {
        this.pos = pos;
        this.dir = dir;
        createBriGeos();
    }

    /* ------------- draw ------------- */

    public void draw(PApplet app, WB_Render render) {
        app.pushStyle();
        app.noFill();
        app.stroke(0);
        app.strokeWeight(2);
        render.drawSegment2D(briGeos);

        app.fill(0, 0, 255);
        app.ellipse(pos.xf(), pos.yf(), posRadius, posRadius);

        app.popStyle();
    }
}
