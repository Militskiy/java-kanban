package tasks.util;

import tasks.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartDate() == null && o2.getStartDate() != null) {
            return 1;
        } else if (o1.getStartDate() != null && o2.getStartDate() == null) {
            return -1;
        } else if (o1.getStartDate() == null && o2.getStartDate() == null) {
            if (o1.getId().equals(o2.getId())) {
                return 0;
            }
            return 1;
        }
        return o1.getStartDate().compareTo(o2.getStartDate());
    }
}
