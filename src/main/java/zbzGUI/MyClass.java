package zbzGUI;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/8
 * @time 9:58
 */
public class MyClass {
    private MyFunction myFunction;

    public MyClass(MyFunction myFunction) {
        this.myFunction = myFunction;
    }

    public void runFunction() {
        myFunction.execute();
    }
}

@FunctionalInterface
interface MyFunction {
    void execute();
}
