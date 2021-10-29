package com.shattered.game.actor.character.player.component.widget;

import com.shattered.game.actor.character.player.Player;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.types.WidgetScript;
import com.shattered.system.SystemCommand;
import com.shattered.system.SystemCommandRepository;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.FileUtility;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author JTlr Frost | Mar 8, 2018 : 4:36:59 PM
 */
public class WidgetEventRepository {

	/**
	 * Represents the Command Repository
	 */
	public static List<WidgetScript> WIDGET_SCRIPTS = new ArrayList<>();


	/**
	 *
	 */
	public static void parseRepository()  {
		try {
			Class<? extends WidgetScript>[] commands = FileUtility.getClasses(WidgetEventRepository.class.getPackage().getName() + ".scripts");
			for (Class<? extends WidgetScript> cmd : commands) {
				if (cmd.isAnonymousClass())
					continue;
				WIDGET_SCRIPTS.add(cmd.newInstance());
			}
			SystemLogger.sendSystemMessage("Successfully Registered " + WIDGET_SCRIPTS.size() + " Core Widget Scripts...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param widgetName
	 * @param player
	 * @param parameters
	 * @param buttonId
	 */
	public static void onClickedWidget(String widgetName, Player player, List<Integer> parameters, int buttonId) {
		WidgetScript script = forSyntax(widgetName);
		if (script != null) {
			script.on_clicked_widget(new PlayerAPI(player), parameters, buttonId);
		}
	}

	/**
	 * Gets the {@link WidgetScript} by syntax
	 * @param widget
	 * @return {@link WidgetScript}
	 */
	public static WidgetScript forSyntax(String widget) {
		for (WidgetScript script : WIDGET_SCRIPTS) {
			if (script.forwidget().equalsIgnoreCase(widget))
				return script;
		}
		return null;
	}

	/**
	 * Checks if a widget is handled
	 * @param widget
	 * @return handled
	 */
	public static boolean isHandled(String widget) {
		for (WidgetScript script : WIDGET_SCRIPTS) {
			if (script.forwidget().equalsIgnoreCase(widget))
				return true;
		}
		return false;
	}


}
