package hk.ust.cse.hunkim.questionroom.question;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

public class QuestionSort {
    private Activity activity;
    private static final String prefKey = "QuestionSortOrder";

    public QuestionSort(Activity activity) {
        this.activity = activity;
    }

    public void saveSort(Order sort) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(prefKey, sort.getValue());
        editor.apply();
    }

    public Comparator<? super Question> readSort() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int num = sharedPref.getInt(prefKey, Order.ECHO_ASC.getValue());
        return getComparator(intToEnum(num));
    }

    private Comparator<? super Question> getComparator(Order sort) {
        switch (sort) {
            case ECHO_DESC:
                return new QuestionEchoComparator(false);
            case ECHO_ASC:
            default:
                return new QuestionEchoComparator(true);
        }
    }

    private Order intToEnum(int num) {
        for (Order o : Order.values()) {
            if (o.getValue() == num)
                return o;
        }
        return Order.ECHO_ASC;
    }

    public enum Order {
        ECHO_ASC(0), ECHO_DESC(1);

        private final int value;

        Order(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
