package co.otipc;

import co.otipc.job.Executor;
import co.otipc.job.Job;
import co.otipc.job.PhysicalPlain;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chaoguo.Cui on 16/7/7.
 */
public class App {


  public static void main(String[] args) throws JSQLParserException, IOException {


    //    String sql =
    //      "SELECT MY_TABLE1.* FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "
    //        + " WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6) AND id3=3";

    //    String sql = "select * from table_1 where age=31 and name='otipc'";

    //    String sql = "select * from table_1 where age in(33,40)";

    String sql = "select * from student where class in (select id from class where name='class_4')";

    PhysicalPlain physicalPlain = new PhysicalPlain();

    Job job = physicalPlain.getJob(sql);

    System.out.println(job);


    List<String> result = job.doExec();

    for (String str : result) {
      System.out.println(str);
    }



  }



}
