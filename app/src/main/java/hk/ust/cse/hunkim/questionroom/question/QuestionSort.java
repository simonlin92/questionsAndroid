package hk.ust.cse.hunkim.questionroom.question;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

import hk.ust.cse.hunkim.questionroom.R;

public class QuestionSort {
    public static final Order defaultSort = Order.TIME_DESC;
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
        int num = sharedPref.getInt(prefKey, defaultSort.getValue());
        return getComparator(intToEnum(num));
    }

    public Order readSortByEnum() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int num = sharedPref.getInt(prefKey, defaultSort.getValue());
        return intToEnum(num);
    }

    private Comparator<? super Question> getComparator(Order sort) {
        switch (sort) {
            case TIME_ASC:
                return new QuestionTimestampComparator(true);
            case TIME_DESC:
                return new QuestionTimestampComparator(false);
            case ECHO_DESC:
                return new QuestionEchoComparator(false);
            case ECHO_ASC:
            default:
                return new QuestionEchoComparator(true);
        }
    }

    public static Order intToEnum(int num) {
        for (Order o : Order.values()) {
            if (o.getValue() == num)
                return o;
        }
        return defaultSort;
    }

    public enum Order {
        TIME_DESC(0, "Most Recent", R.drawable.time_desc),
        ECHO_DESC(1, "Most Likes", R.drawable.echo_desc),
        TIME_ASC(2, "Oldest", R.drawable.time_asc),
        ECHO_ASC(3, "Least Likes", R.drawable.echo_asc);

        private final int value;
        private final String text;
        private final int icon;

        Order(int value, String text, int icon) {
            this.value = value;
            this.text = text;
            this.icon = icon;
        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public int getIcon() {
            return icon;
        }
    }
}
