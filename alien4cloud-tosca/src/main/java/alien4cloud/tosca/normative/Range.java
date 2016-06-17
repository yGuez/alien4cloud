package alien4cloud.tosca.normative;

public class Range <T extends Comparable>  {

    private IComparablePropertyType<T> min;

    private IComparablePropertyType<T> max;

    public Range(IComparablePropertyType<T> min, IComparablePropertyType<T> max) {
        this.min = min;
        this.max = max;
    }
}
