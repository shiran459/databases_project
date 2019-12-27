package app.utils;

import java.sql.Date;
import java.util.List;

public class Expression {
    public int expressionId;
    public int userId;
    public List<Integer> wordIdList;
    public String value;
    public Date creationDate;

    public Expression(int expressionId, int userId, List<Integer> wordIdList, String value, Date creationDate) {
        this.expressionId = expressionId;
        this.userId = userId;
        this.wordIdList = wordIdList;
        this.value = value;
        this.creationDate = creationDate;
    }
}
