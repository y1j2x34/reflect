package com.vgerbot.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Reflect {
	private static final URL[] 	emptyURLArray = new URL[]{};
	private static final Map<String,Class<?>> PRIMTYPEMAPPING = new HashMap<String,Class<?>>();
	static{
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
	public static boolean isInt(Class<?> type){
        return int.class == type || Integer.class == type;
    }

    public static boolean isFloat(Class<?> type){
        return float.class == type || Float.class == type;
    }

    public static boolean isDouble(Class<?> type){
        return double.class == type || Double.class == type;
    }

    public static boolean isShort(Class<?> type){
        return short.class == type || Short.class == type;
    }

    public static boolean isByte(Class<?> type){
        return byte.class == type || Byte.class == type;
    }

    public static boolean isChar(Class<?> type){
        return char.class == type || Character.class == type;
    }

    public static boolean isLong(Class<?> type){
        return long.class == type || Long.class == type;
    }

    public static boolean isBoolean(Class<?> type){
        return boolean.class == type || Boolean.class == type;
    }

    public static boolean isText(Class<?> type){
        return CharSequence.class.isAssignableFrom(type);
    }
    
    private static final String unTitle(String str){
		if(str == null || str.length() < 1) return null;
		char[] chs = str.toCharArray();
		chs[0] = Character.toLowerCase(chs[0]);
		return new String(chs);
	}
	/**
	 * 获取包装类
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param type
	 * @return
	 */
	public static Class<?> wrapper(Class<?> type){
		if(type == null){
			return null;
		}else if(type.isPrimitive()){
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
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param wrapper
	 * @return
	 */
	public static Class<?> primitiveType(Class<?> wrapper){
		if(wrapper == null){
			return null;
		}else if(!wrapper.isPrimitive()){
			if(Integer.class == wrapper){
				return int.class;
			}else if(Short.class == wrapper){
				return short.class;
			}else if(Byte.class == wrapper){
				return byte.class;
			}else if(Float.class == wrapper){
				return float.class;
			}else if(Character.class == wrapper){
				return char.class;
			}else if(Long.class == wrapper){
				return long.class;
			}else if(Double.class == wrapper){
				return double.class;
			}else if(Boolean.class == wrapper){
				return boolean.class;
			}else if(Void.class == wrapper){
				return void.class;
			}
		}
		return wrapper;
	}
	
	/**
	 * 将访问受限的对象转为访问不受限的对象
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param accessible 访问受限的对象
	 * @return 如果accessible不为空，则为访问不受限的对象
	 */
	public static <T extends AccessibleObject> T accessible(T accessible){
		if(accessible == null) return null;
		if(accessible instanceof Member){
			Member m = (Member)accessible;
			if(Modifier.isPublic(m.getModifiers()) && Modifier.isPublic(((Member) accessible).getDeclaringClass().getModifiers())){
				return accessible;
			}
		}
		if(!accessible.isAccessible()){
			accessible.setAccessible(true);
		}
		return accessible;
	}
	//----------------------------------------------------------------
	//内部工具方法
	//----------------------------------------------------------------
	private static Reflect nullReflect(){
		return new NullReflect(null);
	}
	private static ClassLoader getDefaultClassLoader(){
		ClassLoader cl = null;
		try{
			cl = Thread.currentThread().getContextClassLoader();
		}catch(Throwable t){
			//cannot access thread context classLoader - falling back
		}
		if(cl == null){
			cl = Reflect.class.getClassLoader();
			if(cl == null){
				cl = ClassLoader.getSystemClassLoader();
			}
		}
		return cl;
	}
	@SuppressWarnings("unchecked")
	private static <T> Class<T> forName(String name,ClassLoader loader) throws ReflectException{
		Class<?> pmtype = PRIMTYPEMAPPING.get(name);
		if(pmtype != null){
			return (Class<T>) pmtype;
		}
		//"java.lang.String[]" style arrays
		if(name.endsWith("[]")){
			Class<?> emlClass = forName(name.substring(0, name.length()-2));
			return (Class<T>) Array.newInstance(emlClass, 0).getClass();
		}
		//"[I" or "[Ljava.lang.String" style arrays
		if(name.startsWith("[L") || name.startsWith("[I")){
			Class<?> elmClass = forName(name.substring(2),loader);
			return (Class<T>) Array.newInstance(elmClass, 0).getClass();
		}
		//"[[[I" or "[[[[Ljava.lang.String" style arrays
		if(name.charAt(0) == '['){
			Class<?> elmClass = forName(name.substring(1),loader);
			return (Class<T>) Array.newInstance(elmClass, 0).getClass();
		}
		if(loader == null){
			loader = getDefaultClassLoader();
		}
		try{
			return (Class<T>) (loader != null?loader.loadClass(name):Class.forName(name));
		}catch(ClassNotFoundException e){
			int lastDotIndex = name.lastIndexOf('.');
			if(lastDotIndex != -1){
				String clsName = name.substring(0, lastDotIndex) + "$" + name.substring(lastDotIndex + 1);
				try{
					return (Class<T>) (loader != null?loader.loadClass(clsName):Class.forName(clsName));
				}catch(ClassNotFoundException ex){
				}
			}
			throw new ReflectException(e);
		}
	}
	private static <T> Class<T> forName(String name) throws ReflectException{
		return forName(name,null);
	}
	private static Class<?>[] types(Object[] values){
		if(values == null) return EMPTY_CLASS_ARRAY;
		Class<?>[] types = new Class[values.length];
		for(int i=0;i<values.length;i++){
			types[i] = values[i]==null?NULL.class:values[i].getClass();
		}
		return types;
	}
	 /**
     * 给定方法名和参数，匹配一个最接近的方法
     */
    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();

        //对于公有方法:
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }

        //对于私有方法：
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }

            type = type.getSuperclass();
        }
        while (type != null);

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + ".");
    }
	/**
	 * 方法参数类型匹配
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param declaredTypes
	 * @param actualTypes
	 * @return
	 */
	private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
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
     * 再次确认方法签名与实际是否匹配，
     * 将基本类型转换成对应的对象类型，
     * 如int转换成Integer
     */
    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
	
	public static class ClassReflect extends Reflect{
		public ClassReflect(Reflect from,Class<?> type) {
			super(from);
			super.type = type;
		}
		@Override
		public <T> T unwrap() {
			return null;
		}
		@Override
		public boolean isClass() {
			return true;
		}
		@Override
		public String toString() {
			return String.valueOf(super.type);
		}
		public boolean isAbstract(){
			return Modifier.isAbstract(super.type.getModifiers());
		}
		public boolean isInterface(){
			return Modifier.isInterface(super.type.getModifiers());
		}
		public boolean isStrict(){
			return Modifier.isStrict(super.type.getModifiers());
		}
		public boolean isFinal(){
			return Modifier.isFinal(super.type.getModifiers());
		}
		public Reflect superclass(){
			return Reflect.on(super.type.getSuperclass());
		}
		public boolean isParentTypeOf(Class<?> type){
			return super.type.isAssignableFrom(type);
		}
		public boolean isParentTypeOf(Reflect r){
			if(r == null) return false;
			return isParentTypeOf(r.type());
		}
		public boolean isInt(){
			return isInt(super.type);
		}
		public boolean isFloat(){
			return isFloat(super.type);
		}
		public boolean isShort(){
			return isShort(super.type);
		}
		public boolean isByte(){
			return isByte(super.type);
		}
		public boolean isBoolean(){
			return isBoolean(super.type);
		}
		public boolean isLong(){
			return isLong(super.type);
		}
		public boolean isDouble(){
			return isDouble(super.type);
		}
		public boolean isChar(){
			return isChar(super.type);
		}
		public boolean isText(){
			return isText(super.type);
		}
		public void simpleConvert(Object obj){
			
		}
	}
	public static class MemberReflect<M extends Member> extends Reflect{
		private M member;
		private Object object;
		public MemberReflect(Reflect from,M member) {
			this(from,member,null);
		}
		public MemberReflect(Reflect from,M member,Object object) {
			super(from);
			this.member = member;
			super.type = member.getClass();
			this.object = object;
		}
		@SuppressWarnings("unchecked")
		@Override
		public <T> T unwrap() {
			return (T)member;
		}
		public boolean isStatic(){
			return Modifier.isStatic(member.getModifiers());
		}
		public boolean isPublic(){
			return Modifier.isPublic(member.getModifiers());
		}
		public boolean isPrivate(){
			return Modifier.isPrivate(member.getModifiers());
		}
		public boolean isProtected(){
			return Modifier.isProtected(member.getModifiers());
		}
		public boolean isFinal(){
			return Modifier.isFinal(member.getModifiers());
		}
		/**
		 * 取得定义该成员的类
		 * @author y1j2x34
		 * @version 1.0
		 * @date 2015-3-31
		 * @return
		 */
		public ClassReflect declaring(){
			return new ClassReflect(this,member.getDeclaringClass());
		}
	}
	public static class FieldReflect extends MemberReflect<Field>{

		public FieldReflect(Reflect from,Field member, Object object) {
			super(from,member, object);
		}

		public FieldReflect(Reflect from,Field member) {
			super(from,member);
		}
		public void set(Object value) throws ReflectException{
			try{
				accessible(super.member).set(super.object, value);
			}catch(Exception e){
				throw new ReflectException(e);
			}
		}
		public Reflect get(Object object) throws ReflectException{
			return on(this,getValue(object));
		}
		public Reflect get() throws ReflectException{
			return on(this,getValue(super.object));
		}
		@SuppressWarnings("unchecked")
		public <T> T getValue(Object object) throws ReflectException{
			try{
				return (T)(accessible(super.member).get(object));
			}catch(Exception e){
				throw new ReflectException(e);
			}
		}
		public <T> T getValue() throws ReflectException{
			return getValue(super.object);
		}
		@Override
		public boolean isField() {
			return true;
		}
		public boolean isTransient(){
			return Modifier.isTransient(super.member.getModifiers());
		}
		public boolean isVolatile(){
			return Modifier.isVolatile(super.member.getModifiers());
		}
	}
	public static class MethodReflect extends MemberReflect<Method>{

		public MethodReflect(Reflect from,Method member) {
			super(from,member);
		}

		public MethodReflect(Reflect from,Method member, Object object) {
			super(from,member, object);
		}
		private Object[] arguments = EMPTY_OBJECT_ARRAY;
		public MethodReflect(Reflect from,Method member, Object object,Object[] arguments) {
			super(from,member, object);
			if(arguments != null){
				this.arguments = arguments;
			}
		}
		public Reflect call() throws ReflectException{
			return callBy(super.object,arguments);
		}
		public Reflect call(Object...arguments) throws ReflectException{
			return callBy(super.object,arguments);
		}
		private Reflect callBy(Reflect from,Object receiver,Object...arguments) throws ReflectException{
			try{
				return on(from,accessible(super.member).invoke(receiver, arguments));
			}catch(Exception e){
				throw new ReflectException(e);
			}
		}
		public Reflect callBy(Object receiver,Object...arguments) throws ReflectException{
			return callBy(this, receiver, arguments);
		}
		
		private Reflect call(Reflect from,Object...arguments) throws ReflectException{
			return callBy(from, super.object, arguments);
		}
		public int parameterSize(){
			return arguments==null?0:arguments.length;
		}
		public ClassReflect parameterTypeAt(int index){
			Class<?>[] types = super.member.getParameterTypes();
			return types.length==0?null:new ClassReflect(this,types[index]);
		}
		@Override
		public boolean isMethod() {
			return true;
		}
		public boolean isNative(){
			return Modifier.isNative(super.member.getModifiers());
		}
		public boolean isSynchronized(){
			return Modifier.isSynchronized(super.member.getModifiers());
		}
		public boolean isAbstract(){
			return Modifier.isAbstract(super.member.getModifiers());
		}
		public boolean isStrict(){
			return Modifier.isStrict(super.member.getModifiers());
		}
	}
	public static class ConstructorReflect extends MemberReflect<Constructor<?>>{
		public ConstructorReflect(Reflect from,Constructor<?> member) {
			super(from,member);
		}
		private Object[] arguments = EMPTY_OBJECT_ARRAY;
		public ConstructorReflect(Reflect from,Constructor<?> member,Object...arguments) {
			super(from,member);
			if(arguments != null){
				this.arguments = arguments;
			}
		}
		
		public Reflect create() throws ReflectException{
			return create(arguments);
		}
		
		private Reflect create(Reflect from,Object...arguments) throws ReflectException{
			try{
				return on(from,accessible(super.member).newInstance(arguments));
			}catch(Exception e){
				throw new ReflectException(e);
			}
		}
		public Reflect create(Object...arguments) throws ReflectException{
			try{
				return on(this,accessible(super.member).newInstance(arguments));
			}catch(Exception e){
				throw new ReflectException(e);
			}
		}
		@Override
		public boolean isConstructor() {
			return true;
		}
	}
	public static class ObjectReflect extends Reflect{
		private Object object;
		public ObjectReflect(Reflect from,Object object) {
			super(from);
			this.object = object;
			super.type = object.getClass();
		}
		@SuppressWarnings("unchecked")
		@Override
		public <T> T unwrap() {
			return (T)object;
		}
	}
	
	public static class MapReflect extends Reflect{
		private Map<String,Object> map;
		public MapReflect(Reflect from,Map<String,Object> map) {
			super(from);
			this.map = map;
		}
		
		@Override
		public Reflect call(String name) throws ReflectException {
			if(name == null) return new NullReflect(this);
			if(name.startsWith("get")){
				return Reflect.on(map.get(unTitle(name.substring(3))));
			}else if(name.startsWith("is")){
				return Reflect.on(map.get(unTitle(name.substring(2))));
			}
			return super.call(name);
		}
		@Override
		public Reflect call(String name, Object... arguments)
				throws ReflectException {
			if(name == null) return new NullReflect(this);
			if(name.startsWith("set")){
				if(arguments != null && arguments.length == 1){
					map.put(unTitle(name.substring(2)), arguments[0]);
					return new NullReflect(this);
				}
			}
			return super.call(name, arguments);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> T unwrap() {
			return (T)map;
		}
		
	}
	public static class NullReflect extends Reflect{
		private NULL _null = new NULL();
		public NullReflect(Reflect from) {
			super(from);
		}
		@Override
		public Reflect back() {
			return from==null?this:from;
		}
		@SuppressWarnings("unchecked")
		@Override
		public <T> T unwrap() {
			return (T) _null;
		}
	}
	
	private static class NULL{}
	
	public static final ClassReflect find(List<String> imports,String simpleName, URL[] urls) throws ReflectException{
		if(simpleName == null){
			return Reflect.on(Object.class);
		}
		
		if(simpleName.length() < 8 && simpleName.length() > 2){
			Class<?> ft = PRIMTYPEMAPPING.get(simpleName);
			if(ft != null){
				return Reflect.on(ft);
			}
		}
		if(urls == null){
			urls = emptyURLArray;
		}
		URLClassLoader uloader = new URLClassLoader(urls);
		
		Class<?> type = null;
		if(!( imports == null || imports.isEmpty())){
			StringBuilder cname = new StringBuilder();
			Iterator<String> it = imports.iterator();
			while(type == null && it.hasNext()){
				String imp=it.next();
				if(imp.endsWith("*")){
					cname.append(imp.substring(0, imp.length()-1)).append(simpleName);
				}else if(imp.endsWith(simpleName)){
					cname.setLength(0);
					cname.append(imp);
				}else{
					cname.append(imp).append('.').append(simpleName);
				}
				try {
					type = uloader.loadClass(cname.toString());
				} catch (ClassNotFoundException e) {}
				cname.setLength(0);
			}
		}
		if(type == null){
			try {
				type = uloader.loadClass(simpleName);
			} catch (ClassNotFoundException e) {}
		}
		if(type == null){
			try {
				type = Class.forName(simpleName);
			} catch (ClassNotFoundException e) {
			}
		}
		if(type == null){
			throw new ReflectException("Class not found:"+simpleName);
		}
		return Reflect.on(type);
	}
	
	private static ObjectReflect on(Reflect from,Object object) throws ReflectException{
		if(object == null){
			throw new ReflectException("Illegal argument:null");
		}
		return new ObjectReflect(from,object);
	}
	public static ObjectReflect on(Object object) throws ReflectException{
		return on(nullReflect(),object);
	}
	public static ClassReflect on(String name) throws ReflectException{
		if(name == null || name.length() < 1)
			throw new ReflectException("no characters!");
		return on(forName(name));
	}
	public static ClassReflect on(String name,ClassLoader loader) throws ReflectException{
		if(name == null || name.length() < 1)
			throw new ReflectException("no characters!");
		return on(forName(name,loader));
	}
	public static MapReflect on(Map<String,Object> map) throws ReflectException{
		return new MapReflect(nullReflect(), map==null?new HashMap<String,Object>(0):map);
	}
	public static ClassReflect on(Class<?> clazz) throws ReflectException{
		if(clazz == null){
			throw new ReflectException("Illegal argument:null");
		}
		return new ClassReflect(nullReflect(),clazz);
	}
	public static FieldReflect on(Field field) throws ReflectException{
		return on(field,null);
	}
	public static FieldReflect on(Field field,Object object) throws ReflectException{
		if(field == null){
			throw new ReflectException("field is null");
		}
		return new FieldReflect(nullReflect(),field,object);
	}
	public static MethodReflect on(Method method,Object object){
		if(method == null){
			throw new ReflectException("method is null");
		}
		return new MethodReflect(nullReflect(),method,object);
	}
	public static MethodReflect on(Method method){
		return on(method,null);
	}
	public static ConstructorReflect on(Constructor<?> constructor,Object...arguments){
		if(constructor == null){
			throw new ReflectException("constructor is null");
		}
		return new ConstructorReflect(nullReflect(),constructor,arguments);
	}
	public static ConstructorReflect on(Constructor<?> constructor){
		return on(constructor,EMPTY_OBJECT_ARRAY);
	}
	
	protected Class<?> type;
	
	protected Reflect from;
	
	public Reflect(Reflect from) {
		this.from = from;
	}
	
	public boolean isClass(){
		return false;
	}
	public boolean isField(){
		return false;
	}
	public boolean isMethod(){
		return false;
	}
	public boolean isConstructor(){
		return false;
	}
	public boolean isMap(){
		return false;
	}
	public boolean isNull(){
		return false;
	}
	public ObjectReflect asObject(){
		return new ObjectReflect(from, unwrap());
	}
	/**
	 * 获取被包装的对象
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public abstract <T> T unwrap();
	
	public final Class<?> type(){
		return type;
	}
	/**
	 * @author y1j2x34
	 * @date 2015年3月31日 
	 * @param name	方法名
	 * @return 
	 * @throws ReflectException 方法找不到时抛出该异常
	 */
	public MethodReflect method(String name) throws ReflectException{
		return method(name,new Class[0]);
	}
	/**
	 * @author y1j2x34
	 * @date 2015年3月31日 
	 * @param name	方法名
	 * @param parameterTypes	参数类型数组
	 * @return 
	 * @throws ReflectException 方法找不到时抛出该异常
	 */
	public MethodReflect method(String name,Class<?>...parameterTypes) throws ReflectException{
		Method method = method0(name,parameterTypes);
		if(method== null) return null;
		return new MethodReflect(this,method,unwrap());
	}
	/**
	 * 获取字段
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @param name
	 * @return
	 * @throws ReflectException
	 */
	public FieldReflect field(String name) throws ReflectException{
		return new FieldReflect(this,field0(name),unwrap());
	}
	private Field field0(String name) throws ReflectException{
		Class<?> type = type();
		try{
			return type.getField(name);
		}catch(NoSuchFieldException e){
			while(type != null){
				try{
					return accessible(type.getDeclaredField(name));
				}catch(NoSuchFieldException ignore){}
				type = type.getSuperclass();
			}
			throw new ReflectException(e);
		}
	}
	/**
	 * 所有字段
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public Map<String,FieldReflect> fields(){
		return fields0();
	}
	/**
	 * 所有字段对应的值
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-30
	 * @return
	 */
	public Map<String,Reflect> fieldValues(){
		Map<String,FieldReflect> fields = fields0();
		Map<String,Reflect> fieldValues = new HashMap<String, Reflect>(fields.size());
		for(String name:fields.keySet()){
			fieldValues.put(name, fields.get(name).get());
		}
		return fieldValues;
	}
	private Map<String,FieldReflect> fields0(){
		Map<String,FieldReflect> fields = new HashMap<String, FieldReflect>();
		Class<?> type = type();
		while(type != null){
			Field[] fs = type.getDeclaredFields();
			for(int i=0;i<fs.length;i++){
				//如果是ClassReflect，则只取静态字段
				//如果不是ClassReflect，则不取静态字段
				if(!isClass() ^ Modifier.isStatic(fs[i].getModifiers())){
					String name = fs[i].getName();
					if(!fields.containsKey(name)){
						fields.put(name, field(name));
					}
				}
			}
			type = type.getSuperclass();
		}
		return fields;
	}
	private Method method0(String name,Class<?>...parameterTypes) throws ReflectException{
		Class<?> type = type();
		try{
			return type.getMethod(name, parameterTypes);
		}catch(NoSuchMethodException e){
			while(type != null){
				try{
					return accessible(type.getDeclaredMethod(name, parameterTypes));
				}catch(NoSuchMethodException ignore){}
				type = type.getSuperclass();
			}
			throw new ReflectException(e);
		}
	}
	/**
	 * 指定对象来调用某个方法
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param receiver
	 * @param name
	 * @param arguments
	 * @return
	 * @throws ReflectException
	 */
	public Reflect callBy(Object receiver,String name,Object...arguments) throws ReflectException{
		MethodReflect mr = method(name);
		if(mr != null){
			return mr.callBy(this,receiver,arguments);
		}else{
			return nullReflect();
		}
	}
	/**
	 * 调用无参方法
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param name
	 * @return
	 * @throws ReflectException
	 */
	public Reflect call(String name) throws ReflectException{
		 MethodReflect mr = method(name);
		 if(mr != null){
			 return mr.call(this);
		 }else{
			 return nullReflect();
		 }
	}
	/**
	 * 调用对象上的方法
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param name
	 * @param arguments
	 * @return
	 * @throws ReflectException
	 */
	public Reflect call(String name,Object...arguments) throws ReflectException{
		if(name == null || name.length() < 1){
			throw new ReflectException("Illegal method name:"+name);
		}
		Class<?>[] types = types(arguments);
		try{
			Method method = type().getMethod(name, types);
			return new MethodReflect(this,method, unwrap()).call(this,arguments);
		}catch(NoSuchMethodException e){
			try{
				Method method = similarMethod(name, types);
				return new MethodReflect(this,method, unwrap()).call(this,arguments);
			}catch(NoSuchMethodException e1){
				throw new ReflectException(e1);
			}
		}
	}
	/**
	 * 创建实例（无参数）
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @return
	 * @throws ReflectException
	 */
	public Reflect create() throws ReflectException{
		return create(EMPTY_OBJECT_ARRAY);
	}
	/**
	 * 创建实例（有参数）
	 * @author y1j2x34
	 * @version 1.0
	 * @date 2015-3-31
	 * @param arguments 构造器参数和查找对应构造器的依据
	 * @return
	 * @throws ReflectException
	 */
	public Reflect create(Object...arguments) throws ReflectException{
		Class<?>[] types = types(arguments);
		try{
			Constructor<?> constructor = type().getDeclaredConstructor(types);
			return new ConstructorReflect(this,constructor).create(this,arguments);
		}catch(NoSuchMethodException e){
			for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return new ConstructorReflect(this,constructor).create(this,arguments);
                }
            }
			throw new ReflectException(e);
		}
	}
	/**
	 * 获得链式调用的上一节点
	 * @author y1j2x34
	 * @date 2015年3月31日 
	 * @return
	 */
	public Reflect back(){
		return from;
	}
	/**
	 * 判断是否等价
	 * @author y1j2x34
	 * @date 2015年3月31日
	 * @param other
	 * @return
	 */
	public boolean is(Reflect other){
		return other != null && this == other || other.unwrap() == this.unwrap();
	}
	
	@Override
	public String toString() {
		return String.valueOf(unwrap());
	}
}
