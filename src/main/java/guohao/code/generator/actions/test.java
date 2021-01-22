package guohao.code.generator.actions;

import java.util.StringJoiner;

/**
 * @author guohao
 * @since 2021/1/22
 */
public class test {
    public static void main(String[] args) {
        System.out.println(new StringJoiner("a", "v", "b").add("aadd").setEmptyValue("").toString());
    }

}
