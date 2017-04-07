package me.jarkimzhu.libs.utils.collections;


import me.jarkimzhu.libs.utils.annotation.AnnotationUtils;
import me.jarkimzhu.libs.utils.annotation.Priority;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @param <E>
 * @author JarkimZhu
 */
public class PriorityObjects<E> extends PriorityQueue<E> {

    /**
     * Auto generate
     */
    private static final long serialVersionUID = -2079515068769731860L;
    private static Comparator<Object> priorityComparator = new PriorityComparator();

    private boolean checkPriorityCapability;
    /**
     *
     */
    public PriorityObjects() {
        super(priorityComparator);
    }

    public PriorityObjects(boolean checkPriorityCapability) {
        super(priorityComparator);
        this.checkPriorityCapability = checkPriorityCapability;
    }

    @Override
    public boolean add(E e) {
        if(checkPriorityCapability) {
            if (!AnnotationUtils.checkAnnotationCapable(e, Priority.class)) {
                throw new RuntimeException(e.getClass() + " hasn't Priority capability !");
            }
        }
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(checkPriorityCapability) {
            for (E e : c) {
                if (!AnnotationUtils.checkAnnotationCapable(e, Priority.class)) {
                    throw new RuntimeException(e.getClass() + " hasn't Priority capability !");
                }
            }
        }
        return super.addAll(c);
    }

    private static class PriorityComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            Priority p1 = o1.getClass().getAnnotation(Priority.class);
            Priority p2 = o2.getClass().getAnnotation(Priority.class);

            if (p1 != null && p2 != null) {
                return p1.value() > p2.value() ? 1 : p1.value() == p2.value() ? 0 : -1;
            }
            if (p1 != null) {
                return 1;
            }
            if (p2 != null) {
                return -1;
            }
            return 0;
        }
    }
}
