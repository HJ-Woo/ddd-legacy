package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kitchenpos.application.ProductServiceTest.상품만들기;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {
    private MenuService menuService;
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private Menu menu;
    private MenuGroup menuGroup;
    private List<MenuProduct> menuProducts;


    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        menuGroup = 메뉴그룹만들기(menuGroupRepository);
        menuProducts = 메뉴상품들만들기(productRepository);
        menu = new Menu();
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000L));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        final Menu saved = 메뉴등록(menu);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(menu.getName()),
                () -> assertThat(saved.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(saved.getMenuProducts()).hasSize(2),
                () -> assertThat(saved.getMenuGroup()).isEqualTo(menuGroup),
                () -> assertThat(saved.isDisplayed()).isTrue()
        );
    }

    @DisplayName("메뉴의 가격은 0 이상이어야하고, 상품들의 가격의 총합을 넘을 수 없다.")
    @ValueSource(strings = {"-1000", "1000000"})
    @NullSource
    @ParameterizedTest
    void create(BigDecimal price) {
        menu.setPrice(price);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 비어있거나 욕설이 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        menu.setName(name);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 한 가지 이상이어야한다.")
    @Test
    void create_MenuProduct() {
        menu.setMenuProducts(emptyList());

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 등록된 상품이어야한다.")
    @Test
    void create_MenuProduct_without_Product() {
        menuProducts.add(등록되지않은_메뉴상품만들기());
        menu.setMenuProducts(menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품 수량은 0보다 커야한다.")
    @Test
    void create_MenuProduct_quantity() {
        menuProducts.add(수량이음수인_메뉴상품만들기(productRepository));
        menu.setMenuProducts(menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Menu saved = 메뉴등록(menu);
        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(8_000L));
        final Menu expected = 메뉴가격변경(saved.getId(), request);

        assertAll(
                () -> assertThat(expected.getPrice()).isEqualTo(request.getPrice()),
                () -> assertThat(expected.getId()).isEqualTo(saved.getId()),
                () -> assertThat(expected.getName()).isEqualTo(saved.getName())
        );
    }

    @DisplayName("메뉴의 가격을 변경할 땐 0보다 큰 메뉴상품의 총합을 넘지 않는 값이어야한다.")
    @ValueSource(strings = {"-1000", "1000000"})
    @NullSource
    @ParameterizedTest
    void changePrice(BigDecimal price) {
        final Menu saved = 메뉴등록(menu);
        final Menu request = new Menu();
        request.setPrice(price);

        assertThatThrownBy(() -> 메뉴가격변경(saved.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출할 수 있다.")
    @Test
    void display() {
        menu.setDisplayed(false);
        final Menu saved = 메뉴등록(menu);
        assertThat(saved.isDisplayed()).isFalse();

        final Menu expected = 메뉴노출(saved.getId());
        assertThat(expected.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 노출할땐 가격이 메뉴상품의 가격합보다 커서는 안된다.")
    @Test
    void display_price() {
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(12_000L));
        final Menu saved = menuRepository.save(menu);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 메뉴노출(saved.getId()));
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        final Menu saved = 메뉴등록(menu);

        final Menu expected = menuService.hide(saved.getId());
        assertThat(expected.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 전체조회할 수 있다.")
    @Test
    void findAll(){
        final Menu saved1 = 메뉴만들기(menuRepository, menuGroupRepository, productRepository);
        final Menu saved2 = 메뉴만들기(menuRepository, menuGroupRepository, productRepository);

        assertThat(메뉴전체조회()).containsOnly(saved1, saved2);
    }


    Menu 메뉴등록(Menu menu) {
        return menuService.create(menu);
    }

    Menu 메뉴가격변경(UUID id, Menu menu) {
        return menuService.changePrice(id, menu);
    }

    Menu 메뉴노출(UUID id) {
        return menuService.display(id);
    }

    List<Menu> 메뉴전체조회() {
        return menuService.findAll();
    }

    public static Menu 메뉴만들기(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        MenuGroup menuGroup = 메뉴그룹만들기(menuGroupRepository);
        List<MenuProduct> menuProducts = 메뉴상품들만들기(productRepository);
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000L));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menuRepository.save(menu);
    }

    public static List<MenuProduct> 메뉴상품들만들기(ProductRepository productRepository) {
        final Product product = 상품만들기(productRepository);

        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProduct(product);
        menuProduct1.setProductId(product.getId());
        menuProduct1.setQuantity(1L);

        final MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setProduct(product);
        menuProduct2.setProductId(product.getId());
        menuProduct2.setQuantity(2L);
        return new ArrayList<>(Arrays.asList(menuProduct1, menuProduct2));
    }

    public static MenuGroup 메뉴그룹만들기(MenuGroupRepository menuGroupRepository) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");
        return menuGroupRepository.save(menuGroup);
    }

    public static MenuProduct 등록되지않은_메뉴상품만들기() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1L);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuProduct 수량이음수인_메뉴상품만들기(ProductRepository productRepository) {
        final Product product = 상품만들기(productRepository);
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(-1);
        return menuProduct;
    }
}
