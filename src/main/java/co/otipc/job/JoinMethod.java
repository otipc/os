package co.otipc.job;

import co.otipc.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Created by Chaoguo.Cui on 16/7/12.
 */
public class JoinMethod {

  private static final Logger LOGGER = LoggerFactory.getLogger(JoinMethod.class);

  private static Object[][] xyz = new Object[2][];
  private static int counterIndex = xyz.length - 1;
  private static int[] counter = new int[2];

  private static String[] left_scheme;
  private static String[] right_scheme;

  private static Conditions conditions;

  public static List<String> doJoin(List<String> left, List<String> right, Conditions con) {

    LinkedList<String> result = new LinkedList<>();

    conditions = con;

    left_scheme = left.remove(0).split(",");
    right_scheme = right.remove(0).split(",");

    int tmp = 1;

    tmp *= left.size();
    tmp *= right.size();

    xyz[0] = left.toArray();
    xyz[1] = right.toArray();

    for (int i = 0; i < tmp; i++) {

      String l = xyz[0][counter[0]].toString();
      String r = xyz[1][counter[1]].toString();

      //      System.out.println(l + " == " + r);

      if (joinCheckFilter(l, r)) {
        result.add(getLineResult(l, r));
      }
      handle();

    }

    String last_scheme = "";
    result.addFirst(last_scheme);
    return result;

  }

  private static String getLineResult(String left, String right) {

    return getDimsValue(left) + "," + getDimsValue(right);
  }

  private static String getDimsValue(String source) {

    //// TODO: 16/7/12  
    return source;
  }

  private static boolean joinCheckFilter(String left, String right) {
    if (null == conditions) {
      return true;
    } else {
      String type = conditions.getType();
      if ("and".equalsIgnoreCase(type) || null == type) {
        for (Condition cond : conditions.getItems()) {
          if (equalsIgnoreCase("==", cond.getType())) {
            if (!equalsIgnoreCase(left.split(",")[SqlUtils.parserToMap(left_scheme).get(cond.getColumn())],
              cond.getValue().toString())) {
              return false;
            }
          }

          if (equalsIgnoreCase("in", cond.getType())) {
            String[] ss =
              cond.getValue().toString().substring(1, cond.getValue().toString().length() - 1)
                .split(",");
            Set set = new HashSet();
            for (String str : ss) {
              set.add(str.trim());
            }
            if (!set.contains(left.split(",")[SqlUtils.parserToMap(left_scheme).get(cond.getColumn())])) {
              return false;
            }
          }

        }
        return true;
      } else if ("or".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if (equalsIgnoreCase("==", cond.getType())) {
            if (!equalsIgnoreCase(left.split(",")[SqlUtils.parserToMap(left_scheme).get(cond.getColumn())],
              cond.getValue().toString())) {
              return true;
            }
          }

          if (equalsIgnoreCase("in", cond.getType())) {
            String[] ss =
              cond.getValue().toString().substring(1, cond.getValue().toString().length() - 1)
                .split(",");
            Set set = new HashSet();
            for (String str : ss) {
              set.add(str.trim());
            }
            if (set.contains(left.split(",")[SqlUtils.parserToMap(left_scheme).get(cond.getColumn())])) {
              return true;
            }
          }

        }
      }
    }

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

    //    doJoin(left, right);



  }


}
