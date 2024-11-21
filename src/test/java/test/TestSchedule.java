package test;

import processing.core.PApplet;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/14
 * @time 15:36
 */
public class TestSchedule {
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;  // 标志位，判断TestGUI是否在运行

    public static void main(String[] args) {
        TestSchedule controller = new TestSchedule();
        controller.start();
    }

    public void start() {
        scheduler = Executors.newScheduledThreadPool(1);

        // 初次启动 Processing 程序
        launchTestGUI();

        // 设置定时任务，每隔10秒重启程序
        scheduler.scheduleAtFixedRate(this::restartTestGUI, 10, 10, TimeUnit.SECONDS);
    }

    private void launchTestGUI() {
        if (isRunning) return;  // 如果TestGUI已经在运行，不再启动新的实例

        // 启动TestGUI
        isRunning = true;
        new Thread(() -> {
            PApplet.main("test.TestGUI");  // 启动TestGUI程序
        }).start();
    }

    private void restartTestGUI() {
        stopTestGUI(); // 先停止当前的Processing实例
        launchTestGUI(); // 然后重新启动
    }

    private void stopTestGUI() {
        if (isRunning) {
            // 强制退出当前的TestGUI窗口和应用
            System.exit(0);
        }
    }

    public void shutdown() {
        stopTestGUI();
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
