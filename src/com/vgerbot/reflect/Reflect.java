package com.vgerbot.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

@SuppressWarnings("restriction")
public abstract class Reflect {
	// **************************************************************************
	// 定义修饰符
	// **************************************************************************
	public static final int STATIC = Modifier.STATIC;
	public static final int PUBLIC = Modifier.PUBLIC;
	public static final int PRIVATE = Modifier.PRIVATE;
	public static final int PROTECTED = Modifier.PROTECTED;
	public static final int FINAL = Modifier.FINAL;
	public static final int TRANSIENT = Modifier.TRANSIENT;
	public static final int VOLATILE = Modifier.VOLATILE;

	// **************************************************************************
	// 定义反修饰符
	// **************************************************************************
	public static final int NOT_STATIC = ~STATIC;
	public static final int NOT_PUBLIC = ~PUBLIC;
	public static final int NOT_PRIVATE = ~PRIVATE;
	public static final int NOT_PROTECTED = ~PROTECTED;
	public static final int NOT_FINAL = ~FINAL;
	public static final int NOT_TRANSIENT = ~TRANSIENT;
	public static final int NOT_VOLATILE = ~VOLATILE;

	// 空数组避免过多临时对象
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

	private static final URL[] emptyURLArray = new URL[] {};

	// 基本类型映射
	private static final Map<String, Class<?>> PRIMTYPEMAPPING = new HashMap<String, Class<?>>();

	static {
		PRIMTYPEMAPPING.put("int", int.class);
		PRIMTYPEMAPPING.put("string", String.class);
		PRIMTYPEMAPPING.put("char", char.class);
		PRIMTYPEMAPPING.put("byte", byte.class);
		PRIMTYPEMAPPING.put("short", short.class);
		PRIMTYPEMAPPING.put("float", float.class);
		PRIMTYPEMAPPING.put("long", long.class);
		PRIMTYPEMAPPING.put("double", double.class);
		PRIMTYPEMAPPING.put("boolean", boolean.class);
		PRIMTYPEMAPPING.put("void", void.class);
		PRIMTYPEMAPPING.put("class", Class.class);
	}

	// ********************************************************************
	// 基本类型判断方法， 判断一个类型是基本类型或者包装类型
	// ********************************************************************
	public static boolean isInt(Class<?> type) {
		return int.class == type || Integer.class == type;
	}

	public static boolean isFloat(Class<?> type) {
		return float.class == type || Float.class == type;
	}

	public static boolean isDouble(Class<?> type) {
		return double.class == type || Double.class == type;
	}

	public static boolean isShort(Class<?> type) {
		return short.class == type || Short.class == type;
	}

	public static boolean isByte(Class<?> type) {
		return byte.class == type || Byte.class == type;
	}

	public static boolean isChar(Class<?> type) {
		return char.class == type || Character.class == type;
	}

	public static boolean isLong(Class<?> type) {
		return long.class == type || Long.class == type;
	}

	public static boolean isBoolean(Class<?> type) {
		return boolean.class == type || Boolean.class == type;
	}

	public static boolean isText(Class<?> type) {
		return CharSequence.class.isAssignableFrom(type);
	}

	// 第一个字母小写
	private static final String unTitle(String str) {
		if (str == null || str.length() < 1)
			return null;
		char[] chs = str.toCharArray();
		chs[0] = Character.toLowerCase(chs[0]);
		return new String(chs);
	}

	/**
	 * 获取包装类
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param type
	 * @return
	 */
	public static Class<?> wrapper(Class<?> type) {
		if (type == null) {
			return null;
		} else if (type.isPrimitive()) {
			if (boolean.class == type) {
				return Boolean.class;
			} else if (int.class == type) {
				return Integer.class;
			} else if (long.class == type) {
				return Long.class;
			} else if (short.class == type) {
				return Short.class;
			} else if (byte.class == type) {
				return Byte.class;
			} else if (double.class == type) {
				return Double.class;
			} else if (float.class == type) {
				return Float.class;
			} else if (char.class == type) {
				return Character.class;
			} else if (void.class == type) {
				return Void.class;
			}
		}
		return type;
	}

	/**
	 * 获取基本类
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param wrapper
	 * @return
	 */
	public static Class<?> primitiveType(Class<?> wrapper) {
		if (wrapper == null) {
			return null;
		} else if (!wrapper.isPrimitive()) {
			if (Integer.class == wrapper) {
				return int.class;
			} else if (Short.class == wrapper) {
				return short.class;
			} else if (Byte.class == wrapper) {
				return byte.class;
			} else if (Float.class == wrapper) {
				return float.class;
			} else if (Character.class == wrapper) {
				return char.class;
			} else if (Long.class == wrapper) {
				return long.class;
			} else if (Double.class == wrapper) {
				return double.class;
			} else if (Boolean.class == wrapper) {
				return boolean.class;
			} else if (Void.class == wrapper) {
				return void.class;
			}
		}
		return null;
	}

	/**
	 * 判断是否为基本类型
	 * 
	 * @param cls
	 * @return null或者不是基本类型的情况返回false,其它情况返回true
	 */
	public static boolean isPrimitiveType(Class<?> cls) {
		return cls != null && cls.isPrimitive();
	}

	/**
	 * 根据名称判断是否为基本类型
	 * 
	 * @see #PRIMTYPEMAPPING
	 * @param name
	 * @return
	 */
	public static boolean isPrimitiveType(String name) {
		Class<?> cls = PRIMTYPEMAPPING.get(name);
		return isPrimitiveType(cls);
	}

	/**
	 * 将访问受限的对象转为访问不受限的对象
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param accessible
	 *            访问受限的对象
	 * @return 如果accessible不为空，则为访问不受限的对象
	 */
	public static <T extends AccessibleObject> T accessible(T accessible) {
		if (accessible == null)
			return null;
		if (accessible instanceof Member) {
			Member m = (Member) accessible;
			if (Modifier.isPublic(m.getModifiers())
					&& Modifier.isPublic(((Member) accessible).getDeclaringClass().getModifiers())) {
				return accessible;
			}
		}
		if (!accessible.isAccessible()) {
			accessible.setAccessible(true);
		}
		return accessible;
	}

	private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

	/**
	 * 打破final修饰，使字段可写
	 * 
	 * @param field
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static Field breakFinal(Field field) throws IllegalAccessException, NoSuchFieldException {
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		modifiers &= ~Modifier.FINAL;
		modifiersField.setInt(field, modifiers);
		return field;
	}

	// ----------------------------------------------------------------
	// 内部工具方法
	// ----------------------------------------------------------------
	private static Reflect nullReflect() {
		return new NullReflect(null);
	}

	private static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable t) {
			// cannot access thread context classLoader - falling back
		}
		if (cl == null) {
			cl = Reflect.class.getClassLoader();
			if (cl == null) {
				cl = ClassLoader.getSystemClassLoader();
			}
		}
		return cl;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> forName(String name, ClassLoader loader) throws ReflectException {
		Class<?> pmtype = PRIMTYPEMAPPING.get(name);
		if (pmtype != null) {
			return (Class<T>) pmtype;
		}
		// "java.lang.String[]" style arrays
		if (name.endsWith("[]")) {
			Class<?> emlClass = forName(name.substring(0, name.length() - 2));
			return (Class<T>) Array.newInstance(emlClass, 0).getClass();
		}
		// "[I" or "[Ljava.lang.String" style arrays
		if (name.startsWith("[L") || name.startsWith("[I")) {
			Class<?> elmClass = forName(name.substring(2), loader);
			return (Class<T>) Array.newInstance(elmClass, 0).getClass();
		}
		// "[[[I" or "[[[[Ljava.lang.String" style arrays
		if (name.charAt(0) == '[') {
			Class<?> elmClass = forName(name.substring(1), loader);
			return (Class<T>) Array.newInstance(elmClass, 0).getClass();
		}
		if (loader == null) {
			loader = getDefaultClassLoader();
		}
		try {
			return (Class<T>) (loader != null ? loader.loadClass(name) : Class.forName(name));
		} catch (ClassNotFoundException e) {
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String clsName = name.substring(0, lastDotIndex) + "$" + name.substring(lastDotIndex + 1);
				try {
					return (Class<T>) (loader != null ? loader.loadClass(clsName) : Class.forName(clsName));
				} catch (ClassNotFoundException ex) {
				}
			}
			throw new ReflectException(e);
		}
	}

	private static <T> Class<T> forName(String name) throws ReflectException {
		return forName(name, null);
	}

	private static Class<?>[] types(Object[] values) {
		if (values == null)
			return EMPTY_CLASS_ARRAY;
		Class<?>[] types = new Class[values.length];
		for (int i = 0; i < values.length; i++) {
			types[i] = values[i] == null ? NULL.class : values[i].getClass();
		}
		return types;
	}

	private static final boolean matchModifiers(int a, int b) {
		return (a & b) == a || (a | b) == a;
	}

	/**
	 * 给定方法名和参数，匹配一个最接近的方法
	 */
	private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
		Class<?> type = type();

		// 对于公有方法:
		for (Method method : type.getMethods()) {
			if (isSimilarSignature(method, name, types)) {
				return method;
			}
		}

		// 对于私有方法：
		do {
			for (Method method : type.getDeclaredMethods()) {
				if (isSimilarSignature(method, name, types)) {
					return method;
				}
			}

			type = type.getSuperclass();
		} while (type != null);

		throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types)
				+ " could be found on type " + type() + ".");
	}

	/**
	 * 方法参数类型匹配
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param declaredTypes
	 * @param actualTypes
	 * @return
	 */
	private static boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
		if (declaredTypes.length == actualTypes.length) {
			for (int i = 0; i < actualTypes.length; i++) {
				if (actualTypes[i] == NULL.class)
					continue;

				if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
					continue;

				return false;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 再次确认方法签名与实际是否匹配， 将基本类型转换成对应的对象类型， 如int转换成Integer
	 */
	private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName,
			Class<?>[] desiredParamTypes) {
		return possiblyMatchingMethod.getName().equals(desiredMethodName)
				&& match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
	}

	public static final ClassReflect find(List<String> importPackages, String simpleName, URL[] urls)
			throws ReflectException {
		if (simpleName == null) {
			return Reflect.on(Object.class);
		}

		if (simpleName.length() < 8 && simpleName.length() > 2) {
			Class<?> ft = PRIMTYPEMAPPING.get(simpleName);
			if (ft != null) {
				return Reflect.on(ft);
			}
		}
		if (urls == null) {
			urls = emptyURLArray;
		}
		URLClassLoader uloader = new URLClassLoader(urls);
		try {
			Class<?> type = null;
			if (importPackages != null && !importPackages.isEmpty()) {
				StringBuilder cname = new StringBuilder();
				Iterator<String> it = importPackages.iterator();
				while (type == null && it.hasNext()) {
					String imp = it.next();
					if (imp.endsWith("*")) {
						cname.append(imp.substring(0, imp.length() - 1)).append(simpleName);
					} else if (imp.endsWith(simpleName)) {
						cname.setLength(0);
						cname.append(imp);
					} else {
						cname.append(imp).append('.').append(simpleName);
					}
					try {
						type = uloader.loadClass(cname.toString());
					} catch (ClassNotFoundException e) {
					}
					cname.setLength(0);
				}
			}
			if (type == null) {
				try {
					type = uloader.loadClass(simpleName);
				} catch (ClassNotFoundException e) {
				}
			}
			if (type == null) {
				try {
					type = Class.forName(simpleName);
				} catch (ClassNotFoundException e) {
				}
			}
			if (type == null) {
				throw new ReflectException("Class not found:" + simpleName);
			}
			return Reflect.on(type);
		} finally {
			try {
				uloader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static ObjectReflect on(Reflect from, Object object) throws ReflectException {
		return new ObjectReflect(from, object);
	}

	public static ObjectReflect on(Object object) throws ReflectException {
		return on(nullReflect(), object);
	}

	public static BatchReflect on(Object[] objects) throws ReflectException {
		return on(nullReflect(), objects);
	}

	public static BatchReflect on(Reflect from, Object[] objects) throws ReflectException {
		if (objects == null || objects.length < 1) {
			return new BatchReflect(nullReflect(), objects);
		}
		Reflect[] reflects = new Reflect[objects.length];
		for (int i = 0; i < objects.length; i++) {
			reflects[i] = auto(objects[i]);
		}
		return new BatchReflect(from, objects, reflects);
	}

	public static ClassReflect on(String name) throws ReflectException {
		if (name == null || name.length() < 1)
			throw new ReflectException("no characters!");
		return on(forName(name));
	}

	public static ClassReflect on(String name, ClassLoader loader) throws ReflectException {
		if (name == null || name.length() < 1)
			throw new ReflectException("no characters!");
		return on(forName(name, loader));
	}

	public static MapReflect on(Map<String, Object> map) throws ReflectException {
		return new MapReflect(nullReflect(), map == null ? new HashMap<String, Object>(0) : map);
	}

	public static ClassReflect on(Class<?> clazz) throws ReflectException {
		if (clazz == null) {
			throw new ReflectException("Illegal argument:null");
		}
		return new ClassReflect(nullReflect(), clazz);
	}

	public static <T extends Enum<T>> EnumReflect<T> onEnum(Class<T> enumType) throws ReflectException {
		if (enumType == null) {
			throw new ReflectException("enum type == null");
		}
		return new EnumReflect<T>(enumType);
	}

	public static FieldReflect on(Field field) throws ReflectException {
		return on(field, null);
	}

	public static FieldReflect on(Field field, Object object) throws ReflectException {
		if (field == null) {
			throw new ReflectException("field is null");
		}
		return new FieldReflect(nullReflect(), field, object);
	}

	public static MethodReflect on(Method method, Object object) {
		if (method == null) {
			throw new ReflectException("method is null");
		}
		return new MethodReflect(nullReflect(), method, object);
	}

	public static MethodReflect on(Method method, Object... arguments) {
		if (method == null) {
			throw new ReflectException("method is null");
		}
		return new MethodReflect(nullReflect(), method, null, arguments);
	}

	public static MethodReflect on(Method method, Object object, Object... arguments) {
		if (method == null) {
			throw new ReflectException("method is null");
		}
		return new MethodReflect(nullReflect(), method, object, arguments);
	}

	public static MethodReflect on(Method method) {
		return on(method, EMPTY_OBJECT_ARRAY);
	}

	public static ConstructorReflect on(Constructor<?> constructor, Object... arguments) {
		if (constructor == null) {
			throw new ReflectException("constructor is null");
		}
		return new ConstructorReflect(nullReflect(), constructor, arguments);
	}

	public static ConstructorReflect on(Constructor<?> constructor) {
		return on(constructor, EMPTY_OBJECT_ARRAY);
	}

	/**
	 * @param object
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Reflect auto(Object object) {
		if (object == null) {
			return nullReflect();
		}
		if (object instanceof Class<?> && ((Class) object).isEnum()) {
			return Reflect.on(Reflect.class).method("onEnum", object.getClass()).call(object).release().off();
		}
		try {
			return Reflect.on(Reflect.class).method("on", object.getClass()).call(object).release().off();
		} catch (Exception e) {
			return Reflect.on(object);
		}
	}

	protected Class<?> type;

	protected Reflect from;

	private ConcurrentMap<String, Field> fieldsMap = new ConcurrentHashMap<String, Field>();

	private ConcurrentMap<MethodKey, Method> methodMap = new ConcurrentHashMap<MethodKey, Method>();

	public Reflect(Reflect from) {
		this.from = from;
	}

	public boolean isClass() {
		return false;
	}

	public boolean isField() {
		return false;
	}

	public boolean isMethod() {
		return false;
	}

	public boolean isConstructor() {
		return false;
	}

	public boolean isMap() {
		return false;
	}

	public boolean isNull() {
		return false;
	}

	public ObjectReflect asObject() {
		return new ObjectReflect(from, off());
	}

	/**
	 * 获取被包装的对象
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public abstract <T> T off();

	public final Class<?> type() {
		return type;
	}

	/**
	 * @author y1j2x34
	 * @date 2015年3月31日
	 * @param name
	 *            方法名
	 * @return
	 * @throws ReflectException
	 *             方法找不到时抛出该异常
	 */
	public MethodReflect method(String name) throws ReflectException {
		return method(name, EMPTY_CLASS_ARRAY);
	}

	/**
	 * @author y1j2x34
	 * @date 2015年3月31日
	 * @param name
	 *            方法名
	 * @param parameterTypes
	 *            参数类型数组
	 * @return
	 * @throws ReflectException
	 *             方法找不到时抛出该异常
	 */
	public MethodReflect method(String name, Class<?>... parameterTypes) throws ReflectException {
		Method method = method0(name, parameterTypes);
		if (method == null)
			return null;
		return new MethodReflect(this, method, off());
	}

	public MethodReflect method(String name, Object... arguments) throws ReflectException {
		return method(name, types(arguments));
	}

	/**
	 * 获取字段
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param name
	 * @return null if field not exist
	 */
	public FieldReflect field(String name) {
		Field field = field0(name);
		if (field != null) {
			return new FieldReflect(this, field, off());
		} else {
			return null;
		}
	}

	private Field field0(String name) throws ReflectException {
		Field field = fieldsMap.get(name);
		if (field == null) {
			Class<?> type = type();
			try {
				field = type.getField(name);
			} catch (NoSuchFieldException e) {
				while (type != null) {
					try {
						field = accessible(type.getDeclaredField(name));
						break;
					} catch (NoSuchFieldException ignore) {
					}
					type = type.getSuperclass();
				}
			}
			Field previous = fieldsMap.putIfAbsent(name, field);
			if (previous != null) {
				field = previous;
			}
		}
		return field;
	}

	/**
	 * @param modifier
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public Map<String, FieldReflect> fields(int modifiers) {
		return fields0(modifiers);
	}

	/**
	 * 所有字段对应的值
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public Map<String, Reflect> fieldValues() {
		Map<String, FieldReflect> fields = fields0(0);
		Map<String, Reflect> fieldValues = new HashMap<String, Reflect>(fields.size());
		for (Map.Entry<String, FieldReflect> entry : fields.entrySet()) {
			String name = entry.getKey();
			FieldReflect fieldReflect = entry.getValue();
			fieldValues.put(name, fieldReflect.get());
		}
		return fieldValues;
	}

	private Map<String, FieldReflect> fields0(int modifiers) {
		Map<String, FieldReflect> fields = new HashMap<String, FieldReflect>();
		Class<?> type = type();
		while (type != null) {
			Field[] fs = type.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				int fmod = fs[i].getModifiers();
				if (matchModifiers(fmod, modifiers)) {
					String name = fs[i].getName();
					if (!fields.containsKey(name)) {
						fields.put(name, field(name));
					}
				}
			}
			type = type.getSuperclass();
		}
		return fields;
	}

	private Method method0(String name, Class<?>... parameterTypes) throws ReflectException {
		MethodKey key = new MethodKey(name, parameterTypes);
		Method method = methodMap.get(key);
		if (method == null) {
			method = method1(name, parameterTypes);
			methodMap.put(key, method);
		}
		return method;
	}

	private Method method1(String name, Class<?>... parameterTypes) {
		Class<?> type = type();
		try {
			return type.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			while (type != null) {
				try {
					return accessible(type.getDeclaredMethod(name, parameterTypes));
				} catch (NoSuchMethodException ignore) {
				}
				type = type.getSuperclass();
			}
			try {
				return similarMethod(name, parameterTypes);
			} catch (NoSuchMethodException e1) {
				throw new ReflectException(e1);
			}
		}
	}

	/**
	 * 指定对象来调用某个方法
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param receiver
	 * @param name
	 * @param arguments
	 * @return
	 * @throws ReflectException
	 */
	public Reflect callBy(Object receiver, String name, Object... arguments) throws ReflectException {
		MethodReflect mr = method(name);
		if (mr != null) {
			return mr.callBy(this, receiver, arguments);
		} else {
			return nullReflect();
		}
	}

	/**
	 * 调用无参方法
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param name
	 * @return
	 * @throws ReflectException
	 */
	public Reflect call(String name) throws ReflectException {
		MethodReflect mr = method(name);
		if (mr != null) {
			return mr.call(this);
		} else {
			return nullReflect();
		}
	}

	/**
	 * 调用对象上的方法
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param name
	 * @param arguments
	 * @return
	 * @throws ReflectException
	 */
	public Reflect call(String name, Object... arguments) throws ReflectException {
		if (name == null || name.length() < 1) {
			throw new ReflectException("Illegal method name:" + name);
		}
		Class<?>[] types = types(arguments);
		return method(name, types).call(this, arguments);
	}

	/**
	 * 创建实例（无参数）
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @return
	 * @throws ReflectException
	 */
	public Reflect create() throws ReflectException {
		return create(EMPTY_OBJECT_ARRAY);
	}

	/**
	 * 创建实例（有参数）
	 * 
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param arguments
	 *            构造器参数和查找对应构造器的依据
	 * @return
	 * @throws ReflectException
	 */
	public Reflect create(Object... arguments) throws ReflectException {
		Class<?>[] types = types(arguments);
		try {
			Constructor<?> constructor = type().getDeclaredConstructor(types);
			return new ConstructorReflect(this, constructor).create(this, arguments);
		} catch (NoSuchMethodException e) {
			for (Constructor<?> constructor : type().getDeclaredConstructors()) {
				if (match(constructor.getParameterTypes(), types)) {
					return new ConstructorReflect(this, constructor).create(this, arguments);
				}
			}
			throw new ReflectException(e);
		}
	}

	/**
	 * 获得链式调用的上一节点
	 * 
	 * @author y1j2x34
	 * @date 2015年3月31日
	 * @return
	 */
	public Reflect back() {
		return from;
	}

	/**
	 * 从链条中释放,释放后该Reflect不再持有链式调用的上一个节点引用，back() 方法将返回 NullReflect
	 * 
	 * @return
	 */
	public Reflect release() {
		from = nullReflect();
		return this;
	}

	/**
	 * 判断是否等价
	 * 
	 * @author y1j2x34
	 * @date 2015年3月31日
	 * @param other
	 * @return
	 */
	public boolean is(Reflect other) {
		return other != null && this == other || other.off() == this.off();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass()).append('<').append(String.valueOf(off())).append('>').toString();
	}

	public static class AnnotatedReflect<A extends AnnotatedElement> extends Reflect {
		protected A value;
		private List<Annotation> annotations;
		private List<Annotation> declaredAnnotations;

		public AnnotatedReflect(Reflect from, A value) {
			super(from);
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public A off() {
			return value;
		}

		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return this.value.getAnnotation(annotationClass);
		}

		public <T extends Annotation> List<T> getAnnotations(Class<T> annotationClass) {
			T annotation = value.getAnnotation(annotationClass);
			if (annotation == null) {
				return Arrays.asList();
			}
			return Arrays.asList(annotation);
		}

		public List<Annotation> getDeclaredAnnotations() {
			List<Annotation> declaredAnnotations = this.declaredAnnotations;
			if (declaredAnnotations == null) {
				Annotation[] annos = value.getDeclaredAnnotations();
				if (annos == null || annos.length < 1) {
					declaredAnnotations = new ArrayList<Annotation>(0);
				}
				declaredAnnotations = Arrays.asList(annos);
				this.declaredAnnotations = declaredAnnotations;
			}
			return declaredAnnotations;
		}

		public List<Annotation> getAnnotations() {
			List<Annotation> annotations = this.annotations;
			if (annotations == null) {
				Annotation[] annos = value.getAnnotations();
				if (annos == null || annos.length < 1) {
					annotations = new ArrayList<Annotation>(0);
				} else {
					annotations = Arrays.asList(annos);
				}
				this.annotations = annotations;
			}
			return annotations;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!AnnotatedReflect.class.isAssignableFrom(obj.getClass()))
				return false;
			AnnotatedReflect<?> other = (AnnotatedReflect<?>) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	public static class ClassReflect extends AnnotatedReflect<Class<?>> {
		public ClassReflect(Reflect from, Class<?> type) {
			super(from, type);
			super.type = type;
		}

		@Override
		public Class<?> off() {
			return type;
		}

		@Override
		public boolean isClass() {
			return true;
		}

		@Override
		public String toString() {
			return String.valueOf(super.type);
		}

		public boolean isAbstract() {
			return Modifier.isAbstract(super.type.getModifiers());
		}

		public boolean isInterface() {
			return Modifier.isInterface(super.type.getModifiers());
		}

		public boolean isStrict() {
			return Modifier.isStrict(super.type.getModifiers());
		}

		public boolean isFinal() {
			return Modifier.isFinal(super.type.getModifiers());
		}

		public boolean isStatic() {
			return Modifier.isStatic(super.type.getModifiers());
		}

		public ClassReflect superclass() {
			return Reflect.on(super.type.getSuperclass());
		}

		public boolean isParentTypeOf(Class<?> type) {
			return super.type.isAssignableFrom(type);
		}

		public boolean isParentTypeOf(Reflect r) {
			if (r == null)
				return false;
			return isParentTypeOf(r.type());
		}

		public boolean isChildTypeOf(Class<?> type) {
			return type != null && type.isAssignableFrom(super.type);
		}

		public boolean isChildTypeOf(Reflect r) {
			if (r == null)
				return false;
			return isChildTypeOf(r.type());
		}

		public boolean isInt() {
			return isInt(super.type);
		}

		public boolean isFloat() {
			return isFloat(super.type);
		}

		public boolean isShort() {
			return isShort(super.type);
		}

		public boolean isByte() {
			return isByte(super.type);
		}

		public boolean isBoolean() {
			return isBoolean(super.type);
		}

		public boolean isLong() {
			return isLong(super.type);
		}

		public boolean isDouble() {
			return isDouble(super.type);
		}

		public boolean isChar() {
			return isChar(super.type);
		}

		public boolean isText() {
			return isText(super.type);
		}

		public boolean isAnnotation() {
			return super.type.isAnnotation();
		}

		public boolean hasEnclosingClass() {
			return super.type.getEnclosingClass() != null;
		}

		/**
		 * 顶级类或嵌套类（静态内部类）
		 * 
		 * @return
		 */
		public boolean isIndependent() {
			return !hasEnclosingClass() || (super.type.getDeclaringClass() != null && !isStatic());
		}

		@Override
		public ClassReflect release() {
			super.release();
			return this;
		}

		public void simpleConvert(Object obj) {

		}
	}

	public static class MemberReflect<M extends Member & AnnotatedElement> extends AnnotatedReflect<M> {
		// private M member;
		private Object receiver;

		public MemberReflect(Reflect from, M member) {
			this(from, member, null);
		}

		public MemberReflect(Reflect from, M member, Object receiver) {
			super(from, member);
			// this.member = member;
			if (member != null) {
				super.type = member.getClass();
			}
			this.receiver = receiver;
		}

		@Override
		public M off() {
			return super.off();
		}

		public boolean isStatic() {
			return Modifier.isStatic(off().getModifiers());
		}

		public boolean isPublic() {
			return Modifier.isPublic(off().getModifiers());
		}

		public boolean isPrivate() {
			return Modifier.isPrivate(off().getModifiers());
		}

		public boolean isProtected() {
			return Modifier.isProtected(off().getModifiers());
		}

		public boolean isFinal() {
			return Modifier.isFinal(off().getModifiers());
		}

		/**
		 * 取得定义该成员的类
		 * 
		 * @author y1j2x34
		 * @version 1.0
		 * @date 2015-3-31
		 * @return
		 */
		public ClassReflect declaring() {
			return new ClassReflect(this, off().getDeclaringClass());
		}

		@Override
		public MemberReflect<M> release() {
			super.release();
			return this;
		}

		public Object getReceiver() {
			return receiver;
		}
	}

	public static class FieldReflect extends MemberReflect<Field> {

		private FieldAccessor fieldAccessor;

		public FieldReflect(Reflect from, Field member, Object object) {
			super(from, member, object);
		}

		public FieldReflect(Reflect from, Field member) {
			super(from, member);
		}

		public FieldReflect set(Object value) throws ReflectException {
			return set(value, true);
		}

		public FieldReflect set(Object value, boolean force) throws ReflectException {
			try {
				if (!force) {
					super.value.set(super.receiver, value);
				} else {
					if (fieldAccessor == null) {
						Field field = breakFinal(super.value);
						fieldAccessor = reflectionFactory.newFieldAccessor(field, false);
					}
					fieldAccessor.set(super.receiver, value);
				}
			} catch (Exception e) {
				throw new ReflectException(e);
			}
			return this;
		}

		public Reflect get(Object object) throws ReflectException {
			return on(this, getValue(object));
		}

		public Reflect get() throws ReflectException {
			return on(this, getValue(super.receiver));
		}

		@SuppressWarnings("unchecked")
		public <T> T getValue(Object object) throws ReflectException {
			try {
				return (T) (accessible(super.off()).get(object));
			} catch (Exception e) {
				throw new ReflectException(e);
			}
		}

		public <T> T getValue() throws ReflectException {
			return getValue(super.receiver);
		}

		@Override
		public boolean isField() {
			return true;
		}

		public boolean isTransient() {
			return Modifier.isTransient(super.off().getModifiers());
		}

		public boolean isVolatile() {
			return Modifier.isVolatile(super.off().getModifiers());
		}

		public FieldReflect noFinal() {
			if (!isFinal()) {
				return this;
			}
			try {
				breakFinal(super.value);
			} catch (IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			}
			return this;
		}

		@Override
		public Field off() {
			return super.off();
		}

		@Override
		public FieldReflect release() {
			super.release();
			return this;
		}

		public Class<?> fieldType() {
			return super.value.getType();
		}

	}

	public static class MethodReflect extends MemberReflect<Method> {
		private boolean bind = false;
		private Object[] arguments = EMPTY_OBJECT_ARRAY;
		private String[] parameterNames;
		private Class<?>[] parameterTypes;
		private Annotation[][] parameterAnnotations;

		public MethodReflect(Reflect from, Method member) {
			super(from, member);
		}

		public MethodReflect(Reflect from, Method member, Object receiver) {
			super(from, member, receiver);
			bind = true;
		}

		public MethodReflect(Reflect from, Method member, Object receiver, Object[] arguments) {
			super(from, member, receiver);
			if (arguments != null) {
				this.arguments = arguments;
			}
			bind = true;
		}

		public Annotation[][] getParameterAnnotations() {
			if (parameterAnnotations == null) {
				parameterAnnotations = this.value.getParameterAnnotations();
			}
			return parameterAnnotations;
		}

		public Class<?>[] getParameterTypes() {
			if (parameterTypes == null) {
				parameterTypes = this.value.getParameterTypes();
			}
			return parameterTypes;
		}

		public String[] getParameterNames() {
			if (parameterNames == null) {
				int parameterSize = parameterSize();
				parameterNames = new String[parameterSize];
				for (int i = 0; i < parameterSize; i++) {
					parameterNames[i] = "args" + i;
				}
			}
			return parameterNames;
		}

		public MethodReflect bind(Object... arguments) {
			this.arguments = arguments;
			bind = true;
			return this;
		}

		public Reflect call() throws ReflectException {
			return callBy(super.receiver, arguments);
		}

		public Reflect call(Object... arguments) throws ReflectException {
			return callBy(super.receiver, arguments);
		}

		private Reflect callBy(Reflect from, Object receiver, Object... arguments) throws ReflectException {
			try {
				Object returns = accessible(super.off()).invoke(receiver, arguments);
				if (returns == null) {
					return new NullReflect(from);
				}
				return on(from, returns);
			} catch (ReflectException e) {
				throw e;
			} catch (Exception e) {
				throw new ReflectException(e);
			}
		}

		public Reflect callBy(Object receiver, Object... arguments) throws ReflectException {
			return callBy(this, receiver, arguments);
		}

		public Reflect callBy(Object receiver) throws ReflectException {
			return callBy(this, receiver, this.arguments);
		}

		private Reflect call(Reflect from, Object... arguments) throws ReflectException {
			return callBy(from, super.receiver, arguments);
		}

		public int parameterSize() {
			return getParameterTypes().length;
		}

		public ClassReflect parameterTypeAt(int index) {
			Class<?>[] types = super.off().getParameterTypes();
			return types.length == 0 ? null : new ClassReflect(this, types[index]);
		}

		@Override
		public boolean isMethod() {
			return true;
		}

		public boolean isNative() {
			return Modifier.isNative(super.off().getModifiers());
		}

		public boolean isSynchronized() {
			return Modifier.isSynchronized(super.off().getModifiers());
		}

		public boolean isAbstract() {
			return Modifier.isAbstract(super.off().getModifiers());
		}

		public boolean isStrict() {
			return Modifier.isStrict(super.off().getModifiers());
		}

		@Override
		public Method off() {
			return super.off();
		}

		@Override
		public MethodReflect release() {
			super.release();
			return this;
		}

		public Object[] getArguments() {
			return arguments;
		}

		public boolean isBind() {
			return bind;
		}
	}

	public static class BatchMethodReflect extends MethodReflect implements Iterable<MethodReflect> {
		private final MethodReflect[] reflects;

		public BatchMethodReflect(Reflect from, MethodReflect[] reflects) {
			this(from, reflects, null, EMPTY_OBJECT_ARRAY);
		}

		public BatchMethodReflect(Reflect from, MethodReflect[] reflects, Object[] arguments) {
			this(from, reflects, null, arguments);
		}

		public BatchMethodReflect(Reflect from, MethodReflect[] reflects, Object receiver, Object[] arguments) {
			super(from, null, receiver, arguments);
			this.reflects = reflects;
		}

		public boolean isNative() {
			boolean isNative = true;
			for (MethodReflect reflect : reflects) {
				isNative &= reflect.isNative();
			}
			return isNative;
		}

		public boolean isSynchronized() {
			boolean isSynchronized = true;
			for (MethodReflect reflect : reflects) {
				isSynchronized &= reflect.isSynchronized();
			}
			return isSynchronized;
		}

		public boolean isAbstract() {
			boolean isAbstract = true;
			for (MethodReflect reflect : reflects) {
				isAbstract &= reflect.isAbstract();
			}
			return isAbstract;
		}

		public boolean isStrict() {
			boolean isStrict = true;
			for (MethodReflect reflect : reflects) {
				isStrict &= reflect.isStrict();
			}
			return isStrict;
		}

		@Override
		public BatchReflect call() throws ReflectException {
			Object[] returned = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				Reflect returnedReflect = reflect.call();
				returned[i] = returnedReflect.off();
				returnedReflects[i] = returnedReflect;
			}
			return new BatchReflect(this, returned, returnedReflects);
		}

		@Override
		public BatchReflect call(Object... arguments) throws ReflectException {
			Object[] returned = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				Reflect returnedReflect = reflect.call(arguments);
				returned[i] = returnedReflect.off();
				returnedReflects[i] = returnedReflect;
			}
			return new BatchReflect(this, returned, returnedReflects);
		}

		@Override
		public BatchReflect callBy(Object receiver, Object... arguments) throws ReflectException {
			Object[] returned = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				Reflect returnedReflect = reflect.callBy(receiver, arguments);
				returned[i] = returnedReflect.off();
				returnedReflects[i] = returnedReflect;
			}
			return new BatchReflect(this, returned, returnedReflects);
		}

		@Override
		public BatchReflect callBy(Object receiver) throws ReflectException {
			Object[] returned = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				Reflect returnedReflect = reflect.callBy(receiver);
				returned[i] = returnedReflect.off();
				returnedReflects[i] = returnedReflect;
			}
			return new BatchReflect(this, returned, returnedReflects);
		}

		@Override
		public Iterator<MethodReflect> iterator() {
			return Arrays.asList(reflects).iterator();
		}

		@Override
		public BatchMethodReflect release() {
			super.release();
			return this;
		}

		@Override
		public List<Annotation> getAnnotations() {
			List<Annotation> annotations = new ArrayList<Annotation>();
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				annotations.addAll(reflect.getAnnotations());
			}
			return annotations;
		}

		@Override
		public <T extends Annotation> List<T> getAnnotations(Class<T> annotationClass) {
			List<T> annotations = new ArrayList<T>();
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				annotations.addAll(reflect.getAnnotations(annotationClass));
			}
			return annotations;
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Annotation> getDeclaredAnnotations() {
			List<Annotation> annotations = new ArrayList<Annotation>();
			for (int i = 0; i < reflects.length; i++) {
				MethodReflect reflect = reflects[i];
				annotations.addAll(reflect.getDeclaredAnnotations());
			}
			return annotations;
		}
	}

	public static class ConstructorReflect extends MemberReflect<Constructor<?>> {
		public ConstructorReflect(Reflect from, Constructor<?> member) {
			super(from, member);
		}

		private Object[] arguments = EMPTY_OBJECT_ARRAY;

		public ConstructorReflect(Reflect from, Constructor<?> member, Object... arguments) {
			super(from, member);
			if (arguments != null) {
				this.arguments = arguments;
			}
		}

		public Reflect create() throws ReflectException {
			return create(arguments);
		}

		private Reflect create(Reflect from, Object... arguments) throws ReflectException {
			try {
				return on(from, accessible(super.off()).newInstance(arguments));
			} catch (Exception e) {
				throw new ReflectException(e);
			}
		}

		public Reflect create(Object... arguments) throws ReflectException {
			try {
				return on(this, accessible(super.off()).newInstance(arguments));
			} catch (Exception e) {
				throw new ReflectException(e);
			}
		}

		@Override
		public boolean isConstructor() {
			return true;
		}

		@Override
		public Constructor<?> off() {
			return super.off();
		}

		@Override
		public ConstructorReflect release() {
			super.release();
			return this;
		}
	}

	public static class ObjectReflect extends Reflect {
		private Object object;

		public ObjectReflect(Reflect from, Object object) {
			super(from);
			this.object = object;
			if (object != null) {
				super.type = object.getClass();
			} else {
				super.type = NULL.class;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T off() {
			return (T) object;
		}
	}

	public static class MapReflect extends Reflect {
		private Map<String, Object> map;

		public MapReflect(Reflect from, Map<String, Object> map) {
			super(from);
			this.map = map;
			super.type = map.getClass();
		}

		@Override
		public Reflect call(String name) throws ReflectException {
			if (name == null)
				return new NullReflect(this);
			if (name.startsWith("get")) {
				return Reflect.on(map.get(unTitle(name.substring(3))));
			} else if (name.startsWith("is")) {
				return Reflect.on(map.get(unTitle(name.substring(2))));
			}
			return super.call(name);
		}

		@Override
		public Reflect call(String name, Object... arguments) throws ReflectException {
			if (name == null)
				return new NullReflect(this);
			if (name.startsWith("set")) {
				if (arguments != null && arguments.length == 1) {
					map.put(unTitle(name.substring(2)), arguments[0]);
					return new NullReflect(this);
				}
			}
			return super.call(name, arguments);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> off() {
			return map;
		}

	}

	public static class NullReflect extends Reflect {
		private NULL _null = new NULL();

		public NullReflect(Reflect from) {
			super(from);
		}

		@Override
		public Reflect back() {
			return from == null ? this : from;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T off() {
			return (T) _null;
		}
	}

	public static class BatchReflect extends Reflect implements Iterable<Reflect> {
		private final Object[] origin;
		private final Reflect[] reflects;

		public BatchReflect(Reflect from, Object[] origin, Reflect... reflects) {
			super(from);
			this.origin = origin;
			this.reflects = reflects;
		}

		@Override
		public boolean isClass() {
			return false;
		}

		@Override
		public boolean isField() {
			return false;
		}

		@Override
		public boolean isMethod() {
			return false;
		}

		@Override
		public boolean isConstructor() {
			return false;
		}

		@Override
		public boolean isMap() {
			return false;
		}

		@Override
		public boolean isNull() {
			return false;
		}

		@Override
		public BatchMethodReflect method(String name) throws ReflectException {
			MethodReflect[] reflects = new MethodReflect[origin.length];
			for (int i = 0; i < origin.length; i++) {
				MethodReflect reflect = Reflect.on(origin[i]).method(name);
				reflects[i] = reflect;
			}
			return new BatchMethodReflect(this, reflects);
		}

		@Override
		public BatchMethodReflect method(String name, Class<?>... parameterTypes) throws ReflectException {
			MethodReflect[] reflects = new MethodReflect[origin.length];
			for (int i = 0; i < origin.length; i++) {
				MethodReflect reflect = Reflect.on(origin[i]).method(name, parameterTypes);
				reflects[i] = reflect;
			}
			return new BatchMethodReflect(this, reflects);
		}

		@Override
		public BatchMethodReflect method(String name, Object... arguments) throws ReflectException {
			MethodReflect[] reflects = new MethodReflect[origin.length];
			for (int i = 0; i < origin.length; i++) {
				MethodReflect reflect = Reflect.on(origin[i]).method(name, arguments);
				reflects[i] = reflect;
			}
			return new BatchMethodReflect(this, reflects, arguments);
		}

		@Override
		public FieldReflect field(String name) throws ReflectException {
			return super.field(name);
		}

		@Override
		public Map<String, Reflect> fieldValues() {
			return super.fieldValues();
		}

		@Override
		public Reflect callBy(Object receiver, String name, Object... arguments) throws ReflectException {
			Object[] returnds = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				Reflect reflect = reflects[i].callBy(receiver, name, arguments);
				returnedReflects[i] = reflect;
				returnds[i] = reflect.off();
			}
			return new BatchReflect(this, returnds, returnedReflects);
		}

		@Override
		public BatchReflect call(String name) throws ReflectException {
			return this.call(name, EMPTY_OBJECT_ARRAY);
		}

		@Override
		public BatchReflect call(String name, Object... arguments) throws ReflectException {
			Object[] returnds = new Object[reflects.length];
			Reflect[] returnedReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				Reflect reflect = reflects[i].call(name, arguments);
				returnedReflects[i] = reflect;
				returnds[i] = reflect.off();
			}
			return new BatchReflect(this, returnds, returnedReflects);
		}

		@Override
		public BatchReflect create() throws ReflectException {
			return this.create(EMPTY_OBJECT_ARRAY);
		}

		@Override
		public BatchReflect create(Object... arguments) throws ReflectException {
			Object[] createds = new Object[reflects.length];
			Reflect[] createdReflects = new Reflect[reflects.length];
			for (int i = 0; i < reflects.length; i++) {
				Reflect reflect = reflects[i].create(arguments);
				createdReflects[i] = reflect;
				createds[i] = reflect.off();
			}
			return new BatchReflect(this, createds, createdReflects);
		}

		@Override
		public String toString() {
			return new StringBuilder().append(getClass()).append('<').append(Arrays.toString(origin)).append('>')
					.toString();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] off() {
			return origin;
		}

		@Override
		public Iterator<Reflect> iterator() {
			return Arrays.asList(reflects).iterator();
		}
	}

	public static class EnumReflect<T extends Enum<T>> extends Reflect {
		private final Class<T> enumType;
		Field valuesField = null;

		public EnumReflect(Class<T> enumType) {
			super(nullReflect());
			this.enumType = enumType;
			if (!enumType.isEnum()) {
				throw new ReflectException(enumType.getName() + " is not enum type");
			}
			for (Field field : enumType.getDeclaredFields()) {
				if (field.getName().contains("$VALUES")) {
					valuesField = field;
					break;
				}
			}
			accessible(valuesField);
			try {
				breakFinal(valuesField);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		public T[] values() {
			try {
				T[] values = (T[]) valuesField.get(enumType);
				return values;
			} catch (Exception e) {
				return (T[]) new Object[0];
			}
		}

		/**
		 * 是否包含指定名称的枚举
		 * 
		 * @param name
		 * @return
		 * @throws NullPointerException
		 *             if name is null
		 */
		public boolean contains(String name) {
			try {
				return Enum.valueOf(enumType, name) != null;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		public T valueOf(String name) {
			if (name == null || name.length() < 1) {
				return null;
			}
			return Enum.valueOf(enumType, name);
		}

		/**
		 * 添加枚举
		 * 
		 * @param name
		 * @param arguments
		 * @return
		 */
		@SuppressWarnings({ "unchecked" })
		public T add(String name, Object... arguments) {
			try {
				return valueOf(name);
			} catch (IllegalArgumentException ex) {
			}

			try {
				T[] values = (T[]) valuesField.get(enumType);

				T newEnum = makeEnum(arguments, name, values.length);

				T[] newValues = (T[]) Array.newInstance(enumType, values.length + 1);
				newValues[values.length] = newEnum;

				System.arraycopy(values, 0, newValues, 0, values.length);

				FieldAccessor fa = reflectionFactory.newFieldAccessor(valuesField, false);
				fa.set(null, newValues);

				// enumConstantDirectory
				Field directField = similarField(Class.class, "enumConstantDirectory");
				if (directField != null) {
					breakFinal(directField);
					FieldAccessor directFA = reflectionFactory.newFieldAccessor(directField, false);
					Map<String, T> direct = (Map<String, T>) directFA.get(enumType);
					direct.put(name, newEnum);
				}
				// enumConstants
				Field constantsField = similarField(Class.class, "enumConstants");
				if (constantsField != null) {
					breakFinal(constantsField);
					FieldAccessor constantsFA = reflectionFactory.newFieldAccessor(constantsField, false);
					constantsFA.set(enumType, newValues);
				}

				return newEnum;
			} catch (ReflectException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new ReflectException("add enum failed", ex);
			}
		}

		private T makeEnum(Object[] arguments, String name, int odinal) throws Exception {
			Constructor<T> constr = getConstructor(enumType, arguments);
			if (constr == null) {
				throw new ReflectException("constructor not found:" + enumType.getName() + "."
						+ enumType.getSimpleName() + "(" + typeName(arguments) + ")");
			}
			constr.setAccessible(true);
			Object[] args = new Object[arguments.length + 2];
			args[0] = name;
			args[1] = odinal;
			System.arraycopy(arguments, 0, args, 2, arguments.length);
			ConstructorAccessor accessor = reflectionFactory.newConstructorAccessor(constr);
			return enumType.cast(accessor.newInstance(args));
		}

		private static String typeName(Object[] arguments) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i] == null) {
					sb.append("null");
				} else {
					sb.append(arguments[i].getClass().getName()).append(',');
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		}

		private static Field similarField(Class<?> on, String name) {
			for (Field field : on.getDeclaredFields()) {
				if (field.getName().contains(name)) {
					return field;
				}
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		private static <T> Constructor<T> getConstructor(Class<T> type, Object... arguments) {
			Constructor<?>[] constrs = type.getDeclaredConstructors();
			if (constrs.length == 1) {
				return (Constructor<T>) constrs[0];
			}
			Class<?>[] types = types(arguments);
			Class<?>[] realTypes = new Class<?>[types.length + 2];
			realTypes[0] = String.class;
			realTypes[1] = int.class;
			System.arraycopy(types, 0, realTypes, 2, types.length);
			for (int i = 0; i < constrs.length; i++) {
				if (match(constrs[i].getParameterTypes(), realTypes)) {
					return (Constructor<T>) constrs[i];
				}
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<T> off() {
			return enumType;
		}

	}

	private static class NULL {
	}

	private static final class MethodKey {
		private String name;
		private Class<?>[] parameterTypes;
		private int hash;

		private MethodKey(String name, Class<?>[] parameterTypes) {
			super();
			this.name = name;
			this.parameterTypes = parameterTypes;
			this.hash = hash0();
		}

		@Override
		public int hashCode() {
			return hash;
		}

		private int hash0() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + Arrays.hashCode(parameterTypes);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodKey other = (MethodKey) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (!Arrays.equals(parameterTypes, other.parameterTypes))
				return false;
			return true;
		}
	}
}
