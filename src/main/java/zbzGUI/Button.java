package zbzGUI;

import org.eclipse.collections.api.partition.ordered.PartitionReversibleIterable;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 8:40
 */
public class Button extends Controller {

    /* ------------- constructor ------------- */

    public Button(String name) {
        super(name);
    }

    /* ------------- member function ------------- */

    @Override
    public void enableMouseClickEvent(float mouseX, float mouseY) {
        // check boundary
        if (mouseX >= position[0] && mouseX <= position[0] + size[0] && mouseY >= position[1] && mouseY <= position[1] + size[1]) {
            super.myFunction.execute();
        }
    }

    /* ------------- setter & getter ------------- */



    /* ------------- draw ------------- */

    private void setTransition(PApplet app){

    }

    @Override
    public void draw(PApplet app) {
        app.pushStyle();

        // button rectangle
        app.noStroke();
        app.fill(colorBackground);
        app.rect(position[0], position[1], size[0], size[1]);

        // label
        app.fill(colorLabel);
        app.textAlign(app.CENTER, app.CENTER);
        if (font != null) {
            app.textFont(font);
        }
        app.textSize(fontSize);
        app.text(label, position[0] + size[0] * .5f, position[1] + size[1] * .5f);

        app.popStyle();
    }
}
