package com.shattered.game.actor;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.character.npc.NPC;

import java.util.Iterator;
import java.util.NoSuchElementException;


public final class ActorList<T extends Actor> implements Iterable<T> {

	public T[] entities;
	public int lowestFreeIndex = 0;
	private int size;

	@SuppressWarnings("unchecked")
	public ActorList(int capacity, ActorType type) {
		if (type == ActorType.CHARACTER)
			entities = (T[]) new Player[capacity];

		else if (type == ActorType.OBJECT)
			entities = (T[]) new WorldObject[capacity];

		else if (type == ActorType.NPC)
			entities = (T[]) new NPC[capacity];
	}

	/**
	 * @param actor
	 * @return
	 */
	public boolean add(T actor) {
		synchronized (this) {
			actor.setClientIndex(lowestFreeIndex + 1);
			entities[lowestFreeIndex] = actor;
			size++;
			for (int i = lowestFreeIndex + 1; i < entities.length; i++) {
				if (entities[i] == null) {
					lowestFreeIndex = i;
					break;
				}
			}
			return true;
		}
	}

	/**
	 * @param actor
	 */
	public void remove(T actor) {
		synchronized (this) {
			int listIndex = actor.getClientIndex() - 1;
			entities[listIndex] = null;
			size--;
			if (listIndex < lowestFreeIndex)
				lowestFreeIndex = listIndex;
		}
	}

	/**
	 * @param index
	 * @return
	 */
	public T get(int index) {
		if (index >= entities.length || index == 0)
			return null;
		return entities[index - 1];
	}

	/**
	 * @param actor
	 * @return
	 */
	public boolean contains(T actor) {
		return actor.getClientIndex() != 0 && entities[actor.getClientIndex() - 1] == actor;
	}

	/**
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Iterator<T> iterator() {
		return new AnimatorIterator();
	}

	private final class AnimatorIterator implements Iterator<T> {

		/**
		 * The previous zoneId of this iterator.
		 */
		private int previousIndex = -1;

		/**
		 * The current zoneId of this iterator.
		 */
		private int index = 0;

		@Override
		public boolean hasNext() {
			for (int i = index; i < entities.length; i++) {
				if (entities[i] != null) {
					index = i;
					return true;
				}
			}
			return false;
		}

		@Override
		public T next() {
			T animator = null;
			for (int i = index; i < entities.length; i++) {
				if (entities[i] != null) {
					animator = (T) entities[i];
					index = i;
					break;
				}
			}
			if (animator == null)
				throw new NoSuchElementException();
			previousIndex = index;
			index++;
			return animator;
		}

		@Override
		public void remove() {
			if (previousIndex == -1) {
				throw new IllegalStateException();
			}
			ActorList.this.remove((T) entities[previousIndex]);
			previousIndex = -1;
		}

	}

}