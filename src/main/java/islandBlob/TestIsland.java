package islandBlob;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import wblut.processing.WB_Render;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac-island
 * @date 2024/11/2
 * @time 13:21
 */
public class TestIsland extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestIsland.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private WB_Render render;
    private CameraController gcam;


    private int imgW = 1000;
    private int imgH = 1000;
    private PGraphics img;
    private PGraphics imgBlur;
    private int showImgBlur = 1;

    private float brushRadius = 150;

    private float blur = 40; // blur intense
    private BlobIsland blobIsland;

    private boolean adjustBridge = false;

    public void setup() {
        this.render = new WB_Render(this);
        this.gcam = new CameraController(this);
        gcam.setPanButton(CameraController.MOUSE_RIGHTBUTTON);

        this.blobIsland = new BlobIsland(imgW, imgH);

        loadImage();
    }


    private void loadImage() {
        this.img = createGraphics(imgW, imgH);
        img.beginDraw();
        img.background(255, 0);
        PImage pImage = loadImage("C:\\Users\\94017\\Desktop\\img.jpg");
        img.image(pImage, 0, 0);
        img.endDraw();

        this.imgBlur = createGraphics(imgW, imgH);
        imgBlur.beginDraw();
        imgBlur.background(255, 0);
        imgBlur.endDraw();
    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(200);
        rect(0, 0, imgW, imgH);

        // draw texture image (PGraphics)
        if (showImgBlur == 2) {
            image(imgBlur, 0, 0, imgW, imgH);
        } else if (showImgBlur == 1) {
            image(img, 0, 0, imgW, imgH);
        }

        // draw blobs
        blobIsland.draw(this, render);
    }

    @Override
    public void mouseDragged() {
        if (mouseButton == LEFT) {
            if (!adjustBridge) {
                showImgBlur = 1;

                double[] pointer = gcam.pick3dXYPlaneDouble(mouseX, mouseY);

                float x = (float) (pointer[0]);
                float y = (float) (pointer[1]);

                img.beginDraw();

                img.fill(0);
                img.ellipse(x, y, brushRadius, brushRadius);

                img.endDraw();
            } else {
                double[] pointer = gcam.pick3dXYPlaneDouble(mouseX, mouseY);

                float x = (float) (pointer[0]);
                float y = (float) (pointer[1]);

                blobIsland.updateBridgeByNewPos(x, y);

            }
        }
    }

    @Override
    public void mouseReleased() {
//        if (mouseButton == 3) {
//            calculateBlur();
//            blobIsland.updateBoundaryAndBridges(imgBlur.pixels);
//            showImgBlur = true;
//        }
        if (adjustBridge) {
            blobIsland.clearSelectedBridge();
        }
    }

    @Override
    public void keyPressed() {
        if (key == '0') {
            calculateBlur();
            blobIsland.updateBoundaryAndBridges(imgBlur.pixels);
            showImgBlur = 2;
        }
        if (key == 's') {
            adjustBridge = !adjustBridge;
        }
        if (key == 'h') {
            showImgBlur = showImgBlur > 0 ? 0 : 2;
        }
    }


    private void calculateBlur() {
        img.loadPixels();
        imgBlur.loadPixels();
        arrayCopy(img.pixels, imgBlur.pixels);  // 直接复制像素数组
        imgBlur.updatePixels();

        imgBlur.filter(PApplet.BLUR, blur);
    }
}
