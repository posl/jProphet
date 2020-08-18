package MavenFizzBuzz01;

public class FizzBuzz {
    public String call(int i) {
        if(i % 15 == 0)
            return "FizzBuzz";
        if(i % 5 == 0)
            return "Buzz";
        if(i % 3 == 0)
            return "Fizz";
        return String.valueOf(0);
    }

    public static void main(String[] args) {
        for(int i = 0; i < 100; i++){
            System.out.println(new FizzBuzz().call(i));
        }
    }
}
