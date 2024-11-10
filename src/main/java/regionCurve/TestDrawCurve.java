package regionCurve;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * 1 - 切换折线、BSpline、Catmull-Rom曲线
 * 2 - 清除所有控制点和曲线
 * 3 - 切换闭合 / 开放
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/7
 * @time 20:29
 */
public class TestDrawCurve extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestDrawCurve.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;
    private WB_Render render;

    private WB_PolyLine curveRegion;
    private CurveRegionMana curveRegionMana;

    private boolean isClosed = false;

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);

        this.curveRegionMana = new CurveRegionMana(3, 100, 20, isClosed);
    }


    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(100);

        // 绘制多边形
        if (curveRegion != null) {
            render.drawPolylineEdges(curveRegion);
        }

        // 绘制控制点
        for (WB_Point p : curveRegionMana.getCurvePts()) {
            ellipse(p.xf(), p.yf(), 15, 15);
        }
    }

    public void mouseClicked() {
        // 鼠标单击添加新的点
        double[] pointer = gcam.pick3dXYPlaneDouble(mouseX, mouseY);
        float x = (float) (pointer[0]);
        float y = (float) (pointer[1]);
        curveRegionMana.addPts(x, y);

        // 更新多边形
        this.curveRegion = curveRegionMana.getCurveOrPoly();
    }

    public void keyPressed() {
        // 切换曲线/折线
        if (key == '1') {
            curveRegionMana.switchCurvePoly();
            curveRegion = curveRegionMana.getCurveOrPoly();
        }
        // 清除所有控制点
        if (key == '2') {
            curveRegionMana.clearAllPts();
            curveRegion = null;
        }
        // 切换闭合 / 开放
        if (key == '3') {
            isClosed = !isClosed;
            curveRegionMana.setClosed(isClosed);
            curveRegion = curveRegionMana.getCurveOrPoly();
        }
    }

}
