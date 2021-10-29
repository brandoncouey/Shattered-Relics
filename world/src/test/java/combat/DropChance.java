package combat;

import com.shattered.utilities.VariableUtility;

import java.util.Random;

public class DropChance {

    static float chance = 23.4f;

    public static void main(String[] args) {
        int count = 0;
        for (int index =0; index < 500; index++) {
            float val = new Random().nextFloat() * 100;
            //int val = VariableUtility.random(0, 100);
            if (val <= 25) {
                count++;
            }
        }

    }
}
