import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by huangbaihua on 2015/12/12.
 */
public class Demo {
    public static void main(String[] args) {
        String condition = "{a,eq,b}{c,neq,@}{c,like,abc}";

        List<Integer> leftBraceIdxList = new ArrayList<>();
        List<Integer> rightBraceIdxList = new ArrayList<>();

        byte[] bytes2 = condition.getBytes();
        for (int i = 0; i < bytes2.length; i++) {
            byte b = bytes2[i];
            if (b == 123) {
                leftBraceIdxList.add(i);
            } else if (b == 125) {
                rightBraceIdxList.add(i);
            }
        }

        System.out.println(leftBraceIdxList);
        System.out.println(rightBraceIdxList);

        for (int i = 0; i < leftBraceIdxList.size(); i++) {
            System.out.println(condition.substring(leftBraceIdxList.get(i) + 1,rightBraceIdxList.get(i)));
        }

        String a = "fieldName,expr,,condValue";
        String[] tokens = a.split(",expr,");
        for (String t : tokens) {
            System.out.println(t);
        }
    }
}
