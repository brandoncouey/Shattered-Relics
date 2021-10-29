package com.shattered.game.actor.character.npc.component;

import com.shattered.game.actor.character.npc.component.channel.NPCChannelComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost | Jan 10, 2018 : 7:49:49 PM
 */
public class NPCComponents<T extends Component> extends Components {


	/**
	 * Represents the statistics component
	 */
	public static final Components<NPCChannelComponent> SOCIAL_CHANNEL = new Components<>(() -> new NPCChannelComponent(null));


	/**
	 * @param supplier
	 */
	private NPCComponents(Supplier supplier) {
		super(supplier);
	}
}
