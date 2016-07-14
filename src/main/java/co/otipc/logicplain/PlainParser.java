package co.otipc.logicplain;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chaoguo.Cui on 16/7/13.
 */
public class PlainParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(PlainParser.class);

  public static Queue<Plain> plains = new LinkedBlockingQueue<>();

  public static void explain(PlainSelect select) {

  }


  //    1. FROM:对FROM子句中的前两个表执行笛卡尔积，生成虚拟表VT1。
  //    2. ON:对VT1应用ON筛选器。只有那些使<join_condition>为真的行才被插入VT2。
  //    3. OUTER(JOIN):如果指定了OUTER JOIN，保留表中未找到匹配的行将作为外部行添加到VT2，生成VT3。
  //       如果FROM子句包含两个以上的表，则对上一个联接生成的结果表和下一个表重复执行步骤1到步骤3，直到
  //       处理完所有的表为止。
  //    4. 对VT3应用WHERE筛选器。只有使<where_condition>为TRUE的行才被插入VT4。
  //    5. GROUP BY:按GROUP BY 子句中的列列表对VT4中的行分组，生成VT5。
  //    6. CUBEROLLUP:把超组插入VT5，生成VT6。
  //    7. HAVING:对VT6应用HAVING筛选器。只有使<having_condition>为TRUE的组才会被插入VT7。
  //    8. SELECT:处理SELECT列表，产生VT8。
  //    9. DISTINCT:将重复的行从VT8中移除，产生VT9。
  //    10. ORDER BY:将VT9中的行按ORDER BY子句中的列列表排序，生成一个有表(VC10)。
  //    11. TOP:从VC10的开始处选择指定数量或比例的行，生成表VT11,并返回给调用者。


  public static void main(String[] args) throws JSQLParserException {
    doSelect();
  }

  public static void doSelect() throws JSQLParserException {

    String sql = "select * from table_1 where id=0001 and age=31 and name='otipc'";

    Statement parse = CCJSqlParserUtil.parse(sql);
    Select select = (Select) parse;
    PlainSelect ps = (PlainSelect) select.getSelectBody();
    doFrom(ps);

  }

  private static List<Object> doFrom(PlainSelect plain) {
    List<Object> result = new ArrayList();

    if (null != plain.getFromItem()) {
      FromItem from = plain.getFromItem();

      if (from instanceof LateralSubSelect) {

        LateralSubSelect lateralSubSelect = (LateralSubSelect) from;
        SubSelect subSelect = lateralSubSelect.getSubSelect();
        PlainSelect ps = (PlainSelect) subSelect.getSelectBody();
        List<Object> tmp_l = doFrom(ps);

      } else if (from instanceof SubJoin) {
        SubJoin subjoin = (SubJoin) from;
        FromItem left = subjoin.getLeft();
        Join join = subjoin.getJoin();

      } else if (from instanceof Table) {

        Table table = (Table) from;

        table.getName();
        Alias alias = new Alias("a");
        table.setAlias(alias);
        Pivot pivot = new Pivot();
        List<Column> columns = new ArrayList<>();
        columns.add(new Column(table, "id"));
        columns.add(new Column(table, "name"));
        pivot.setForColumns(columns);
        table.setPivot(pivot);


        System.out.println(table);



      } else if (from instanceof SubSelect) {
        SubSelect subSelect = (SubSelect) from;

        PlainSelect ps = (PlainSelect) subSelect.getSelectBody();
        List<Object> tmp_l = doFrom(ps);


      } else if (from instanceof ValuesList) {
        ValuesList valuesList = (ValuesList) from;



      } else if (from instanceof TableFunction) {
        //        TableFunction tableFunction = (TableFunction) from;
        //        tableFunction.getFunction()
      }

    }

    if (null != plain.getJoins()) {

    }


    return result;
  }

  private static List<Object> doOn(List<Object> source, PlainSelect plain) {
    List<Object> result = new ArrayList();

    if (null != plain.getJoins()) {
      for (Join join : plain.getJoins()) {
        join.getOnExpression();
      }
    }



    return result;
  }

  private static List<Object> doJoin(PlainSelect plain) {
    List<Object> result = new ArrayList();



    return result;
  }


}
