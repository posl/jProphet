package testMavenProject01;

public class RoughConstantValue {
    public double calcCircleArea(double radius) {
        double pi = Math.PI;
        double area = radius * 2;
        area *= 3.141592;
        return area;
    }

    public static void main(String[] args) {
        final RoughConstantValue roughConstantValue = new RoughConstantValue();
        System.out.println(roughConstantValue.calcCircleArea(5));
    }
}
