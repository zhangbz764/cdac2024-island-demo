package islandImport;

import igeo.ICurve;
import igeo.IG;
import igeo.IPoint;
import utils.UtilsZBZ;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

/**
 * description
 *
 * @author zbz_lennovo
 * @project cdac2024-island-demo
 * @date 2024/11/3
 * @time 15:08
 */
public class Island3DM {
    private WB_Polygon[] borders;
    private WB_Polygon[] islands;
    private WB_Polygon[][] envs;
    private WB_Point[][] bridgePts;

    private int islandNum;

    /* ------------- constructor ------------- */

    public Island3DM(int islandNum) {
        this.islandNum = islandNum;

        this.islands = new WB_Polygon[islandNum];
        this.bridgePts = new WB_Point[islandNum][];
        this.borders = new WB_Polygon[islandNum];
        this.envs = new WB_Polygon[islandNum][];
    }

    /* ------------- member function ------------- */

    public void load3dmIsland(String filePath) {
        IG.init();
        IG.open(filePath);

        for (int i = 0; i < islandNum; i++) {
            String border = "border" + i;
            String island = "island" + i;
            String env = "env" + i;
            String bridgePt = "bridgePt" + i;

            ICurve bord_icurve = IG.layer(border).curve(0);
            ICurve island_icurve = IG.layer(island).curve(0);
            ICurve[] env_icurve = IG.layer(env).curves();
            IPoint[] briPts_ipoint = IG.layer(bridgePt).points();

            this.borders[i] = (WB_Polygon) UtilsZBZ.ICurveToWB(bord_icurve);
            this.islands[i] = (WB_Polygon) UtilsZBZ.ICurveToWB(island_icurve);
            this.envs[i] = new WB_Polygon[env_icurve.length];
            for (int j = 0; j < env_icurve.length; j++) {
                envs[i][j] = (WB_Polygon) UtilsZBZ.ICurveToWB(env_icurve[j]);
            }
            this.bridgePts[i] = new WB_Point[briPts_ipoint.length];
            for (int j = 0; j < briPts_ipoint.length; j++) {
                bridgePts[i][j] = UtilsZBZ.IPointToWB_Point(briPts_ipoint[j]);
            }
        }

        IG.clear();
    }



    /* ------------- setter & getter ------------- */

    public int getIslandNum() {
        return islandNum;
    }

    public WB_Polygon[] getBorders() {
        return borders;
    }

    public WB_Polygon[] getIslands() {
        return islands;
    }

    public WB_Polygon[][] getEnvs() {
        return envs;
    }

    public WB_Point[][] getBridgePts() {
        return bridgePts;
    }

    /* ------------- draw ------------- */
}
