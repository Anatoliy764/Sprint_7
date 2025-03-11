package kz.yandex.practicum.qa.scooter;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderedRunner extends BlockJUnit4ClassRunner {
    public OrderedRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> list = super.computeTestMethods();
        List<FrameworkMethod> copy = new ArrayList<>(list);

        // Sort the test methods
        copy.sort((f1, f2) -> {
            // Get the annotations
            Order o1 = getOrderAnnotation(f1);
            Order o2 = getOrderAnnotation(f2);

            // Check if methods are annotated with @First or @Last
            boolean isFirst1 = f1.getAnnotation(First.class) != null;
            boolean isFirst2 = f2.getAnnotation(First.class) != null;
            boolean isLast1 = f1.getAnnotation(Last.class) != null;
            boolean isLast2 = f2.getAnnotation(Last.class) != null;

            // Handle @First
            if (isFirst1 && !isFirst2) {
                return -1; // f1 is @First, so it comes before f2
            } else if (!isFirst1 && isFirst2) {
                return 1; // f2 is @First, so it comes before f1
            }

            // Handle @Last
            if (isLast1 && !isLast2) {
                return 1; // f1 is @Last, so it comes after f2
            } else if (!isLast1 && isLast2) {
                return -1; // f2 is @Last, so it comes after f1
            }

            // If neither are @First/@Last, fall back to @Order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 != null && o2 == null) {
                return -1;
            } else if (o1 == null && o2 != null) {
                return 1;
            } else {
                return Integer.compare(o1.value(), o2.value());
            }
        });

        return copy;
    }

    /**
     * Helper method to retrieve the `Order` annotation, even if the method
     * is annotated with `@First` or `@Last`, which themselves use `@Order`.
     */
    private Order getOrderAnnotation(FrameworkMethod method) {
        Order order = method.getAnnotation(Order.class);
        if (order == null) {
            // Check for meta-annotations like @First and @Last
            First first = method.getAnnotation(First.class);
            if (first != null) {
                return First.class.getAnnotation(Order.class);
            }
            Last last = method.getAnnotation(Last.class);
            if (last != null) {
                return Last.class.getAnnotation(Order.class);
            }
        }
        return order;
    }

}
