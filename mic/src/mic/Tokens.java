package mic;

import static mic.Util.none;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Created by amatheny on 2/11/17.
 */
public enum Tokens {
    shield(
            none,
            Lists.newArrayList("gonk"),
            none
    ),
    targetlock(
            Lists.newArrayList("Target Lock"),
            Lists.newArrayList("targetingcomputer", "firecontrolsystem"),
            none
    ),
    stress(
            none,
            Lists.newArrayList("zuckuss", "r3a2","elusiveness","overclockedr4"),
            Lists.newArrayList("awing/tychocelchu")
    ),
    focus(
            Lists.newArrayList("Focus"),
            none,
            none
    ),
    evade(
            Lists.newArrayList("Evade"),
            Lists.newArrayList("r3astromech", "janors", "millenniumfalcon","coolhand"),
            Lists.newArrayList("t70xwing/redace")
    ),
    crit,
    ion,
    energy,
    reinforce,
    cloak(
            Lists.newArrayList("Cloak"),
            Lists.newArrayList("cloakingdevice"),
            none
    ),
    idtoken,
    weaponsdisabled(
            Lists.newArrayList("SLAM"),
            Lists.newArrayList("burnoutslam", "arccaster"),
            Lists.newArrayList("ewing/corranhorn", "m3ainterceptor/quinnjast")
    ),
    initiative,
    ordnance(
            none,
            Lists.newArrayList("extramunitions"),
            none
    ),
    tractorbeam(
            none,
            Lists.newArrayList("tractorbeam", "spacetugtractorarray", "shadowcaster"),
            Lists.newArrayList("lancerclasspursuitcraft/ketsuonyo", "quadjumper/unkarplutt")
    ),
    hull,
    hugeshipforestats,
    hugeshipaftstats;

    private List<String> actions = Lists.newArrayList();
    private List<String> upgrades = Lists.newArrayList();
    private List<String> pilots = Lists.newArrayList();

    Tokens() {
    }

    Tokens(List<String> actions, List<String> upgrades, List<String> pilots) {
        this.actions = actions;
        this.upgrades = upgrades;
        this.pilots = pilots;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getUpgrades() {
        return upgrades;
    }

    public List<String> getPilots() {
        return pilots;
    }
}
