package org.spingarda.features;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ivan on 20/11/2016.
 */
@Service
public class MathUtils {

    public Double calcAvg(List<Double> list) {
        try {
            return list.stream().mapToDouble(d -> d).average().getAsDouble();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Double calcVariation(List<Double> list) {
        Double avg = calcAvg(list);
        Double variance = list.stream().reduce(0.0, (acc, val) -> Math.pow(val - avg, 2) + acc)
                / (list.size() - 1);
        Double deviation = Math.sqrt(variance);
        return deviation / avg;
    }
}
