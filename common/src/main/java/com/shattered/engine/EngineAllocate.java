package com.shattered.engine;

public class EngineAllocate {

	/** Represents Bytes */
	private byte[] bytes;

	/**
	 * Creates a new Const Setting the {@code bytes}
	 */
	public EngineAllocate() {
		bytes = new byte[0];
	}

	/**
	 * @param bytes
	 */
	public synchronized void allocate(byte[] bytes) {
		System.arraycopy(this.bytes, this.bytes.length, bytes, 0, bytes.length);
	}

	/**
	 * @param start
	 * @param offset
	 */
	public synchronized void exaustExternalBytes(int start, int offset) {
		for (int i = start; i < offset; i++)
			bytes[i] = -1;
	}

	/**
	 * @param length
	 */
	public synchronized void stack(int length) {
		byte[] newBuffer = new byte[(bytes.length + length)];
		System.arraycopy(bytes, 0, newBuffer, 0, bytes.length);
		bytes = newBuffer;
	}
}
