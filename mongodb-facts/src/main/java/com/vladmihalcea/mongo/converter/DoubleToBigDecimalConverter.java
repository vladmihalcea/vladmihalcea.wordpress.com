package com.vladmihalcea.mongo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DoubleToBigDecimalConverter - Double To BigDecimal Converter
 *
 * @author Vlad Mihalcea
 */
@Component
public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {

    @Override
    public BigDecimal convert(Double source) {
        return new BigDecimal(source);
    }
}
