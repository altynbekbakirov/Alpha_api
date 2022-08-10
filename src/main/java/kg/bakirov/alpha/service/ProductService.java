package kg.bakirov.alpha.service;

import kg.bakirov.alpha.exception.NotFoundException;
import kg.bakirov.alpha.model.products.*;
import kg.bakirov.alpha.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(int firmNo, int periodNo, String begDate, String endDate, int sourceindex) throws NotFoundException {
        List<Product> productList = productRepository.getProducts(firmNo, periodNo, begDate, endDate, sourceindex);
        if (productList.size() == 0) throw new NotFoundException("No records");
        return productList;
    }

    public List<ProductInventory> getProductsInventory(int firmNo, int periodNo) throws NotFoundException {
        List<ProductInventory> productInventoryList = productRepository.getProductsInventory(firmNo, periodNo);
        if (productInventoryList.size() == 0) throw new NotFoundException("No records");
        return productInventoryList;
    }

    public List<ProductFiche> getProductsFiche(int firmNo, int periodNo, String begDate, String endDate, int sourceindex) throws NotFoundException {
        List<ProductFiche> productFicheList = productRepository.getProductFiche(firmNo, periodNo, begDate, endDate, sourceindex);
        if (productFicheList.size() == 0) throw new NotFoundException("No records");
        return productFicheList;
    }

    public List<ProductPrice> getProductsPrice(int firmNo, int periodNo) throws NotFoundException {
        List<ProductPrice> productPriceList = productRepository.getProductPrice(firmNo, periodNo);
        if (productPriceList.size() == 0) throw new NotFoundException("No records");
        return productPriceList;
    }

    public List<ProductTransaction> getProductTransactions(int firmNo, int periodNo, String begDate, String endDate, int sourceindex) throws NotFoundException {
        List<ProductTransaction> productTransactionList = productRepository.getProductTransactions(firmNo, periodNo, begDate, endDate, sourceindex);
        if (productTransactionList.size() == 0) throw new NotFoundException("No records");
        return productTransactionList;
    }

    public List<ProductTransaction> getProductTransaction(int firmNo, int periodNo, String begDate, String endDate, int sourceindex, String code) throws NotFoundException {
        List<ProductTransaction> productTransactionList = productRepository.getProductTransaction(firmNo, periodNo, begDate, endDate, sourceindex, code);
        if (productTransactionList.size() == 0) throw new NotFoundException("No records");
        return productTransactionList;
    }

    public List<ProductPrices> getProductsPrices(int firmNo, int periodNo, String begDate, String endDate, int sourceindex) throws NotFoundException {
        List<ProductPrices> productPrices = productRepository.getProductsPrices(firmNo, periodNo, begDate, endDate, sourceindex);
        if (productPrices.size() == 0) throw new NotFoundException("No records");
        return productPrices;
    }

    public List<ProductPrices> getProductPrices(int firmNo, int periodNo, String begDate, String endDate, int sourceindex, String code) throws NotFoundException {
        List<ProductPrices> productPrices = productRepository.getProductPrices(firmNo, periodNo, begDate, endDate, sourceindex, code);
        if (productPrices.size() == 0) throw new NotFoundException("No records");
        return productPrices;
    }

}
