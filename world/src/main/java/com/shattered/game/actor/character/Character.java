package com.shattered.game.actor.character;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.components.CharacterComponents;
import com.shattered.game.actor.character.components.buff.CharacterBuffComponent;

public abstract class Character extends Actor  {

    /**
     * Creates a new Character Constructor setting the ID
     * @param id
     */
    public Character(int id) {
        super(id);
    }


    /**
     * Adds all of the components
     */
    @Override
    public void addComponents() {
        super.addComponents();
        getComponentManager().attatch(CharacterComponents.BUFF, new CharacterBuffComponent(this));
    }



}
