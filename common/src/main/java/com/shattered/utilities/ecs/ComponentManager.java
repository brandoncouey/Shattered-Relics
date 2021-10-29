package com.shattered.utilities.ecs;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author JTlr Frost | Jan 10, 2018 : 7:41:05 PM
 */
@Data
public abstract class ComponentManager {

	/**
	 * Represents the {@link Object} for this {@link ComponentManager}.
	 */
	protected Object object;

	/**
	 * Represents the List of {@link Components}
	 */
	protected Map<Components<?>, Component> components;

	/**
	 * @param object
	 */
	public ComponentManager(Object object) {
		setObject(object);
		setComponents(new ConcurrentHashMap<>());
	}


	/**
	 * Method called when the player is in construction phase, and is loading in.
	 */
	public void onStart() {
		for (Component components : getComponents().values()) {
			if (components == null) continue;
			components.onStart();
		}
	}

	/**
	 * Method is called upon the entity joining the world.
	 */
	public void onWorldAwake() {
		for (Component components : getComponents().values()) {
			if (components == null) continue;
			components.onWorldAwake();
		}
	}


	//TODO i feel like i could make this a bit more performance wise.

	/**
	 * Each is called once per world cycle
	 */
	public void onTick(long deltaTime) {

		//Calls all the components with priority first
		for (Component component : getComponents().values()) {
			if (component == null || !component.getClass().isAnnotationPresent(ProcessComponent.class)
					/*|| !component.getClass().isAssignableFrom(PriorityComponent.class)*/) {
				continue;
			}

			component.setNextFrame(component.getNextFrame() + deltaTime);
			float interval = component.getClass().getAnnotation(ProcessComponent.class).interval();
			if (component.getNextFrame() < interval * 1000) {
				continue;
			}
			component.onTick(deltaTime);
			component.setNextFrame(0);//Resets the sleep interval
		}

		//Calls all components without priority annotation last
		/*for (Component component : getComponents().values()) {
			if (component == null || !component.getClass().isAnnotationPresent(ProcessComponent.class)
					*//*|| component.getClass().isAssignableFrom(PriorityComponent.class)*//*) continue;

			component.setNextFrame(component.getNextFrame() + component.getNextFrame());
			float interval = component.getClass().getAnnotation(ProcessComponent.class).interval();
				if (component.getNextFrame() < interval * 1000)
					continue;
			component.onTick(deltaTime);
			component.setNextFrame(0);//Resets the sleep interval
		}*/
	}

	/**
	 * Calls any Finishing Methods for {@link Component}
	 */
	public void onFinish() {
		for (Component component : getComponents().values()) {
			if (component == null) continue;
			component.onFinish();
		}
	}

	/**
	 * Inserts all Information for All Components to Database
	 */
	public void onInsertData() {
		for (Component component : getComponents().values()) {
			component.insert();
		}
	}

	/**
	 * Updates all Information for All Components to Database
	 */
	public void onUpdateData() {
		for (Component component : getComponents().values()) {
			component.update();
		}
	}

	/**
	 * Fetches Database Information for All Components from Database
	 */
	public void onFetchData() {
		for (Component component : getComponents().values()) {
			component.fetch();
		}
	}


	/**
	 * @param components
	 * @param component
	 * @return added
	 */
	public boolean attatch(Components<?> components, Component component) {
		getComponents().put(components, component);
		return true;
	}

	/**
	 * Gets a piece of Component
	 * @param components
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Components<T> components) {
		return (T) getComponents().get(components);
	}



}
