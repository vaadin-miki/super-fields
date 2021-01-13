package org.vaadin.miki.superfields.numbers;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

final class BigDecimalFormatParser {

    static BigDecimal parseFromString(String rawValue, DecimalFormat format) throws ParseException {
        // no idea how decimal formats work, but just in case the instance is shared across many objects...
        boolean oldParse = format.isParseBigDecimal();
        format.setParseBigDecimal(true);
        BigDecimal result = (BigDecimal) format.parse(rawValue);
        // ...here it returns to the prior state after it was used
        format.setParseBigDecimal(oldParse);
        return result;
    }


    private BigDecimalFormatParser() {
        // no instances allowed
    }
}
