package test;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HEM_Extrude;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/15
 * @time 14:19
 */
public class TestHEMeshMemory extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestHEMeshMemory.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    public final static WB_GeometryFactory wbgf = new WB_GeometryFactory();
    List<HE_Mesh> mesh;
    WB_Polygon base;
    CameraController gcam;
    WB_Render render;

    public void setup() {
        gcam = new CameraController(this);
        render = new WB_Render(this);
        mesh = new ArrayList<>();
        base = new WB_Polygon(new WB_Point[]{
                new WB_Point(0, 0),
                new WB_Point(random(20, 30), random(0, 10)),
                new WB_Point(random(20, 30), random(20, 30)),
                new WB_Point(random(0, 10), random(20, 30)),
                new WB_Point(0, 0),
        });

        for (int i = 0; i < 50; i++) {
            mesh.add(extrude(base, 20));
        }

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        for (int i = 0; i < mesh.size(); i++) {
            HE_Mesh m = mesh.get(i);
            translate(i * 30, 0);
            for (HE_Face face : m.getFaces()) {
                render.drawFace(face);
            }
        }

        if (frameCount % 5 == 0) {
            mesh = new ArrayList<>();
            base = new WB_Polygon(new WB_Point[]{
                    new WB_Point(0, 0),
                    new WB_Point(random(20, 30), random(0, 10)),
                    new WB_Point(random(20, 30), random(20, 30)),
                    new WB_Point(random(0, 10), random(20, 30)),
                    new WB_Point(0, 0),
            });

            for (int i = 0; i < 50; i++) {
                mesh.add(extrude(base, 20));
            }
        }
        if (frameCount % 200 == 0) {
            clearHEMeshStatuses();
            System.out.println("clear");
        }
    }

    public void clearHEMeshStatuses() {
        try {
            // 通过 instance() 方法获取 WB_ProgressTracker 的单例
            Class<?> trackerClass = Class.forName("wblut.core.WB_ProgressReporter$WB_ProgressTracker");
            Object trackerInstance = trackerClass.getMethod("instance").invoke(null);

            // 获取 trackerInstance 中的 statuses 队列字段
            Field statusesField = trackerClass.getDeclaredField("statuses");
            statusesField.setAccessible(true);
            Queue<?> statuses = (Queue<?>) statusesField.get(trackerInstance);

            // 清空 statuses 队列
            statuses.clear();
            // System.out.println("Successfully cleared statuses in WB_ProgressTracker.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static HE_Mesh extrude(WB_Polygon base, double extrudeSize) {
        if (base == null) return null;

        // use a face-down polygon as the base face of the mesh
        double signedArea = base.getSignedArea();
        WB_Polygon basePoly;
        WB_Polygon basePolyRev;
        if (signedArea > 0) {
            basePoly = validateWB_Polygon(reversePolygon(base));
            basePolyRev = validateWB_Polygon(base);
        } else {
            basePoly = validateWB_Polygon(base);
            basePolyRev = validateWB_Polygon(reversePolygon(base));
        }

        // mesh creator
        HEC_FromPolygons creator = new HEC_FromPolygons();
        List<WB_Polygon> meshPolyFaceList = new ArrayList<>();

        // base
        meshPolyFaceList.add(copySimple_WB_Polygon(basePoly));
        // side
        for (int i = 0; i < basePoly.getNumberOfPoints() - 1; i++) {
            WB_Point p0 = basePoly.getPoint(i + 1).copy();
            WB_Point p1 = basePoly.getPoint(i).copy();
            WB_Point p2 = p1.add(0, 0, extrudeSize);
            WB_Point p3 = p0.add(0, 0, extrudeSize);

            WB_Polygon sideFace = wbgf.createSimplePolygon(p0, p1, p2, p3, p0);
            meshPolyFaceList.add(sideFace);
        }
        HEM_Extrude m = new HEM_Extrude();

        // top
        WB_Point[] topFacePts = new WB_Point[basePolyRev.getNumberOfPoints()];
        for (int i = 0; i < basePolyRev.getNumberOfPoints(); i++) {
            WB_Point _p = basePolyRev.getPoint(i);
            WB_Point p = new WB_Point(_p.xd(), _p.yd(), _p.zd() + extrudeSize);
            topFacePts[i] = p;
        }
        WB_Polygon topFace = wbgf.createSimplePolygon(topFacePts);
        meshPolyFaceList.add(topFace);

        creator.setPolygons(meshPolyFaceList);
        return new HE_Mesh(creator);
    }

    /**
     * check the start point and the end point of a WB_Polygon
     * validate WB_Polygon (holes supported)
     *
     * @param polygon input WB_Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon validateWB_Polygon(final WB_Polygon polygon) {
        if (polygon.getNumberOfHoles() == 0) {
            if (polygon.getPoint(0).equals(polygon.getPoint(polygon.getNumberOfPoints() - 1))) {
                return polygon;
            } else {
                List<WB_Coord> points = polygon.getPoints().toList();
                points.add(polygon.getPoint(0));
                return wbgf.createSimplePolygon(points);
            }
        } else {
            boolean flag = true;
            List<WB_Point> exterior = new ArrayList<>();
            for (int i = 0; i < polygon.getNumberOfShellPoints(); i++) {
                exterior.add(polygon.getPoint(i));
            }
            if (!exterior.get(0).equals(exterior.get(exterior.size() - 1))) {
                flag = false;
                exterior.add(exterior.get(0));
            }

            WB_Point[][] interior = new WB_Point[polygon.getNumberOfHoles()][];
            int[] npc = polygon.getNumberOfPointsPerContour();
            int index = npc[0];
            for (int i = 0; i < polygon.getNumberOfHoles(); i++) {
                List<WB_Point> contour = new ArrayList<>();
                for (int j = 0; j < npc[i + 1]; j++) {
                    contour.add(polygon.getPoint(index));
                    index = index + 1;
                }
                if (!contour.get(0).equals(contour.get(contour.size() - 1))) {
                    flag = false;
                    contour.add(contour.get(0));
                }
                interior[i] = contour.toArray(new WB_Point[0]);
            }
            if (flag) {
                return polygon;
            } else {
                return wbgf.createPolygonWithHoles(exterior.toArray(new WB_Point[0]), interior);
            }
        }
    }

    public static WB_Polygon copySimple_WB_Polygon(WB_Polygon polygon) {
        List<WB_Point> cs = new ArrayList<>();
        int numberOfPoints = polygon.getNumberOfPoints();
        for (int i = 0; i < numberOfPoints; i++) {
            WB_Point p = polygon.getPoint(i);
            cs.add(new WB_Point(p.xd(), p.yd(), p.zd()));
        }
        return wbgf.createSimplePolygon(cs);
    }

    /**
     * reverse the order of a polygon (holes supported)
     *
     * @param original input polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon reversePolygon(final WB_Polygon original) {
        if (original.getNumberOfHoles() == 0) {
            WB_Point[] newPoints = new WB_Point[original.getNumberOfPoints()];
            for (int i = 0; i < newPoints.length; i++) {
                newPoints[i] = original.getPoint(newPoints.length - 1 - i);
            }
            return new WB_Polygon(newPoints);
        } else {
            WB_Point[] newExteriorPoints = new WB_Point[original.getNumberOfShellPoints()];
            for (int i = 0; i < original.getNumberOfShellPoints(); i++) {
                newExteriorPoints[i] = original.getPoint(original.getNumberOfShellPoints() - 1 - i);
            }

            final int[] npc = original.getNumberOfPointsPerContour();
            int index = npc[0];
            WB_Point[][] newInteriorPoints = new WB_Point[original.getNumberOfHoles()][];

            for (int i = 0; i < original.getNumberOfHoles(); i++) {
                WB_Point[] newHole = new WB_Point[npc[i + 1]];
                for (int j = 0; j < newHole.length; j++) {
                    newHole[j] = new WB_Point(original.getPoint(newHole.length - 1 - j + index));
                }
                newInteriorPoints[i] = newHole;
                index = index + npc[i + 1];
            }

            return new WB_Polygon(newExteriorPoints, newInteriorPoints);
        }
    }
}
