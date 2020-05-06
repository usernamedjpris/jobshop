package jobshop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BestKnownResult {

    public static boolean isKnown(String instanceName) {
        return bests.containsKey(instanceName);
    }

    public static List<String> instancesMatching(String namePrefix) {
        return Arrays.stream(instances)
                .filter(i -> i.startsWith(namePrefix))
                .sorted()
                .collect(Collectors.toList());
    }

    public static int of(String instanceName) {
        if(!bests.containsKey(instanceName)) {
            throw new RuntimeException("Unknown best result for "+instanceName);
        }
        return bests.get(instanceName);
    }

    static private HashMap<String, Integer> bests;
    static String[] instances;
    static {
        bests = new HashMap<>();
        bests.put("aaa1", 10);
        bests.put("abz5", 1234);
        bests.put("abz6", 943);
        bests.put("abz7", 656);
        bests.put("abz8", 665);
        bests.put("abz9", 679);
        bests.put("ft06", 55);
        bests.put("ft10", 930);
        bests.put("ft20", 1165);
        bests.put("la01", 666);
        bests.put("la02", 655);
        bests.put("la03", 597);
        bests.put("la04", 590);
        bests.put("la05", 593);
        bests.put("la06", 926);
        bests.put("la07", 890);
        bests.put("la08", 863);
        bests.put("la09", 951);
        bests.put("la10", 958);
        bests.put("la11", 1222);
        bests.put("la12", 1039);
        bests.put("la13", 1150);
        bests.put("la14", 1292);
        bests.put("la15", 1207);
        bests.put("la16", 945);
        bests.put("la17", 784);
        bests.put("la18", 848);
        bests.put("la19", 842);
        bests.put("la20", 902);
        bests.put("la21", 1046);
        bests.put("la22", 927);
        bests.put("la23", 1032);
        bests.put("la24", 935);
        bests.put("la25", 977);
        bests.put("la26", 1218);
        bests.put("la27", 1235);
        bests.put("la28", 1216);
        bests.put("la29", 1152);
        bests.put("la30", 1355);
        bests.put("la31", 1784);
        bests.put("la32", 1850);
        bests.put("la33", 1719);
        bests.put("la34", 1721);
        bests.put("la35", 1888);
        bests.put("la36", 1268);
        bests.put("la37", 1397);
        bests.put("la38", 1196);
        bests.put("la39", 1233);
        bests.put("la40", 1222);
        bests.put("orb01", 1059);
        bests.put("orb02", 888);
        bests.put("orb03", 1005);
        bests.put("orb04", 1005);
        bests.put("orb05", 887);
        bests.put("orb06", 1010);
        bests.put("orb07", 397);
        bests.put("orb08", 899);
        bests.put("orb09", 934);
        bests.put("orb10", 944);
        bests.put("swv01", 1407);
        bests.put("swv02", 1475);
        bests.put("swv03", 1398);
        bests.put("swv04", 1474);
        bests.put("swv05", 1424);
        bests.put("swv06", 1678);
        bests.put("swv07", 1600);
        bests.put("swv08", 1763);
        bests.put("swv09", 1661);
        bests.put("swv10", 1767);
        bests.put("swv11", 2991);
        bests.put("swv12", 3003);
        bests.put("swv13", 3104);
        bests.put("swv14", 2968);
        bests.put("swv15", 2904);
        bests.put("swv16", 2924);
        bests.put("swv17", 2794);
        bests.put("swv18", 2852);
        bests.put("swv19", 2843);
        bests.put("swv20", 2823);
        bests.put("yn1", 885);
        bests.put("yn2", 909);
        bests.put("yn3", 892);
        bests.put("yn4", 968);
        bests.put("ta01", 1231);
        bests.put("ta02", 1244);
        bests.put("ta03", 1218);
        bests.put("ta04", 1175);
        bests.put("ta05", 1224);
        bests.put("ta06", 1238);
        bests.put("ta07", 1227);
        bests.put("ta08", 1217);
        bests.put("ta09", 1274);
        bests.put("ta10", 1241);
        bests.put("ta11", 1361);
        bests.put("ta12", 1367);
        bests.put("ta13", 1342);
        bests.put("ta14", 1345);
        bests.put("ta15", 1340);
        bests.put("ta16", 1360);
        bests.put("ta17", 1462);
        bests.put("ta18", 1396);
        bests.put("ta19", 1335);
        bests.put("ta20", 1351);
        bests.put("ta21", 1644);
        bests.put("ta22", 1600);
        bests.put("ta23", 1557);
        bests.put("ta24", 1647);
        bests.put("ta25", 1595);
        bests.put("ta26", 1645);
        bests.put("ta27", 1680);
        bests.put("ta28", 1614);
        bests.put("ta29", 1635);
        bests.put("ta30", 1584);
        bests.put("ta31", 1764);
        bests.put("ta32", 1796);
        bests.put("ta33", 1793);
        bests.put("ta34", 1829);
        bests.put("ta35", 2007);
        bests.put("ta36", 1819);
        bests.put("ta37", 1778);
        bests.put("ta38", 1673);
        bests.put("ta39", 1795);
        bests.put("ta40", 1674);
        bests.put("ta41", 2018);
        bests.put("ta42", 1956);
        bests.put("ta43", 1859);
        bests.put("ta44", 1984);
        bests.put("ta45", 2000);
        bests.put("ta46", 2021);
        bests.put("ta47", 1903);
        bests.put("ta48", 1952);
        bests.put("ta49", 1968);
        bests.put("ta50", 1926);
        bests.put("ta51", 2760);
        bests.put("ta52", 2756);
        bests.put("ta53", 2717);
        bests.put("ta54", 2839);
        bests.put("ta55", 2679);
        bests.put("ta56", 2781);
        bests.put("ta57", 2943);
        bests.put("ta58", 2885);
        bests.put("ta59", 2655);
        bests.put("ta60", 2723);
        bests.put("ta61", 2868);
        bests.put("ta62", 2869);
        bests.put("ta63", 2755);
        bests.put("ta64", 2702);
        bests.put("ta65", 2725);
        bests.put("ta66", 2845);
        bests.put("ta67", 2825);
        bests.put("ta68", 2784);
        bests.put("ta69", 3071);
        bests.put("ta70", 2995);
        instances = bests.keySet().toArray(new String[0]);
        Arrays.sort(instances);
    }

}
