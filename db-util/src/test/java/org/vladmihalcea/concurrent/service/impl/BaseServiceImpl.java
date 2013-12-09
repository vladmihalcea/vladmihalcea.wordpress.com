package org.vladmihalcea.concurrent.service.impl;

import org.springframework.stereotype.Service;
import org.vladmihalcea.concurrent.service.BaseService;

/**
 * ProductServiceImpl - ProductService Impl
 *
 * @author Vlad Mihalcea
 */
@Service
public class BaseServiceImpl implements BaseService {

    private volatile int calls = 0;

    protected void incrementCalls() {
        calls++;
    }

    @Override
    public int getRegisteredCalls() {
        return calls;
    }
}
