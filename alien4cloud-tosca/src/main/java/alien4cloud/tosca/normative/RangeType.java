package alien4cloud.tosca.normative;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeType implements IPropertyType<Range> {

    private static final Pattern RANGE_PATTERN = Pattern.compile("[" + ScalarType.SCALAR_UNIT_PATTERN + "," + ScalarType.SCALAR_UNIT_PATTERN + "]");

    public static final String NAME = "range";

    @Override
    public Range parse(String text) throws InvalidPropertyValueException {
        if (StringUtils.isEmpty(text)) {
            throw new InvalidPropertyValueException("Could not parse scalar from value " + text + " as the text is empty");
        }
        Matcher matcher = ScalarType.SCALAR_UNIT_PATTERN.matcher(text);
        if (matcher.matches()) {
            String valueText = matcher.group(1);
            String unitText = matcher.group(2);
            try {
                return null;
            } catch (IllegalArgumentException e) {
                throw new InvalidPropertyValueException("Could not parse scalar from value " + text + " as the text is empty");
            }
        } else {
            throw new InvalidPropertyValueException("Could not parse scalar from value " + text + " as it does not match pattern " + ScalarType.SCALAR_UNIT_PATTERN);
        }
    }

    @Override
    public String print(Range value) {
        return String.valueOf(value);
    }

    @Override
    public String getTypeName() {
        return NAME;
    }
}
