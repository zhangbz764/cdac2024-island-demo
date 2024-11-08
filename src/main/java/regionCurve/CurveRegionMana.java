package regionCurve;

import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/7
 * @time 20:34
 */
public class CurveRegionMana {
    private int degree;
    private int ptNum;
    private int catmullPerSpan;

    private List<WB_Point> curvePts;

    private WB_Polygon spline;
    private WB_Polygon catmull;
    private WB_Polygon poly;

    private int whichCurve = 0;

    /* ------------- constructor ------------- */

    public CurveRegionMana(int degree, int splineNum, int catmullPerSpan) {
        this.curvePts = new ArrayList<>();

        this.degree = degree;
        this.ptNum = splineNum;
        this.catmullPerSpan = catmullPerSpan;
    }

    /* ------------- member function ------------- */

    public void addPts(double x, double y) {
        WB_Point pt = new WB_Point(x, y);
        this.curvePts.add(pt);

        if (curvePts.size() > 2) {
            // reconstruct poly
            this.poly = new WB_Polygon(curvePts);

            // reconstruct curve
            ZBSpline zbSpline = new ZBSpline(curvePts, degree, ptNum, ZBSpline.CLOSE);
            this.spline = zbSpline.getAsWB_Polygon();
            ZCatmullRom zCatmullRom = new ZCatmullRom(curvePts, catmullPerSpan, true);
            this.catmull = zCatmullRom.getAsWB_Polygon();
        }
    }

    public void clearAllPts() {
        this.curvePts.clear();
        this.poly = null;
        this.spline = null;
    }

    public void switchCurvePoly() {
        this.whichCurve = ++whichCurve % 3;
    }

    /* ------------- setter & getter ------------- */

    public WB_Polygon getPoly() {
        return poly;
    }

    public WB_Polygon getSpline() {
        return spline;
    }

    public WB_Polygon getCatmull() {
        return catmull;
    }

    public List<WB_Point> getCurvePts() {
        return curvePts;
    }

    public WB_Polygon getCurveOrPoly() {
        if (whichCurve == 0) {
            return poly;
        } else if (whichCurve == 1) {
            return spline;
        } else if (whichCurve == 2) {
            return catmull;
        } else {
            return poly;
        }
    }

    /* ------------- draw ------------- */
}
