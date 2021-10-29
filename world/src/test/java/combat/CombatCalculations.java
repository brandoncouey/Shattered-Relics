package combat;

import com.shattered.utilities.VariableUtility;

import java.util.Random;

public class CombatCalculations {


    //Combat Power = (accuracy, strength, constitution, resilience) * 4 / 5
    //Combat Power Max = 80

    public static final double
            WEAKNESS_MULTIPLIER_STRONGEST = 0.7,
            WEAKNESS_MULTIPLIER_STRONG = 0.65,
            WEAKNESS_MULTIPLIER_NEUTRAL = 0.55,
            WEAKNESS_MULTIPLIER_LOW = 0.45;


    public static void main(String[] args) {
                                                                        //style                 //attack weakest weakness       //attack strongest weakness
        Entity player = new Entity(30, 75, 50, 90, 150, 90, AttackStyle.MAGIC, AttackStyle.ARCHERY, AttackStyle.MELEE);
        Entity bear = new Entity(50, 50, 1, 1, 0, 0, AttackStyle.ARCHERY, AttackStyle.MELEE, AttackStyle.MAGIC);
        double chance = getHitChance(player, bear);

        System.out.println("The ply has a " + (int) Math.floor(chance) + "% chance to hit the ach");
    }

    static double getHit(Entity entity, Entity target, boolean mainHand) {
        double damage = 0;
        double damageModifier = 1;
        boolean isCritical = entity.criticalstrike > VariableUtility.random(100);//TODO crit chance lower if using wrong style..?
        if (isCritical)
        damageModifier += 0.25;
        if (entity.style == target.weakness)
            damageModifier += 0.10;
        else if (entity.style == target.strongestProt)
            damageModifier -= 0.30;
        double maxHit = getMaxHit(entity, target, mainHand, damageModifier);
        double minHit = maxHit / (isCritical ? 1.4 : 1.8);
        damage = VariableUtility.random((int) minHit, (int) maxHit);
        System.out.println("Ply hit " + damage + "/" + maxHit + ". " + (isCritical ? "(Critical)" : ""));
        return damage;
    }

    static double getMaxHit(Entity entity, Entity target, boolean mainHand, double modifier) {
        double damage = (entity.strength * 0.001) * 10000;//350

        damage *= modifier;
        damage += (entity.pvpPower * 0.001) * 1000;//pvp only

        damage -= (target.defence * 0.015) * 100;
        damage -= (target.pvpResilience * 0.001) * 1000;//pvp only
        return mainHand ? damage : (damage / 2);
    }

    static double getHitChance(Entity source, Entity target) {
        double accuracy;


        /// ------------------ DEFENCE ----------------

        double defence;
        double defenceMultipler = WEAKNESS_MULTIPLIER_NEUTRAL;

        if (target.weakness == source.style)
            defenceMultipler = WEAKNESS_MULTIPLIER_STRONGEST;

        //This is like a ice creature being attacked by ice
        else if (target.strongestProt == source.style)
            defenceMultipler = WEAKNESS_MULTIPLIER_LOW;

        defence = target.defence;
        double defenceBonus = target.defence * (WEAKNESS_MULTIPLIER_NEUTRAL - defenceMultipler);

        defence += (int) defenceBonus;

        return (source.accuracy / defence * WEAKNESS_MULTIPLIER_NEUTRAL) * 100;
    }

}

enum AttackStyle {
    MELEE, ARCHERY, MAGIC
}



class Entity {

    public double accuracy;
    public double strength;
    public double defence;
    public double criticalstrike;
    public double pvpPower;
    public double pvpResilience;
    public AttackStyle style;
    public AttackStyle weakness;
    public AttackStyle strongestProt;

    public Entity(double accuracy, double strength, double defence, double criticalstrike, double pvpPower, double pvpResilience, AttackStyle style, AttackStyle weakness, AttackStyle strongestProt) {
        this.accuracy = accuracy; this.strength = strength; this.defence = defence; this.criticalstrike = criticalstrike; this.pvpPower = pvpPower; this.pvpResilience = pvpResilience; this.style = style; this.weakness = weakness; this.strongestProt = strongestProt;
    }



}
