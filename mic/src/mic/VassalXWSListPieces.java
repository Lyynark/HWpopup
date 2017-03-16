package mic;

import java.util.List;

import com.google.common.collect.Lists;

import VASSAL.build.widget.PieceSlot;
import VASSAL.counters.GamePiece;

/**
 * Created by amatheny on 2/8/17.
 */
public class VassalXWSListPieces {
    private List<VassalXWSPilotPieces> pilots = Lists.newArrayList();
    private List<PieceSlot> obstacles = Lists.newArrayList();
    private String listName;

    public List<VassalXWSPilotPieces> getPilots() {
        return pilots;
    }

    public List<PieceSlot> getObstacles() {
        return obstacles;
    }

    public List<GamePiece> getObstaclesForDisplay() {
        List<GamePiece> pieces = Lists.newArrayList();
        for (PieceSlot slot : getObstacles()) {
            pieces.add(Util.newPiece(slot));
        }
        return pieces;
    }

    public int getSquadPoints() {
        if (pilots.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (VassalXWSPilotPieces ship : pilots) {
            if (ship.getPilotData() == null) {
                Util.logToChat("Unable to calculate points for " + ship.getPilotCard().getConfigureName());
                continue;
            }
            total += ship.getPilotData().getPoints();
            int systemCost = 0;
            boolean isTIEx1Here = false;
            for (VassalXWSPilotPieces.Upgrade upgrade : ship.getUpgrades()) {
                if (upgrade.getUpgradeData() == null) {
                    Util.logToChat("Unable to calculate points for " + upgrade.getXwsName());
                    continue;
                }
                total += upgrade.getUpgradeData().getPoints();
                if ("tiex1".equals(upgrade.getXwsName())) {
                    isTIEx1Here = true;
                }
                if ("System".equals(upgrade.getUpgradeData().getSlot()))
                    systemCost += upgrade.getUpgradeData().getPoints();
            }
            if (isTIEx1Here) {
                total -= Math.min(4, systemCost);
            }
        }
        return total;
    }

    public void setListName(String name) {
        this.listName = name;
    }

    public String getListName() {
        return this.listName;
    }
}
