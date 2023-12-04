package springboot.loadjar1.test1;

public class TestB {

    public void hello() {
        System.out.println("TestB: " + this.getClass().getClassLoader());
    }
}
