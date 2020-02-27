package alektas.pocketbasket.ads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.domain.entities.ShowcaseItemModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

class AdManagerTest {
    private AdManager mAdManager;
    private NativeAdWrapper mAd;
    private ShowcaseItemModel mProduct;

    @BeforeEach
    void setUp() {
        mAd = mock(NativeAdWrapper.class);
        mProduct = mock(ShowcaseItemModel.class);
        mAdManager = mock(AdManager.class);
        doCallRealMethod().when(mAdManager).combine(anyList(), anyList(), anyInt());
    }

    @Test
    void combine_emptyProductAndAdList_isNoAds() {
        List<Object> combined = combine(0, 0, 8);
        int adCount = getAdCount(combined);

        assertEquals(0, adCount);
    }

    @Test
    void combine_emptyAdList_isNoAds() {
        List<Object> combined = combine(5, 0, 8);
        int adCount = getAdCount(combined);

        assertEquals(0, adCount);
    }

    @Test
    void combine_emptyProductList_isNonAd() {
        List<Object> combined = combine(0, 5, 8);
        int adCount = getAdCount(combined);

        assertEquals(0, adCount);
    }

    @Test
    void combine_productCountLesserThanMinimalAdOffset_isSingleAd() {
        List<Object> combined = combine(6, 5, 8);
        int adCount = getAdCount(combined);

        assertEquals(1, adCount);
    }

    @Test
    void combine_productCountEqualsMinimalAdOffset_isTwoAd() {
        List<Object> combined = combine(8, 5, 8);
        int adCount = getAdCount(combined);

        assertEquals(2, adCount);
    }

    @ParameterizedTest(name = "{index}. With product count: {0}")
    @ValueSource(ints = {9, 15, 100})
    void combine_productCountGreaterMinimalAdOffset_isSeveralAds(int productCount) {
        List<Object> combined = combine(productCount, 5, 8);
        int adCount = getAdCount(combined);

        assertTrue(adCount >= 2);
    }

    @ParameterizedTest(name = "{index}. With product count: {0}")
    @ValueSource(ints = {9, 15, 100})
    void combine_productCountGreaterMinimalAdOffset_isAdCountLesserThanMax(int productCount) {
        List<Object> combined = combine(productCount, 5, 8);
        int adCount = getAdCount(combined);

        assertTrue(adCount <= 5);
    }

    private List<Object> combine(int productCount, int adCount, int minAdOffset) {
        List<ShowcaseItemModel> products = getProducts(productCount);
        List<NativeAdWrapper> ads = getAds(adCount);
        return mAdManager.combine(products, ads, minAdOffset);
    }

    private List<ShowcaseItemModel> getProducts(int count) {
        List<ShowcaseItemModel> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(mProduct);
        }
        return products;
    }

    private List<NativeAdWrapper> getAds(int count) {
        List<NativeAdWrapper> ads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ads.add(mAd);
        }
        return ads;
    }

    private int getAdCount(List<Object> combinedList) {
        int adCount = 0;
        for (Object o : combinedList) {
            if (o instanceof NativeAdWrapper) adCount++;
        }
        return adCount;
    }
}