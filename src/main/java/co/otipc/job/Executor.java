package co.otipc.job;

import co.otipc.utils.SqlUtils;
import org.apache.commons.io.FileUtils;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Executor {

  private final static String dir = "/Users/admaster/sql/";

  private Job job;

  private List<String> table_source;

  private List<String> result;

  private String[] columns;

  private Map<String, Integer> mapIndex;

  private Conditions conditions;



  public List<String> exec(Job job) throws IOException {
    this.job = job;
    result = new ArrayList<>();
    table_source =
      FileUtils.readLines(new File(dir + this.job.getTable() + ".txt"), Charset.defaultCharset());

    columns = table_source.get(0).split(",");
    mapIndex = SqlUtils.parserToMap(columns);

    conditions = this.job.getConditions();

    doGetResultSet();

    return result;

  }

  private void doGetResultSet() {

    for (int i = 1; i < table_source.size(); i++) {
      checkFilter(table_source.get(i));
    }

  }

  private void setLineResultValue(String source) {
    List<String> dims = this.job.dims;
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

  private void checkFilter(String line) {

    if (null == conditions) {
      setLineResultValue(line);
    } else {
      String type = this.conditions.getType();
      if ("and".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if (equalsIgnoreCase("==", cond.getType())) {
            if (!equalsIgnoreCase(line.split(",")[mapIndex.get(cond.getColumn())],
              cond.getValue().toString())) {
              return;
            }
          }
        }
        setLineResultValue(line);

      } else if ("or".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if ("==".equals(cond.getType())) {
            if (!line.split(",")[mapIndex.get(cond.getColumn())].equals(cond.getValue())) {
              setLineResultValue(line);
              return;
            }
          }
        }
      }

    }
  }


}




