package com.eussence.mosquito.command.wrapper

import java.util.Map.Entry

/**
 * An entry point for triggering extensions added to standard classes.
 * 
 * @author Ernest Kiwele
 */
class ProgramExtensionsManager {

	static void setupDefaults() {
		extendMaps()
		extendLists()
	}

	static void extendMaps() {
		addMixin(LinkedHashMap, MapExtensions)
		addMixin(HashMap, MapExtensions)
		addMixin(TreeMap, MapExtensions)
	}

	static void extendLists() {
		addMixin(ArrayList, ListExtensions)
		addMixin(LinkedList, ListExtensions)
		addMixin(List, ListExtensions)
	}

	public static void addMixin(Class extendedClass, Class providerClass) {
		extendedClass.metaClass.mixin(providerClass)
	}

	public static void addMixins(List<Entry<Class, Class>> pairs) {
		pairs?.each { addMixin(it.key, it.value) }
	}
}