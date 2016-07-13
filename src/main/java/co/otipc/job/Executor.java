package co.otipc.job;

import co.otipc.plain.VisitorExpression;
import co.otipc.plain.VisitorSelect;
import co.otipc.utils.SqlUtils;
import com.google.common.collect.Sets;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Executor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

  private final static String dir = "/Users/admaster/sql/";

  private static Job job;

  private static String[] columns;

  private static Map<String, Integer> mapIndex;

  public static List<String> exec(Job j) throws IOException {

    job = j;

    List<String> table_source = readFile(job.getTable());
    columns = table_source.get(0).split(",");
    mapIndex = SqlUtils.parserToMap(columns);

    table_source = checkJoin(table_source, job);

    //    while (job.isNeedJoin()) {
    //      List<String> join_result = new ArrayList<>();
    //      Join join = job.getJoin();
    //      FromItem fromItem = join.getRightItem();
    //      if (fromItem instanceof SubSelect) {
    //        SubSelect subSelect = (SubSelect) fromItem;
    //        PlainSelect subPs = (PlainSelect) subSelect.getSelectBody();
    //        if (null != subPs) {
    //          Job innerJob = new Job();
    //          VisitorSelect.doSelect(innerJob, subPs);
    //          join_result = innerJob.doExec();
    //          //todo  write to temp file
    //        } else {
    //          join_result = null;
    //          //todo join table
    //        }
    //      } else if (fromItem instanceof Table) {
    //        Table table = (Table) fromItem;
    //        join_result = readFile(table.getName());
    //        //todo join table
    //      } else {
    //
    //      }
    //      Conditions joinConditions = null;
    //      Expression expression = join.getOnExpression();
    //      if (null != expression) {
    //        joinConditions = new Conditions();
    //        VisitorExpression.doExpr(joinConditions, expression);
    //      }
    //
    //      if (join.isSimple() || join.isLeft()) {
    //        table_source = JoinMethod.doJoin(table_source, join_result, joinConditions);
    //      } else if (join.isRight()) {
    //        table_source = JoinMethod.doJoin(join_result, table_source, joinConditions);
    //      } else if (join.isInner()) {
    //
    //      } else if (join.isFull()) {
    //
    //      } else if (join.isCross()) {
    //
    //      } else if (join.isOuter()) {
    //
    //      } else if (join.isNatural()) {
    //
    //      } else {
    //        LOGGER.error(join.toString());
    //      }
    //
    //    }


    List<String> result = new ArrayList<>();
    doGetResultSet(table_source, job.getConditions(), result);

    return result;

  }


  private static List<String> checkJoin(List<String> table_source, Job job) throws IOException {

    while (job.isNeedJoin()) {

      List<String> join_result = new ArrayList<>();
      Join join = job.getJoin();

      FromItem fromItem = join.getRightItem();
      if (fromItem instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) fromItem;
        PlainSelect subPs = (PlainSelect) subSelect.getSelectBody();
        if (null != subPs) {
          Job innerJob = new Job();
          VisitorSelect.doSelect(innerJob, subPs);
          join_result = innerJob.doExec();
          //todo  write to temp file
        } else {
          LOGGER.error(subSelect.toString());
        }
      } else if (fromItem instanceof Table) {
        Table table = (Table) fromItem;
        join_result = readFile(table.getName());
        //todo join table
      } else {
        LOGGER.error(fromItem.toString());
      }


      Conditions joinConditions = null;
      Expression expression = join.getOnExpression();
      if (null != expression) {
        joinConditions = new Conditions();
        VisitorExpression.doExpr(joinConditions, expression);
      }

      if (null != joinConditions) {
        System.out.println(joinConditions.toString());
      }

      if (join.isSimple() || join.isLeft()) {
        table_source = JoinMethod.doJoin(table_source, join_result, joinConditions);
      } else if (join.isRight()) {
        table_source = JoinMethod.doJoin(join_result, table_source, joinConditions);
      } else if (join.isInner()) {
        table_source = JoinMethod.doJoin(join_result, table_source, joinConditions);
      } else if (join.isFull()) {

      } else if (join.isCross()) {

      } else if (join.isOuter()) {

      } else if (join.isNatural()) {

      } else {
        LOGGER.error(join.toString());
      }

    }

    return table_source;


  }

  //  private static List<String> execJoin(String left_table, String right_table) throws IOException {
  //
  //
  //    List<String> left_table_source = readFile(left_table);
  //    List<String> right_table_source = readFile(right_table);
  //
  //    return JoinMethod.doJoin(left_table_source, right_table_source);
  //
  //  }


  private static List<String> readFile(File file) throws IOException {
    return FileUtils.readLines(file, Charset.defaultCharset());
  }

  private static List<String> readFile(String tableName) throws IOException {
    return readFile(getFile(tableName));
  }

  private static File getFile(String tableName) {
    return new File(
      Executor.class.getClassLoader().getResource("file/" + tableName + ".txt").getFile());

  }

  private static void doGetResultSet(List<String> source, Conditions conditions,
    List<String> result) {

    for (int i = 1; i < source.size(); i++) {
      checkFilter(source.get(i), conditions, result);
    }

  }

  private static void setLineResultValue(String source, List<String> result) {
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

  private static void checkFilter(String line, Conditions conditions, List<String> result) {

    if (null == conditions) {
      setLineResultValue(line, result);
    } else {
      String type = conditions.getType();
      if ("and".equalsIgnoreCase(type) || null == type) {
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
        setLineResultValue(line, result);

      } else if ("or".equalsIgnoreCase(type)) {
        for (Condition cond : conditions.getItems()) {
          if ("==".equals(cond.getType())) {
            if (equalsIgnoreCase(line.split(",")[mapIndex.get(cond.getColumn())],
              cond.getValue().toString())) {
              setLineResultValue(line, result);
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
              setLineResultValue(line, result);
              return;
            }
          }


        }
      }

    }
  }


}




