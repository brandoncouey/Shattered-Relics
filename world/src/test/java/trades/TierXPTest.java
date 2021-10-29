package trades;

import com.shattered.datatable.UDataTableRepository;
import com.shattered.datatable.tables.ItemUDataTable;
import com.shattered.datatable.tables.TradesUDataTable;

import java.util.Map;

public class TierXPTest {


    public static void main(String[] args) {
        ItemUDataTable.parse();
        TradesUDataTable.forTrade("woodworking").getXpVariables().forEach(System.out::println);

    }

    public static int getTechniqueTierByXPAndProducts(int currentXp, int maximumXp, int numOfProducts) {
        if (currentXp >= maximumXp) return 4;
        int t2 = (int) (22_550 * numOfProducts * 1.1);
        int t3 = (int) (t2 * 2.5 * 1.1);
        if (currentXp >= t3)
            return 3;
        if (currentXp >= t2)
            return 2;
        return 1;
    }

    public static int getMaximumXpForTechnique(int tier, int products) {
        double value = 0;
        for (int i = 0; i < products; i++)
            value += getMaximumXPByProductTier(tier);
        return (int) value;
    }

    public static int getMaximumXPByProductTier(int tier) {
        double baseValue = 22_550;
        if (tier == 1)
            return (int) baseValue;
        for (int i = 1; i < tier; i++)
            Math.floor(baseValue = ((baseValue * 2.5) * 1.1));
        return (int) baseValue;
    }


}
