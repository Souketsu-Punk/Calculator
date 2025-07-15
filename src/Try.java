import java.util.Scanner;
// test for the compiler and running of the code
public class Try {
    public static void main(String[] args) {
        Scanner txt = new Scanner(System.in);
        System.out.println("enter first number");
        int x = txt.nextInt();
        System.out.println("Enter next no.");
        int y = txt.nextInt();
        int z = x+y;
        System.out.println("the answer is " + z);
    }
}
