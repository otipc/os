package co.otipc.job;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Chaoguo.Cui on 16/7/8.
 */
public class Condition {


  public static void main(String[] args){

    String str="'otipc'";

    if(str.startsWith("'")&&str.endsWith("'")){
      System.out.println("dsaffds");
    }

  }

  private String type;

  private String column;

  private Object value;

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
