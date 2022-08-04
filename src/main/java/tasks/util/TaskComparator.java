package tasks.util;

import tasks.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartDate() == null) {
            return 1;
        }
        if (o2.getStartDate() == null) {
            return -1;
        }
        if (o1.getStartDate().equals(o2.getStartDate())) {
            return 0;
        }
        return o1.getStartDate().compareTo(o2.getStartDate());
    }
}
