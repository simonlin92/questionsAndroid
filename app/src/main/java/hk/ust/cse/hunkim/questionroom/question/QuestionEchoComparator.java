package hk.ust.cse.hunkim.questionroom.question;

import java.util.Comparator;

public class QuestionEchoComparator implements Comparator<Question> {
    private boolean isAscending;

    public QuestionEchoComparator(boolean isAscending) {
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
        if (lhs.getEcho() == rhs.getEcho())
            return 0;
        if (lhs.getEcho() < rhs.getEcho())
            return isAscending ? -1 : 1;
        else
            return isAscending ? 1 : -1;
    }
}