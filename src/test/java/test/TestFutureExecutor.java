package test;

import wblut.core.WB_ProgressReporter;
import wblut.hemesh.HEC_FromPolygons;
import wblut.hemesh.HE_Mesh;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.*;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/15
 * @time 9:41
 */
public class TestFutureExecutor {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Void> future = null;

    public static void main(String[] args) {
        clearStaticField(HE_Mesh.class,"tracker");
    }

    public static void clearStaticField(Class<?> clazz, String fieldName) {
        try {
            // 获取目标字段
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true); // 绕过访问控制检查

            // 移除 final 修饰符
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            // 将字段设为 null 或清空内容
            if (field.getType().isAssignableFrom(java.util.Map.class)) {
                ((java.util.Map<?, ?>) field.get(null)).clear();
            } else if (field.getType().isAssignableFrom(java.util.List.class)) {
                ((java.util.List<?>) field.get(null)).clear();
            } else {
                field.set(null, null); // 设置为null
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        clearStaticField(HE_Mesh.class,"tracker");
        future = executor.submit(() -> {

            System.out.println("dsdsdsddsd");

            return null;
        });


        int time = 20;
        try {
            future.get(time, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 超时
            System.out.println("运算时间超过" + time + "秒，中断线程");
            future.cancel(true); // 终止执行
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
        } finally {
            executor.shutdownNow(); // 关闭线程池
        }
    }
}
