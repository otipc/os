package co.otipc.job;

import co.otipc.utils.SqlUtils;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Executor {

  private final static String dir = "/Users/admaster/sql/";

  private static Job job;

  private static List<String> table_source;

  private static List<String> result;

  private static String[] columns;

  private static Map<String, Integer> mapIndex;

  private static Conditions conditions;


  public static List<String> exec(Job j) throws IOException {
    job = j;

    File file = new File(
      Executor.class.getClassLoader().getResource("file/" + job.getTable() + ".txt").getFile());

    result = new ArrayList<>();

    table_source = FileUtils.readLines(file, Charset.defaultCharset());

    columns = table_source.get(0).split(",");
    mapIndex = SqlUtils.parserToMap(columns);

    conditions = job.getConditions();

    doGetResultSet();

    return result;

  }

  private static void doGetResultSet() {

    for (int i = 1; i < table_source.size(); i++) {
      checkFilter(table_source.get(i));
    }

  }

  private static void setLineResultValue(String source) {
    List<String> dims = job.getDims();
    if (dims.size() == 1 && equalsIgnoreCase("*", dims.get(0))) {
      result.add(source);
    } else {
      StringBuilder sb = new StringBuilder();
      for (String dim : dims) {
        sb.append(source.split(",")[mapIndex.get(dim)]);
        sb.append(",");
      }
      if (sb.length() > 1)
        result.add(sb.substring(0, sb.length() - 1));
    }
  }

  private static void checkFilter(String line) {

    if (null == conditions) {
      setLineResultValue(line);
    } else {
      String type = conditions.getType();
      if ("and".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if (equalsIgnoreCase("==", cond.getType())) {
            if (!equalsIgnoreCase(line.split(",")[mapIndex.get(cond.getColumn())],
              cond.getValue().toString())) {
              return;
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
            if (!set.contains(line.split(",")[mapIndex.get(cond.getColumn())])) {
              return;
            }
          }

        }
        setLineResultValue(line);

      } else if ("or".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if ("==".equals(cond.getType())) {
            if (equalsIgnoreCase(line.split(",")[mapIndex.get(cond.getColumn())],
              cond.getValue().toString())) {
              setLineResultValue(line);
              return;
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
            if (set.contains(line.split(",")[mapIndex.get(cond.getColumn())])) {
              setLineResultValue(line);
              return;
            }
          }


        }
      }

    }
  }


}




