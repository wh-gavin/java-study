package com.efuture.omd.storage.parser;

import java.util.HashMap;
import java.util.Map;

public class MongoExpr {
	public static final int NOT_EQUAL = 1;
	public static final int LESS_THAN = 2;
	public static final int LESS_THAN_OR_EQUAL = 3;
	public static final int GREATER_THAN = 4;
	public static final int GREATER_THAN_OR_EQUAL = 5;
	public static final int NOT = 6;
	public static final int IN = 7;
	public static final int NOT_IN = 8;
	public static final int EXISTS = 9;
	public static final int AND = 10;
	public static final int OR = 11;
	public static final int NOR = 12;
	public static final int MOD = 13;
	public static final int ALL = 14;
	public static final int SIZE = 15;
	public static final int TYPE = 16;
	public static final int WITHIN = 17;
	public static final int NEAR = 18;
	public static final int NEAR_SPHERE = 19;
	public static final int MAX_DISTANCE = 20;
	public static final int ELEM_MATCH = 21;
	public static final int SET = 22;
	public static final int INC = 23;
	private static final Map<String, Integer> map = new HashMap<String, Integer>();

	public static int fromString(String op) {
		return map.get(op);
	}

	static {
		map.put("$ne", 1);
		map.put("$lt", 2);
		map.put("$lte", 3);
		map.put("$gt", 4);
		map.put("$gte", 5);
		map.put("$not", 6);
		map.put("$in", 7);
		map.put("$nin", 8);
		map.put("$exists", 9);
		map.put("$and", 10);
		map.put("$or", 11);
		map.put("$nor", 12);
		map.put("$mod", 13);
		map.put("$all", 14);
		map.put("$size", 15);
		map.put("$type", 16);
		map.put("$within", 17);
		map.put("$near", 18);
		map.put("$nearSphere", 19);
		map.put("$maxDistance", 20);
		map.put("$elemMatch", 21);
		map.put("$set", 22);
		map.put("$inc", 23);
	}
}