package com.shattered.game.actor.character.components;

import com.shattered.game.actor.character.components.buff.CharacterBuffComponent;
import com.shattered.game.actor.character.components.combat.CharacterCombatComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 10/29/2019 : 9:27 PM
 */
public class CharacterComponents<T extends Component> extends Components {


    /**
     * Represents the Combat Component
     */
    public static final Components<CharacterCombatComponent> COMBAT = new Components<>(() -> new CharacterCombatComponent(null));

    /**
     * Represents the Buff Component
     */
    public static final Components<CharacterBuffComponent> BUFF = new Components<>(() -> new CharacterBuffComponent(null));

    /**
     * @param supplier
     */
    public CharacterComponents(Supplier supplier) {
        super(supplier);
    }
}
