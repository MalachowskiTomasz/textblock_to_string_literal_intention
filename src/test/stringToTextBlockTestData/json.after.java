public class X {
	void f(boolean isMale) {
		String title = <caret>"""
                {
                	"a":"A",
                	"b":"B"
                }
                """;
		System.out.println("title = " + title);
	}
}
