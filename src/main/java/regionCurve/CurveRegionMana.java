package regionCurve;

import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
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

    private WB_PolyLine spline;
    private WB_PolyLine catmull;
    private WB_PolyLine poly;

    private int whichCurve = 0;

    private boolean isClosed = false;

    /* ------------- constructor ------------- */

    public CurveRegionMana(int degree, int splineNum, int catmullPerSpan, boolean isClosed) {
        this.curvePts = new ArrayList<>();

        this.degree = degree;
        this.ptNum = splineNum;
        this.catmullPerSpan = catmullPerSpan;

        this.isClosed = isClosed;
    }

    /* ------------- member function ------------- */

    public void addPts(double x, double y) {
        WB_Point pt = new WB_Point(x, y);
        this.curvePts.add(pt);

        updateAllCurves();
    }

    public void clearAllPts() {
        this.curvePts.clear();
        this.poly = null;
        this.spline = null;
    }

    public void switchCurvePoly() {
        this.whichCurve = ++whichCurve % 3;
    }

    private void updateAllCurves(){
        if (isClosed) {
            if (curvePts.size() > 2) {
                // reconstruct poly
                List<WB_Point> newPts = new ArrayList<>(curvePts);
                newPts.add(curvePts.get(0));
                this.poly = new WB_Polygon(newPts);

                // reconstruct curve
                ZBSpline zbSpline = new ZBSpline(curvePts, degree, ptNum, ZBSpline.CLOSE);
                this.spline = zbSpline.getAsWB_Polygon();
                ZCatmullRom zCatmullRom = new ZCatmullRom(curvePts, catmullPerSpan, true);
                this.catmull = zCatmullRom.getAsWB_Polygon();
            }
        } else {
            if (curvePts.size() > 2) {
                this.poly = new WB_PolyLine(curvePts);
                ZBSpline zbSpline = new ZBSpline(curvePts, degree, ptNum, ZBSpline.CLAMPED);
                this.spline = zbSpline.getAsWB_PolyLine();
                ZCatmullRom zCatmullRom = new ZCatmullRom(curvePts, catmullPerSpan, false);
                this.catmull = zCatmullRom.getAsWB_PolyLine();
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public void setClosed(boolean closed) {
        isClosed = closed;
        updateAllCurves();
    }

    public WB_PolyLine getPoly() {
        return poly;
    }

    public WB_PolyLine getSpline() {
        return spline;
    }

    public WB_PolyLine getCatmull() {
        return catmull;
    }

    public List<WB_Point> getCurvePts() {
        return curvePts;
    }

    public WB_PolyLine getCurveOrPoly() {
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
