package zbzGUI;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:38
 */
public class GUIManager {
    private List<Controller> controllers;

    /* ------------- constructor ------------- */

    public GUIManager() {
        this.controllers = new ArrayList<>();
    }

    /* ------------- member function ------------- */

    public Button addButton(String name) {
        Button button = new Button(name);
        this.controllers.add(button);
        return button;
    }

    public void listenMouseClicked(float mouseX, float mouseY) {
        for (Controller controller : controllers) {
            controller.enableMouseClickEvent(mouseX, mouseY);
        }
    }

    /* ------------- setter & getter ------------- */


    /* ------------- draw ------------- */

    public void draw(PApplet app) {
        for (Controller controller : controllers) {
            controller.draw(app);
        }
    }
}
