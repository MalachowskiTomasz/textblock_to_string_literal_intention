public class X {
	void f(boolean isMale) {
		String name = "world";
		String title = <caret>"hello " + name;
		System.out.println("title = " + title);
	}
}