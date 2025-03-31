import org.springframework.context.annotation.Bean;

public class BadClass {
    public void doSomething() {
        String myString = "test";
        myString.trim(); // SpotBugs: RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT
    }
}
