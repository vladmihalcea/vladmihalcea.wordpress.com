package com.vladmihalcea.mongo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * BigDecimalToDoubleConverter - BigDecimal To Double Converter
 *
 * @author Vlad Mihalcea
 */
@Component
public class BigDecimalToDoubleConverter implements Converter<BigDecimal, Double> {

    @Override
    public Double convert(BigDecimal source) {
        return source.doubleValue();
    }
}
