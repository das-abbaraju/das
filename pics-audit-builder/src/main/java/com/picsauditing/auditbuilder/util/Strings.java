package com.picsauditing.auditbuilder.util;

import java.util.Collection;

public class Strings {

	public static final String EMPTY_STRING = "";
	private static final int NO_STRING_ESCAPE_STRATEGY = 0;
	private static final int STRING_ESCAPE_STRATEGY = 1;
	private static final int OBJECT_TO_STRING_ESCAPE_STRATEGY = 2;

	public static boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}

		value = value.trim();
		return value.length() == 0;
	}

	public static String escapeQuotes(String value) {
		if (isEmpty(value))
			return EMPTY_STRING;

		String singleQuote = "\'";

		return value.replace(singleQuote, singleQuote + singleQuote);
	}

    public static String escapeSlashes(String value) {
        if (isEmpty(value))
            return EMPTY_STRING;

        String singleSlash = "\\";

        return value.replace(singleSlash, singleSlash + singleSlash);
    }

    public static String escapeQuotesAndSlashes(String value) {
        return escapeSlashes(escapeQuotes(value));
    }

	public static String implode(Collection<? extends Object> collection) {
		return genericImplode(collection, ",", NO_STRING_ESCAPE_STRATEGY);
	}

	private static <E> String genericImplode(Collection<E> collection, String delimiter, int escapeType) {
        if (collection == null || collection.size() == 0) {
            return EMPTY_STRING;
        }

		StringBuilder builder = new StringBuilder();
		for (E entity : collection) {
			if (builder.length() > 0) {
				builder.append(delimiter);
			}

			appendEntity(builder, entity, escapeType);
		}

		return builder.toString();
	}

	private static <E> void appendEntity(StringBuilder builder, E entity, int escapeType) {
		switch (escapeType) {
		case NO_STRING_ESCAPE_STRATEGY:
			builder.append(entity);
			break;

		case STRING_ESCAPE_STRATEGY:
			performStringEscapeStrategy(builder, entity);
			break;

		case OBJECT_TO_STRING_ESCAPE_STRATEGY:
			builder.append("'").append(escapeQuotesAndSlashes(String.valueOf(entity))).append("'");
			break;

		default:
			throw new RuntimeException("Invalid use of string escaping.");
		}
	}

	private static <E> void performStringEscapeStrategy(StringBuilder stringBuilder, E entity) {
		stringBuilder.append("'");

		if (entity instanceof String) {
			stringBuilder.append(escapeQuotesAndSlashes((String) entity));
		} else {
			stringBuilder.append(entity);
		}

		stringBuilder.append("'");
	}
}