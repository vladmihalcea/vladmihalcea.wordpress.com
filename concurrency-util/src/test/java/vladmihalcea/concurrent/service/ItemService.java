package vladmihalcea.concurrent.service;

/**
 * ItemService - Item Service
 *
 * @author Vlad Mihalcea
 */
public interface ItemService extends BaseService {

    void saveItem();

    void saveItems();
}
