public class X {
	void f(boolean isMale) {
		int valueA = 12;
		int valueB = 3;
		String title = String.format("""
                {
                	"a":"%s",
                	"b":"%s"
                }
                """, valueA, valueB);
		System.out.println("title = " + title);
	}
}