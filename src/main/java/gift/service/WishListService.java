package gift.service;

import gift.dto.response.WishProductResponse;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.ProductNotFoundException;
import gift.exception.WishAlreadyExistsException;
import gift.exception.WishNotFoundException;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WishListService {

    private final WishRepository wishListRepository;
    private final ProductRepository productRepository;

    public WishListService(WishRepository wishListRepository, ProductRepository productRepository) {
        this.wishListRepository = wishListRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addProductToWishList(Long memberId, Long productId, int amount) {
        Optional<Wish> existingWish = wishListRepository.findByMemberIdAndProductId(memberId, productId);
        if (existingWish.isPresent()) {
            throw new WishAlreadyExistsException(existingWish.get());
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        Wish wish = new Wish(memberId, amount, product);
        wishListRepository.save(wish);
    }

    @Transactional
    public void deleteProductInWishList(Long memberId, Long productId) {
        Wish wish = wishListRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(WishNotFoundException::new);
        wishListRepository.delete(wish);
    }

    @Transactional
    public void updateWishProductAmount(Long memberId, Long productId, int amount) {
        Wish wish = wishListRepository.findByMemberIdAndProductId(memberId, productId)
                .orElseThrow(WishNotFoundException::new);
        wish.setAmount(amount);
    }

    public List<WishProductResponse> getWishProductsByMemberId(Long memberId) {
        return wishListRepository.findAllByMemberIdWithProduct(memberId)
                .stream()
                .map(wish -> new WishProductResponse(wish.getProduct().getId(), wish.getProduct().getName(), wish.getProduct().getPrice(), wish.getProduct().getImageUrl(), wish.getAmount()))
                .toList();
    }

}
