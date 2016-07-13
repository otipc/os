package co.otipc;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/7.
 */
public class MainTest {


  public static void main(String[] args) throws JSQLParserException {


    String sql = "select * from table_1 where id=0001 or age=31 and name='otipc'";

    String ss = "select  A.ID as AID, B.ID as BID  from A left join B on A.ID = B.ID where B.ID<3 ";

    String xx = "select  A.ID as AID, B.ID as BID  from A left join B on A.ID = B.ID and  B.ID<3";

    String leftjoin = "SELECT * FROM a LEFT JOIN  b ON a.aID =b.bID ";

    //    aID        aNum                   bID           bName
    //    1            a20050111         1               2006032401
    //    2            a20050112         2              2006032402
    //    3            a20050113         3              2006032403
    //    4            a20050114         4              2006032404
    //    5            a20050115         NULL       NULL

    String rightjoin = "SELECT  * FROM a RIGHT JOING b ON a.aID = b.bID ";

    //    aID        aNum                   bID           bName
    //    1            a20050111         1               2006032401
    //    2            a20050112         2              2006032402
    //    3            a20050113         3              2006032403
    //    4            a20050114         4              2006032404
    //    NULL    NULL                   8              2006032408

    String innerjoin = "SELECT * FROM  a INNER JOIN  b ON a.aID =b.bID";
    String innerjoin2 = "SELECT *  FROM a,b WHERE a.aID = b.bID";

    //    aID        aNum                   bID           bName
    //    1            a20050111         1              2006032401
    //    2            a20050112         2              2006032402
    //    3            a20050113         3              2006032403
    //    4            a20050114         4              2006032404


    Statement parse = CCJSqlParserUtil.parse(innerjoin);
    Select select = (Select) parse;
    PlainSelect ps = (PlainSelect) select.getSelectBody();

    List<WithItem> list = select.getWithItemsList();
    for (WithItem with : list) {
      System.out.println(with.toString());
    }

    System.out.println(ps.getSelectItems());

    System.out.println(ps.getFromItem());

    System.out.println(ps.getJoins());

    List<Join> joins = ps.getJoins();
    for (Join join : joins) {

      System.out.println(join.isSimple());
      System.out.println(join.isLeft());
      System.out.println(join.isFull());
      System.out.println(join.isInner());
      System.out.println(join.isNatural());
      System.out.println(join.isOuter());
      System.out.println(join.isRight());

      System.out.println(" === " + join.getRightItem());

      FromItem from = join.getRightItem();
      System.out.println(from instanceof Table);
      System.out.println(join.getOnExpression());

    }

    Expression expression = ps.getWhere();

    if (null != expression) {
      System.out.println(expression instanceof AndExpression);
      System.out.println(expression instanceof OrExpression);

      System.out.println(((BinaryExpression) expression).getLeftExpression());
      System.out.println(((BinaryExpression) expression).getRightExpression());
    }



  }


}
