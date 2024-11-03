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

    private Island3DM island3DM;

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
        this.island3DM = new Island3DM(totalIsland);
        island3DM.load3dmIsland("src/main/resources/island.3dm");
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(44, 61, 163);
        gcam.drawSystem(500);

        drawIsland(currIsland);
    }

    private void drawIsland(int id) {
        pushStyle();

        noFill();
        stroke(255);
        strokeWeight(3);
        render.drawPolygonEdges(island3DM.getBorders()[id]);
        render.drawPolygonEdges(island3DM.getIslands()[id]);

        stroke(200);
        strokeWeight(1);
        for (WB_Polygon env : island3DM.getEnvs()[id]) {
            render.drawPolygonEdges(env);
        }

        noStroke();
        fill(255, 89, 98);
        for (WB_Point bri : island3DM.getBridgePts()[id]) {
            ellipse(bri.xf(), bri.yf(), 20, 20);
        }
        popStyle();
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
