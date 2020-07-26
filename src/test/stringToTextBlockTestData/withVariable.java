public class X {
	void f(boolean isMale) {
		int valueA = 12;
		int valueB = 3;
		String title = <caret>"{\n" +
				"	\"a\":\"" + valueA + "\",\n" +
				"	\"b\":\"" + valueB + "\"\n" +
				"}";
		System.out.println("title = " + title);
	}
}