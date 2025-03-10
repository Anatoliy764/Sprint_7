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
        Collections.sort(copy, (f1, f2) -> {
            Order o1 = f1.getAnnotation(Order.class);
            Order o2 = f2.getAnnotation(Order.class);

            if (o1 == null && o2 == null) {
                return 0;
            } else if(o1 != null && o2 == null) {
                return -1;
            } else if(o1 == null && o2 != null) {
                return 1;
            } else {
                return Comparator.comparingInt(Order::value).compare(o1, o2);
            }
        });
        return copy;
    }
}
