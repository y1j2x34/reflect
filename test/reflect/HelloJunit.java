/**
 * 姓名：陆梦琳
 *下午10:05:33
 */
package reflect;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.Test;

import com.vgerbot.lml.mock.Person;
import com.vgerbot.reflect.Reflect;

import junit.framework.Assert;

public class HelloJunit {
	@Test
	public void testReflectCreationAPI(){
		Object result = Reflect.on(Person.class).create().off();
		assertThat()
	}
	@Test
	public void helloworld(){
		String expectedName = "陆梦琳";
		String actual = Reflect.on(Person.class)
		.create(expectedName, 0)
		.call("getName").off();
		// Assert.assertEquals(expectedName, actual);
		assertThat(actual, equalTo(expectedName));
	}
}
