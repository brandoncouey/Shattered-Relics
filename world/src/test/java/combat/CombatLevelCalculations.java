package combat;

public class CombatLevelCalculations {


    public static final int ACCURACY_LEVEL = 70;

    public static final int STRENGTH_LEVEL = 70;

    public static final int STAMINA_LEVEL = 70;

    public static final int RESILIENCE_LEVEL = 70;

    public static final int FOCUS_LEVEL = 70;

    public static final int INTELLECT_LEVEL = 70;

    public static final int GRIMOIRE_LEVEL = 15;

    public static final int GRIMOIRE2_LEVEL = 15;



    public static final double ACCURACY_EXPERIENCE = 21328856;

    public static final double GRIMOIRE_EXPERIENCE = 436;

    public static void main(String[] args) {
       /* System.out.println(getCombatLevel());
        System.out.println(getCombaPercentTill());*/
        //System.out.println(getLevelForExperience());
        //System.out.println(getCombatLevel());
        //System.out.println(getCombatLevelPercent());
       /* for (int i = 2; i <= 70; i++) {
            System.out.println(getExperienceForLevel(i) + "xp is needed for level " + i + ".");
        }
        System.out.println(getLevelForExpereince(1));*/

       /* System.out.println(getExperienceForLevel(2));
        System.out.println(getLevelForExpereince(3));*/


       /* for (int i = 2; i <= 15; i++) {
            System.out.println(getGrimoireExperienceForLevel(i) + "xp is needed for grimoire level " + i + ".");
        }*/

        //System.out.println(getGrimoireExperienceForLevel(3));


        float stamina = 158;
        float damage = 76;

        float difference = (damage / stamina) * 100;
        System.out.println(difference);


       /* for (int i = 2; i <= 15; i++) {
            System.out.println(getGrimoireExperienceForLevel(i) + "xp is needed for level " + i + ".");
        }*/

    }


    public static float getCombatLevel() {
        float combatLevel = 1;
        float accuracy = ACCURACY_LEVEL * 0.125f;
        float strength = STRENGTH_LEVEL * 0.125f;
        float stamina = STAMINA_LEVEL * 0.136f;
        float resilience = RESILIENCE_LEVEL * 0.136f;
        float ranger = FOCUS_LEVEL * 0.125f;
        float magic = INTELLECT_LEVEL * 0.125f;
        combatLevel += accuracy + strength + stamina + resilience + ranger + magic + GRIMOIRE_LEVEL + GRIMOIRE2_LEVEL;
        return combatLevel;
    }

    public static float getCombatLevelPercent() {
        String levelAsString = String.valueOf(getCombatLevel());
        int indexOfDecimal = levelAsString.indexOf(".");
        return Float.parseFloat(levelAsString.substring(indexOfDecimal));
    }


    /**
     * Gets the Experience from Level
     * @param level
     * @return experience
     */
    public static int getExperienceForLevel(int level) {
        double baseValue = 28;
        if (level == 1)
            return (int) baseValue;
        for (int i = 2; i <= level; i++)
            Math.floor(baseValue = ((baseValue) * 1.144));
        return (int) baseValue;
    }

    public static int getExperienceNeededForLevel(int level) {
        int xpNeeded = 0;
        for (int i = 2; i <= level; i++) {
            xpNeeded += getExperienceForLevel(level);
        }
        return xpNeeded;
    }

    public static int getLevelForExpereince(int currentLevel) {
        double currentXp = ACCURACY_EXPERIENCE;
       for (int i = 70; i >= 2; i--)
           if (currentXp >= getExperienceForLevel(i)) {
               return i;
       }
        return 1;
    }

    /**
     * Gets the Experience from Level
     * @param level
     * @return experience
     */
    public static int getGrimoireExperienceForLevel(int level) {
        int xpNeeded = 0;
        double baseValue = 255;
        if (level == 1)
            return (int) baseValue;
        for (int i = 2; i <= level; i++)
            Math.floor(baseValue = ((baseValue) * 1.85));
        return (int) baseValue;
    }

    public static int getGrimoireLevelForExpereince() {
        double currentXp = ACCURACY_EXPERIENCE;
       for (int i = 15; i >= 2; i--)
           if (currentXp >= getExperienceForLevel(i)) {
               return i;
       }
        return 1;
    }

}
