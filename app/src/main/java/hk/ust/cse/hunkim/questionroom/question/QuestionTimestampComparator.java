package hk.ust.cse.hunkim.questionroom.question;

import java.util.Comparator;

public class QuestionTimestampComparator implements Comparator<Question> {
    private boolean isAscending;

    public QuestionTimestampComparator(boolean isAscending) {
        this.isAscending = isAscending;
    }

    @Override
    public int compare(Question lhs, Question rhs) {

        if(lhs.getOrder()==1){
            if(rhs.getOrder()==lhs.getOrder())
                return 0;
            return -1;
        }
        if(rhs.getOrder()==1){
            return 1;
        }

        if (lhs.getTimestamp() == rhs.getTimestamp())
            return 0;
        if (lhs.getTimestamp() < rhs.getTimestamp())
            return isAscending ? -1 : 1;
        else
            return isAscending ? 1 : -1;
    }
}