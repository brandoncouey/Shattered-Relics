package com.shattered.utilities.ecs;

import lombok.Getter;

import java.util.function.Supplier;

/**
 * @author JTlr Frost | Jan 10, 2018 : 7:49:49 PM
 */
public class Components<T extends Component> {


	/**
	 * Represents The Supplier of the Components
	 */
	@Getter
	private final Supplier<T> supplier;

	/**
	 * @param supplier
	 */
	public Components(Supplier<T> supplier) {
		this.supplier = supplier;
	}


}
