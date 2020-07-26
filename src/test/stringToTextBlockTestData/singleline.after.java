public class X {
	void f(boolean isMale) {
		String title = <caret>"""
                {

                """;
		System.out.println("title = " + title);
	}
}
