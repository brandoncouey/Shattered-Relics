package com.shattered.game.actor.components.animation;

import com.shattered.datatable.tables.AnimSequenceUDataTable;
import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.actor.components.flags.FlagType;
import com.shattered.game.component.WorldComponent;
import lombok.Getter;

/**
 * @author JTlr Frost 10/29/2019 : 9:28 PM
 */
public class ActorAnimSequenceComponent extends WorldComponent {

    /**
     * Represents the current ongoing animation sequence.
     */
    @Getter
    private AnimSequence animSequence;
    
    /**
     * Creates a new constructor setting the {@link Player}
     *
     * @param gameObject
     */
    public ActorAnimSequenceComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        
    }

    public void playAnimSequence(String name) {
        if (isPlayer()) {
            playAnimSequence(name, true);
            return;
        }
        playAnimSequence(name, getNPC().getDataTable().isHummanoid());
    }

    /**
     * Plays an animation with the provided animation name
     * @param name
     * @param isHummanoid
     */
    public void playAnimSequence(String name, boolean isHummanoid) {
        AnimSequenceUDataTable table = AnimSequenceUDataTable.forName(name, isHummanoid);
        if (table == null) return;
        playAnimSequence(table.getId(), isHummanoid);
    }

    /**
     * Stops the Animation
     */
    public void stopAnimation() {
        playAnimSequence(0, true);
    }

    /**
     * Plays an Animation
     * @param
     */
    public void playAnimSequence(int animSequenceId, boolean isHummanoid) {
        playAnimSequence(new AnimSequence(animSequenceId));
    }

    /**
     * Plays an Animation
     * @param animSequence
     */
    public void playAnimSequence(AnimSequence animSequence) {
        this.animSequence = animSequence;
        getActor().component(ActorComponents.FLAG_UPDATE).flag(FlagType.ANIMATION);
    }

    /**
     * Gets the Actor from the Game Object
     * @return
     */
    public Actor getActor() { return (Actor) gameObject; }

    /**
     * Plays the specified animation by id
     * @param id
     */
    public void playAnimSequence(int id) {
        if (isPlayer()) {
            playAnimSequence(id, true);
            return;
        }
        playAnimSequence(id, getNPC().getDataTable().isHummanoid());
    }
}
