package com.shattered.game.actor.character.player.component;

import com.shattered.game.actor.character.player.component.actionbar.PlayerActionBarComponent;
import com.shattered.game.actor.character.player.component.interaction.PlayerInteractionComponent;
import com.shattered.game.actor.character.player.component.interaction.dialogue.PlayerDialogComponent;
import com.shattered.game.actor.character.player.component.managers.WorldLevelManagerComponent;
import com.shattered.game.actor.character.player.component.model.PlayerModelComponent;
import com.shattered.game.actor.character.player.component.quest.PlayerQuestComponent;
import com.shattered.game.actor.character.player.component.reputation.PlayerReputationComponent;
import com.shattered.game.actor.character.player.component.synchronize.character.PlayerSynchronizeComponent;
import com.shattered.game.actor.character.player.component.synchronize.npc.NPCSynchronizeComponent;
import com.shattered.game.actor.character.player.component.synchronize.object.ObjectSynchronizeComponent;
import com.shattered.game.actor.character.player.component.channel.PlayerChannelComponent;
import com.shattered.game.actor.character.player.component.trades.PlayerTradesComponent;
import com.shattered.game.actor.character.player.component.widget.PlayerWidgetComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost | Jan 10, 2018 : 7:49:49 PM
 */
public class PlayerComponents<T extends Component> extends Components {


	/**
	 * Represents the World Manager Component
	 */
	public static final Components<WorldLevelManagerComponent> WORLD_LEVEL_MANAGER = new Components<>(() -> new WorldLevelManagerComponent(null));

	/**
	 * Represents the Character Synchronize Component
	 */
	public static final Components<PlayerSynchronizeComponent> CHARACTER_SYNCHRONIZE = new Components<>(() -> new PlayerSynchronizeComponent(null));

	/**
	 * Represents the NPC Synchronize Component
	 */
	public static final Components<NPCSynchronizeComponent> NPC_SYNCHRONIZE = new Components<>(() -> new NPCSynchronizeComponent(null));

	/**
	 * Represents the Object Synchronize Component
	 */
	public static final Components<ObjectSynchronizeComponent> OBJECT_SYNCHRONIZE = new Components<>(() -> new ObjectSynchronizeComponent(null));

	/**
	 * Represents the Character Model Block Component
	 */
	public static final Components<PlayerModelComponent> MODEL_BLOCK = new Components<>(() -> new PlayerModelComponent(null));

	/**
	 * Represents the Character Social Channel Component
	 */
	public static final Components<PlayerChannelComponent> SOCIAL_CHANNEL = new Components<>(() -> new PlayerChannelComponent(null));

	/**
	 * Represents the Character Interaction Component
	 */
	public static final Components<PlayerInteractionComponent> INTERACTION = new Components<>(() -> new PlayerInteractionComponent(null));

	/**
	 * Represents the Character Dialog Component
	 */
	public static final Components<PlayerDialogComponent> DIALOG = new Components<>(() -> new PlayerDialogComponent(null));

	/**
	 * Represents the Quest Component
	 */
	public static final Components<PlayerQuestComponent> QUEST = new Components<>(() -> new PlayerQuestComponent(null));

	/**
	 * Represents the Widget Component
	 */
	public static final Components<PlayerWidgetComponent> WIDGET = new Components<>(() -> new PlayerWidgetComponent(null));

	/**
	 * Represents the Action Bar Component
	 */
	public static final Components<PlayerActionBarComponent> ACTION_BAR = new Components<>(() -> new PlayerActionBarComponent(null));

	/**
	 * Represents the Tradesman Component
	 */
	public static final Components<PlayerTradesComponent> TRADESMAN = new Components<>(() -> new PlayerTradesComponent(null));

	/**
	 * Represents the Reputation Component
	 */
	public static final Components<PlayerReputationComponent> REPUTATION = new Components<>(() -> new PlayerReputationComponent(null));


	
	/**
	 * @param supplier
	 */
	private PlayerComponents(Supplier supplier) {
		super(supplier);
	}
}
