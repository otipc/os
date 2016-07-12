package co.otipc.job;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Job {

  List<String> dims = new ArrayList<>();

  Conditions conditions=new Conditions();

  String table;

  boolean isNeedJoin;

  String joinTable;

  List<Job> subJobs;


  public void setDims(List<String> dims) {
    this.dims = dims;
  }

  public List<String> getDims() {
    return dims;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getTable() {
    return table;
  }


  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
  }

  public Conditions getConditions() {
    return conditions;
  }


  public List<String> doExec() {
    try {
      return Executor.exec(this);
    } catch (IOException e) {

    }
    return null;
  }


  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
