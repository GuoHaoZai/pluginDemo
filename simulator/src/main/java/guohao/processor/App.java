package guohao.processor;

import guohao.anno.Getter;
import guohao.anno.Utils;

@Getter
@Utils
public class App {
    private String value;

    private String value2;

    public App(String value) {
        this.value = value;
    }

    public static void main(String[] args) {
        App app = new App("it works");
//        System.out.println(app.getValue()); // TODO 测试时，解开注释
    }
}
