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
    private int islandNum;
    private IslandObj[] islandObjs;

    /* ------------- constructor ------------- */

    public Island3DM(int islandNum) {
        this.islandNum = islandNum;
        this.islandObjs = new IslandObj[islandNum];
    }

    /* ------------- member function ------------- */

    public void load3dmIsland(String filePath) {
        IG.init();
        IG.open(filePath);

        for (int i = 0; i < islandNum; i++) {
            String border = "border" + (i + 1);
            String island = "island" + (i + 1);
            String env = "env" + (i + 1);
            String bridgePt = "bridgePt" + (i + 1);

            ICurve bord_icurve = IG.layer(border).curves()[0];
            ICurve island_icurve = IG.layer(island).curve(0);
            ICurve[] env_icurve = IG.layer(env).curves();
            IPoint[] briPts_ipoint = IG.layer(bridgePt).points();

            WB_Polygon borderPoly = (WB_Polygon) UtilsZBZ.ICurveToWB(bord_icurve);
            WB_Polygon islandPoly = (WB_Polygon) UtilsZBZ.ICurveToWB(island_icurve);
            WB_Polygon[] envPolys = new WB_Polygon[env_icurve.length];
            for (int j = 0; j < env_icurve.length; j++) {
                envPolys[j] = (WB_Polygon) UtilsZBZ.ICurveToWB(env_icurve[j]);
            }
            WB_Point[] briPts = new WB_Point[briPts_ipoint.length];
            for (int j = 0; j < briPts_ipoint.length; j++) {
                briPts[j] = UtilsZBZ.IPointToWB_Point(briPts_ipoint[j]);
            }

            islandObjs[i] = new IslandObj(borderPoly, islandPoly, envPolys, briPts);
        }

        IG.clear();
    }



    /* ------------- setter & getter ------------- */

    public int getIslandNum() {
        return islandNum;
    }

    public IslandObj[] getIslandObjs() {
        return islandObjs;
    }

    /* ------------- draw ------------- */
}
