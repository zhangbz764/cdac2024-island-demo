package zbzGUI;

import Guo_Cam.CameraController;
import processing.core.PApplet;
import processing.core.PFont;

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

    public void setup() {
        this.gcam = new CameraController(this);

        this.guiManager = new GUIManager();

        PFont font = createFont("src/main/resources/simhei.ttf", 32);

        guiManager.addButton("button")
                .setPosition(100, 100)
                .setSize(300, 60)

                .setFont(font, 15)
                .setLabel("按钮")

                .setColorBackground(0xff00ffff)
                .setColorLabel(0xffffffff)

                .setMyFunction(() -> function1(8))
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

    /* ------------- functions ------------- */

    public void function1(float x) {
        System.out.println("function1");
    }

    public void function2(String str) {
        System.out.println("function2");
    }
}
