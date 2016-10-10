package fr.sparna.rdf.skos.xls2skos;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

public class Xls2SkosException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static void when(boolean test) {
		if (test) {
			throw new Xls2SkosException("Assertion failed");
		}
	}

	public static void when(boolean test, String message) {
		if (test) {
			throw new Xls2SkosException(message);
		}
	}

	public static void when(boolean test, String message, Object... parameters) {
		if (test) {
			throw new Xls2SkosException(message, parameters);
		}
	}

	public static Xls2SkosException rethrow(Throwable exception) {
		if (exception instanceof Error) {
			throw (Error) exception;
		}
		if (exception instanceof RuntimeException) {
			throw (RuntimeException) exception;
		}
		throw new Xls2SkosException(exception);
	}

	public static <T> T failIfNotInstance(Object object, Class<T> clazz, String message, Object... parameters) {
		when(!clazz.isInstance(object), message, parameters);
		//noinspection unchecked
		return (T) object;
	}

	public static <T> T failIfNull(T value, String message, Object... parameters) {
		when(null == value, message, parameters);
		//noinspection ConstantConditions
		return value;
	}

	public static <T extends CharSequence> T failIfBlank(T value, String message, Object... parameters) {
		when(StringUtils.isBlank(value), message, parameters);
		//noinspection ConstantConditions
		return value;
	}

	public Xls2SkosException() {
	}

	public Xls2SkosException(String message) {
		super(message);
	}

	public Xls2SkosException(Throwable cause, String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage(), cause);
	}

	public Xls2SkosException(String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage());
	}

	public Xls2SkosException(Throwable cause) {
		super(cause);
	}
}
