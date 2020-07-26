public class X {
	void f(boolean isMale) {
		String title = <caret>"{\n" +
				"	\"a\":\"A\",\n" +
				"	\"b\":\"B\"\n" +
				"}";
		System.out.println("title = " + title);
	}
}
