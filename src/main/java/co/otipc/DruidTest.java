package co.otipc;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/11.
 */
public class DruidTest {

  public static void main(String[] args) {

    //    test();

    int i = comput(2);

    System.out.println(i);

  }

  private static int comput(int n) {
    try {
      int x = 1 / n;
      return n + comput(n - 1);
    } catch (Exception e) {
      return 0;
    }
  }


  private static void test() {
    String sql = "select * from table_1 where age=31 and name='otipc'";
    sql = "SELECT ID, NAME, AGE FROM USER WHERE ID in (select id from t2 where name='xxx')";

    sql =
      "SELECT ID, NAME, AGE FROM USER join id2 WHERE ID in (select id from t2 where name='xxx')";

    SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
    List<SQLStatement> ListstmtList = parser.parseStatementList(); //

    System.out.println(ListstmtList.size());

    for (SQLStatement statement : ListstmtList) {
      System.out.println(statement.toString());
    }
  }


  private static void test2() {
    String sql =
      "select p, s.count as views, (select count(*) from Comments rc where rc.linkedId=p.id and rc.classcode='InfoPublishs') as commentNumber, (select count(*) from CollectIndexs rci where rci.toId=p.id and rci.classcode='InfoPublishs' and rci.type='favorite') as favorite FROM InfoPublishs p,UserScores s where p.id=s.linkedId and p.userInfo.id=s.userInfo.id and s.classCode='InfoPublishs' AND p.status=?1 ORDER BY p.createtime DESC";

    sql = "SELECT ID, NAME, AGE FROM USER WHERE ID in (select id from t2 where name='xxx')";

    StringBuffer select = new StringBuffer();
    StringBuffer from = new StringBuffer();
    StringBuffer where = new StringBuffer();
    // parser得到AST
    SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
    List<SQLStatement> stmtList = parser.parseStatementList(); //
    // 将AST通过visitor输出
    SQLASTOutputVisitor visitor =
      SQLUtils.createFormatOutputVisitor(from, stmtList, JdbcUtils.MYSQL);

    SQLASTOutputVisitor whereVisitor =
      SQLUtils.createFormatOutputVisitor(where, stmtList, JdbcUtils.MYSQL);


    for (SQLStatement stmt : stmtList) {
      if (stmt instanceof SQLSelectStatement) {
        SQLSelectStatement sstmt = (SQLSelectStatement) stmt;
        SQLSelect sqlselect = sstmt.getSelect();
        SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

        System.out.println(query.getSelectList());

        query.getFrom().accept(visitor);
        query.getWhere().accept(whereVisitor);
      }
      System.out.println(from.toString());
      System.out.println(select);
      System.out.println(where);
    }
  }


}
