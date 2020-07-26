public class X {
	void f(boolean isMale) {
		String title = <caret>"<li>\n" +
				"	\"How are you doing?\"\n" +
				"\n" +
				"</li>";
		System.out.println("title = " + title);
	}
}
