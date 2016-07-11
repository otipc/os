package co.otipc.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class SqlUtils {


  public static Map<String, Integer> parserToMap(String[] columns) {
    Map<String, Integer> result = new HashMap<>();
    for (int i = 0; i < columns.length; i++) {
      result.put(columns[i], i);
    }

    return result;
  }



}
