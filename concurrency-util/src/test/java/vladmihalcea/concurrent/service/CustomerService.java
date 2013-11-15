package vladmihalcea.concurrent.service;

/**
 * CustomerService - Customer Service
 *
 * @author Vlad Mihalcea
 */
public interface CustomerService {

    void saveCustomer();

    int getRegisteredCalls();
}
