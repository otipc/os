package co.otipc.job;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class JoinMethod {

  private static Object[][] xyz = new Object[2][];
  private static int counterIndex = xyz.length - 1;
  private static int[] counter = new int[2];

  public static List<String> doJoin(List<String> left, List<String> right) {

    List<String> result = new ArrayList<>();

    int tmp = 1;

    tmp *= left.size();
    tmp *= right.size();

    xyz[0] = left.toArray();
    xyz[1] = right.toArray();

    for (int i = 0; i < tmp; i++) {

      String l = xyz[0][counter[0]].toString();
      String r = xyz[1][counter[1]].toString();

      System.out.println(l + " == " + r);

      if (joinCheckFilter(l, r)) {
        result.add(getLineResult(l, r));
      }
      handle();

    }

    return result;

  }

  private static String getLineResult(String left, String right) {

    return getDimsValue(left) + "," + getDimsValue(right);
  }

  private static String getDimsValue(String source) {

    //// TODO: 16/7/12  
    return null;
  }

  private static boolean joinCheckFilter(String left, String right) {
    return false;
  }


  public static void handle() {
    counter[counterIndex]++;
    if (counter[counterIndex] >= xyz[counterIndex].length) {
      counter[counterIndex] = 0;
      counterIndex--;
      if (counterIndex >= 0) {
        handle();
      }
      counterIndex = xyz.length - 1;
    }
  }


  public static void main(String[] args) {
    List<String> left = new ArrayList<>();
    left.add("left_1");
    left.add("left_2");
    left.add("left_3");

    List<String> right = new ArrayList<>();
    right.add("right_1");
    right.add("right_2");
    right.add("right_3");

    doJoin(left, right);



  }


}
