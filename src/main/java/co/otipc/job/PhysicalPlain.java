package co.otipc.job;

import co.otipc.plain.VisitorSelect;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class PhysicalPlain {



  public PhysicalPlain() {

  }

  public Job getJob(String sql) throws JSQLParserException {

    Statement parse = CCJSqlParserUtil.parse(sql);
    Select select = (Select) parse;
    PlainSelect ps = (PlainSelect) select.getSelectBody();

    Job job = new Job();

    VisitorSelect.doSelect(job, ps);

    return job;
  }



}
