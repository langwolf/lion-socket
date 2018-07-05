package com.lioncorp.server;

import java.util.List;

public interface Inject {
	 List<Class<?>> inject(String packageName);
	 <T>boolean inspect(Class<T> clazz);
}
