package islandImport;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/3
 * @time 12:08
 */
public class TestIsland3DM extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestIsland3DM.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1600, 900, P3D);
    }

    private IslandObj[] islandObjs;

    private CameraController gcam;
    private WB_Render render;

    private int currIsland = 0;
    private int totalIsland = 4;

    /* ------------- setup ------------- */

    public void setup() {
        this.gcam = new CameraController(this);
        this.render = new WB_Render(this);
        gcam.setPanButton(CameraController.MOUSE_RIGHTBUTTON);

        // load 3dm file
        Island3DM island3DM = new Island3DM(totalIsland);
        island3DM.load3dmIsland("src/main/resources/island_rh4.3dm");
        IslandBridge.maxLength = 400;
        IslandBridge.posRadius = 50;
        IslandBridge.width = 50;

        this.islandObjs = island3DM.getIslandObjs();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(83, 192, 231);
        gcam.drawSystem(500);

        drawIsland(currIsland);
    }

    private void drawIsland(int id) {
        pushStyle();

        // border and island
        stroke(255);
        strokeWeight(3);
        noFill();
        render.drawPolygonEdges(islandObjs[id].getBorder());
        fill(119, 195, 153);
        render.drawPolygonEdges(islandObjs[id].getIsland());

        // environments
        stroke(255);
        strokeWeight(1);
        fill(119, 195, 153);
        for (WB_Polygon env : islandObjs[id].getEnvs()) {
            render.drawPolygonEdges(env);
        }

        // bridges
        stroke(255);
        strokeWeight(1);
        for (IslandBridge bri : islandObjs[id].getBridges()) {
            render.drawSegment2D(bri.getBridgeSide());
        }
        noStroke();
        fill(128);
        for (IslandBridge bri : islandObjs[id].getBridges()) {
            bri.drawArea(this);
        }
        noStroke();
        fill(255, 89, 98);
        for (IslandBridge bri : islandObjs[id].getBridges()) {
            ellipse(bri.getStart().xf(), bri.getStart().yf(), IslandBridge.posRadius, IslandBridge.posRadius);
            ellipse(bri.getEnd().xf(), bri.getEnd().yf(), IslandBridge.posRadius, IslandBridge.posRadius);
        }

        popStyle();
    }

    @Override
    public void mouseDragged() {
        if (mouseButton == LEFT) {
            double[] pointer = gcam.pick3dXYPlaneDouble(mouseX, mouseY);
            float x = (float) (pointer[0]);
            float y = (float) (pointer[1]);

            islandObjs[currIsland].updateBridgeByNewPos(x, y);
        }
    }

    @Override
    public void mouseReleased() {
        islandObjs[currIsland].clearSelectedBridge();
    }

    @Override
    public void keyPressed() {
        switch (key) {
            case '1':
                if (currIsland > 0) {
                    currIsland--;
                } else {
                    currIsland = totalIsland - 1;
                }
                break;
            case '2':
                if (currIsland < totalIsland - 1) {
                    currIsland++;
                } else {
                    currIsland = 0;
                }
                break;
            default:
                break;
        }
    }
}
