package com.efuture.omd.storage.mybatis;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class HashMapCase<K, V> extends HashMap<K, V> implements Map<K, V>, Cloneable, Serializable {
	private static final long serialVersionUID = 362498820763181265L;

	public HashMapCase(final int paramInt, final float paramFloat) {
		super(paramInt, paramFloat);
	}

	public HashMapCase(final int paramInt) {
		super(paramInt);
	}

	public HashMapCase() {
	}

	public HashMapCase(final Map<? extends K, ? extends V> paramMap) {
		super(paramMap);
	}

	@Override
	public V get(final Object paramObject) {
		return super.get(paramObject.toString().toLowerCase());
	}

	@Override
	public boolean containsKey(final Object paramObject) {
		return super.containsKey(paramObject.toString().toLowerCase());
	}

	@Override
	public V put(final K paramK, final V paramV) {
		if (paramV instanceof BigDecimal) {
			return super.put((K) paramK.toString().toLowerCase(), (V) ((BigDecimal) paramV).toString());
		}
		return super.put((K) paramK.toString().toLowerCase(), paramV);
	}

	@Override
	public V remove(final Object paramObject) {
		return super.remove(paramObject.toString().toLowerCase());
	}
}