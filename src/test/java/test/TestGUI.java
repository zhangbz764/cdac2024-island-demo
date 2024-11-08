package test;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import zbzGUI.GUIManager;
import zbzGUI.ImageButton;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 9:28
 */
public class TestGUI extends PApplet {
    public static void main(String[] args) {
        PApplet.main(TestGUI.class.getName());
    }

    /* ------------- settings ------------- */

    public void settings() {
        size(1000, 1000, P3D);
    }

    /* ------------- setup ------------- */

    private CameraController gcam;

    private GUIManager guiManager;

    private PImage[] pImages;
    private int imgCount = 0;

    public void setup() {
        this.gcam = new CameraController(this);
        gcam.setPanButton(CameraController.MOUSE_RIGHTBUTTON);

        this.guiManager = new GUIManager();
        this.pImages = new PImage[3];
        pImages[0] = loadImage("src/test/resources/p1.jpg");
        pImages[1] = loadImage("src/test/resources/p2.jpg");
        pImages[2] = loadImage("src/test/resources/p3.jpg");

        PFont font = createFont("src/main/resources/simhei.ttf", 32);

        guiManager.addButton("button")
                .setFunction(() -> function1("按钮1"))

                .setPosition(100, 100)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮1")

                .setColorBackground(0xff0000ff)
                .setColorActive(0xff00ffff)
                .setColorLabel(0xffffffff)
        ;
        guiManager.addButton("button2")
                .setFunction(() -> function1("按钮2"))

                .setPosition(100, 200)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮2")

                .setColorBackground(0xff0000ff)
                .setColorActive(0xff00ffff)
                .setColorLabel(0xffffffff)
        ;
        guiManager.addButton("button3")
                .setFunction(() -> function1("按钮3"))

                .setPosition(100, 300)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("按钮3")

                .setColorBackground(0xff0000ff)
                .setColorActive(0xff00ffff)
                .setColorLabel(0xffffffff)
        ;

        guiManager.addSlider("slider1")
                .setRange(0, 500)
                .setValue(100)
                .setFunction((cal) -> function2(cal))

                .setPosition(100, 400)
                .setSize(300, 60)

                .setFont(font, 25)
                .setLabel("滑条3")

                .setColorBackground(0xff000000)
                .setColorForeground(0xff0000ff)
                .setColorActive(0xff00ffff)
                .setColorLabel(0xff000000)
        ;

        guiManager.addImageButton("imageButton1")
                .setImage(this.pImages[imgCount])
                .setFunction(() -> function3())

                .setPosition(500, 100)
                .setSize(400, 150)

                .setFont(font, 25)
                .setLabel("切換圖片")

                .setColorLabel(0xff000000)
                .setColorActive(0xff00ffff)
        ;

        guiManager.addImageButton("imageButton2")
                .setImages(this.pImages)
                .setFunction(() -> function4())

                .setPosition(500, 400)
                .setSize(300, 200)

                .setFont(font, 25)
                .setLabel("切換圖片")

                .setColorLabel(0xff000000)
                .setColorActive(0x7900ffff)
        ;

    }

    /* ------------- draw ------------- */

    public void draw() {
        background(255);
        gcam.drawSystem(100);

        gcam.begin2d();
        guiManager.draw(this);

        gcam.begin3d();
        ellipse(0, 0, 100, 100);

    }

    @Override
    public void mouseClicked() {
        guiManager.listenMouseClicked(mouseX, mouseY);
    }

    @Override
    public void mouseDragged() {
        guiManager.listenMouseDragged(mouseX, mouseY);
    }

    @Override
    public void mousePressed() {
        guiManager.listenMousePressed(mouseX, mouseY);
    }

    @Override
    public void mouseReleased() {
        guiManager.listenMouseReleased(mouseX, mouseY);
    }

    /* ------------- functions ------------- */

    public void function1(String str) {
        System.out.println(str);
    }

    public void function2(double val) {
        System.out.println("slider  " + val);
    }

    public void function3() {
        imgCount = (imgCount + 1) % pImages.length;
        ImageButton imageButton1 = (ImageButton) this.guiManager.getControllerByName("imageButton1");
        imageButton1.setImage(pImages[imgCount]);
    }

    public void function4() {
        imgCount = (imgCount + 1) % pImages.length;
        ImageButton imageButton2 = (ImageButton) this.guiManager.getControllerByName("imageButton2");
        imageButton2.setImage(pImages[imgCount]);
    }
}
