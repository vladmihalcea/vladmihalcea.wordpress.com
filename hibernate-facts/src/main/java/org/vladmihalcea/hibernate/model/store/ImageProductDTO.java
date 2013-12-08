package org.vladmihalcea.hibernate.model.store;

/**
 * ImageProductDTO - ImageProduct DTO
 *
 * @author Vlad Mihalcea
 */
public class ImageProductDTO {

    private final String imageName;
    private final String productName;

    public ImageProductDTO(String imageName, String productName) {
        this.imageName = imageName;
        this.productName = productName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getProductName() {
        return productName;
    }
}
