package co.otipc;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;

/**
 * Created by Chaoguo.Cui on 16/7/7.
 */
public class App {
  public static void main(String[] args) throws JSQLParserException {

    System.out.println();

    CCJSqlParserManager pm = new CCJSqlParserManager();

    String sql = "";

    Statement stat = pm.parse(new StringReader(sql));

  }
}
