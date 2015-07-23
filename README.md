# reflect
Java反射工具

##准备一个Person类
```
class Person{
	private String name;
	private int age = 0;
	Person(){
		name = "default name";
	}
	Person(String name,int age){
		this.name = name;
		this.age = age;
	}
	Person(String name){
		this.name = name;
	}
	String selfIntroduction(){
		return "Hello!My name is '"+name+"'";
	}
	void say(String something){
		System.out.println(something);
	}
	boolean wasDead(){
		return age > 200; 
	}
}
```
***
##基本使用方法
### 1. 创建一个Reflect
```
//包装类
ClassReflect cr = Reflect.on(Person.class);
ClassReflect cr = Reflect.on("class name here");
ClassReflect cr = Reflect.on("class name here ",ClassLoader.getSystemClassLoader());//指定类加载器
//包装对象
ObjectReflect or = Reflect.on(new Person());
//包装字段
Field f = Person.class.getDeclaredField("name");
FieldReflect fr = Reflect.on(f);
FieldReflect fr = Reflect.on(f,new Person("mario"));//绑定字段默认作用对象
//包装方法
Method m = Person.class.getDeclaredMethod("say",String.class);
MethodReflect mr = Reflect.on(m);
MethodReflect mr = Reflect.on(m,new Person());//绑定方法默认作用对象
MethodReflect mr = Reflect.on(m,new Person(),new Object[]{"hello world"});//绑定方法默认作用对象和参数
//包装构造器
Constructor<?> constr = Person.class.getDeclaredConstructor();
ConstructorReflect cr = Reflect.on(constr);
ConstructorReflect cr = Reflect.on(constr,"john",26);//绑定默认构造参数
//解包
调用unwrap()方法即可
```
### 2. 创建实例

```
Reflect.on(Person.class).create();//Reflect.on(new Person());
Reflect.on(Person.class).create("mario");//Reflect.on(new Person("mario"));
Reflect.on(Person.class).create("kankan",26);//Reflect.on(new Person("kankan",26));
```
***
### 3. 查找方法
```
Reflect op = Reflect.on(Person.class).create();
//精确查找
op.method("say",String.class);
op.method("selfIntroduction");
//根据提供的参数类型查找
op.method("say","你好");
```
### 4. 查找字段
```
Reflect op = Reflect.on(Person.class).create("data");
//所有字段
Map<String,FieldReflect> allFields = op.fields();
//某个字段
FieldReflect nameField = op.field("name");
//字段值
op.field("name").get().unwrap();//returns "data"
//所有字段值
Map<String,Reflect> values = op.fieldValues();
```
***
##链式调用
```
Reflect.on(Person.class).create().method("say",String.class).call((Object)"你好世界");//output: 你好世界
```
***
##参数绑定
### 1. 创建时绑定
```
Method sayMethod = Person.class.getDeclaredMethod("say",String.class);
MethodReflect methodBindArgs = Reflect.on(sayMethod,new Object[]{"讲中文"});
methodBindArgs.callBy(new Person);//output : 讲中文。
```
### 2. 方法绑定：bind(Object...)
```
MethodReflect mr = Reflect.on(sayMethod);
mr.bind("讲鸟语").callBy(new Person());//output: 讲鸟语
```
