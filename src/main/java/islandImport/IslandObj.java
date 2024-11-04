package islandImport;

import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project cdac2024-island-demo
 * @date 2024/11/4
 * @time 13:41
 */
public class IslandObj {

    private WB_Polygon border;
    private WB_Polygon island;
    private WB_Polygon[] envs;
    private WB_Point[] bridgePts;
    private IslandBridge[] bridges;

    /* ------------- constructor ------------- */

    public IslandObj(WB_Polygon border, WB_Polygon island, WB_Polygon[] envs, WB_Point[] bridgePts) {
        this.border = border;
        this.island = island;
        this.envs = envs;
        this.bridgePts = bridgePts;

        this.bridges = new IslandBridge[bridgePts.length];
        for (int i = 0; i < bridgePts.length; i++) {
            WB_Point bridgePt = bridgePts[i];
            WB_Point closest = WB_GeometryOp.getClosestPoint2D(bridgePt, island);

            IslandBridge bridge = new IslandBridge(bridgePt, closest);
            this.bridges[i] = bridge;
        }
    }

    /* ------------- member function ------------- */


    /* ------------- setter & getter ------------- */

    public WB_Polygon getBorder() {
        return border;
    }

    public WB_Polygon getIsland() {
        return island;
    }

    public WB_Polygon[] getEnvs() {
        return envs;
    }

    public WB_Point[] getBridgePts() {
        return bridgePts;
    }

    public IslandBridge[] getBridges() {
        return bridges;
    }

    /* ------------- draw ------------- */
}
