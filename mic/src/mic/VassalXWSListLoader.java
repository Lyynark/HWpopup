package mic;

import VASSAL.build.widget.PieceSlot;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import java.util.List;

/**
 * Created by amatheny on 3/15/17.
 */
public class VassalXWSListLoader {

    private final VassalXWSPieceLoader vassalPieces;

    public VassalXWSListLoader(VassalXWSPieceLoader vassalPieces) {
        Preconditions.checkNotNull(vassalPieces);
        this.vassalPieces = vassalPieces;
    }

    public VassalXWSListPieces loadListFromUrl(String xwsUrl) {
        XWSList list = Util.loadRemoteJson(xwsUrl, XWSList.class);
        if (list == null) {
            Util.logToChat("Unable to load list %s", xwsUrl);
            return null;
        }

        VassalXWSListPieces pieces = new VassalXWSListPieces();

        pieces.setListName(list.getName());

        Multiset<String> pilotCounts = HashMultiset.create();
        for (XWSList.XWSPilot pilot : list.getPilots()) {
            pilotCounts.add(pilot.getName());
        }

        Multiset<String> genericPilotsAdded = HashMultiset.create();

        for (XWSList.XWSPilot pilot : list.getPilots()) {
            VassalXWSPilotPieces barePieces = this.vassalPieces.getPilot(list.getFaction(), pilot.getShip(), pilot.getName());
            if (barePieces == null) {
                Util.logToChat("Could not find pilot: %s/%s/%s", list.getFaction(), pilot.getShip(), pilot.getName());
                continue;
            }

            VassalXWSPilotPieces pilotPieces = new VassalXWSPilotPieces(barePieces);

            if (pilotPieces.getPilotData() != null) {
                List<PieceSlot> foundConditions = getConditionsForCard(pilotPieces.getPilotData().getConditions());
                pilotPieces.getConditions().addAll(foundConditions);
            }

            if (pilotCounts.count(pilot.getName()) > 1) {
                genericPilotsAdded.add(pilot.getName());
                pilotPieces.setShipNumber(genericPilotsAdded.count(pilot.getName()));
            }

            for (String upgradeType : pilot.getUpgrades().keySet()) {
                for (String upgradeName : pilot.getUpgrades().get(upgradeType)) {
                    VassalXWSPilotPieces.Upgrade upgrade = this.vassalPieces.getUpgrade(upgradeType, upgradeName);
                    if (upgrade == null) {
                        Util.logToChat("Could not find upgrade: %s/%s", upgradeType, upgradeName);
                        continue;
                    }

                    if (upgrade.getUpgradeData() != null) {
                        List<PieceSlot> foundConditions = getConditionsForCard(upgrade.getUpgradeData().getConditions());
                        pilotPieces.getConditions().addAll(foundConditions);
                    }
                    pilotPieces.getUpgrades().add(upgrade);
                }
            }

            List<Tokens> tokens = loadTokensForPilot(pilotPieces);
            for (Tokens token : tokens) {
                PieceSlot tokenSlot = this.vassalPieces.getToken(token);
                if (tokenSlot != null) {
                    pilotPieces.getTokens().put(token, tokenSlot);
                }
            }
            pieces.getPilots().add(pilotPieces);
        }

        for (String xwsObstacleName : list.getObstacles()) {
            PieceSlot obstacle = this.vassalPieces.getObstacle(xwsObstacleName);
            if (obstacle == null) {
                Util.logToChat("Unable to find vassal obstacle for xws obstacle '" + xwsObstacleName + "'");
                continue;
            }
            pieces.getObstacles().add(obstacle);
        }

        return pieces;
    }

    private List<PieceSlot> getConditionsForCard(List<String> conditions) {
        List<PieceSlot> conditionSlots = Lists.newArrayList();
        for (String conditionName : conditions) {
            String canonicalConditionName = Canonicalizer.getCanonicalUpgradeName(
                    "conditions", conditionName);
            VassalXWSPilotPieces.Upgrade condition = this.vassalPieces.getUpgrade("conditions", canonicalConditionName);
            if (condition == null) {
                Util.logToChat("Unable to load condition: " + conditionName);
                continue;
            }
            conditionSlots.add(condition.getPieceSlot());
        }
        return conditionSlots;
    }


    public static List<Tokens> loadTokensForPilot(VassalXWSPilotPieces pilot) {
        List<Tokens> tokens = Lists.newArrayList();
        for (Tokens token : Tokens.values()) {
            if (pilot.getShipData() != null) {
                for (String action : pilot.getShipData().getActions()) {
                    if (token.getActions().contains(action)) {
                        tokens.add(token);
                    }
                }

                if (pilot.getPilotData() != null) {
                    String shipPilot = pilot.getShipData().getXws() + "/" + pilot.getPilotData().getXws();
                    if (token.getPilots().contains(shipPilot)) {
                        tokens.add(token);
                    }
                }
            }

            for (VassalXWSPilotPieces.Upgrade upgrade : pilot.getUpgrades()) {
                if (token.getUpgrades().contains(upgrade.getXwsName())) {
                    tokens.add(token);
                }
            }
        }

        return tokens;
    }

}
