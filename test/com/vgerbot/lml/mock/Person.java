/**
 * 姓名：陆梦琳
 *下午10:07:02
 */
package com.vgerbot.lml.mock;

public class Person {
	private String name;
	private int age;
	
	
	/**
	 * 
	 */
	public Person() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 * @param age
	 */
	public Person(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}
}
