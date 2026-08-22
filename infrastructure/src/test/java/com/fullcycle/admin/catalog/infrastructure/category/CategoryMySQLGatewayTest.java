package com.fullcycle.admin.catalog.infrastructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_thenShouldReturnANewCategory() {
        final String expectedName = "movies";
        final String expectedDescription = "most watched category";
        final boolean expectedIsActive = true;

        final Category aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        final Category actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final CategoryJpaEntity actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_thenShouldReturnAnUpdatedCategory() {
        final String expectedName = "movies";
        final String expectedDescription = "most watched category";
        final boolean expectedIsActive = true;

        final Category aCategory = Category.newCategory("Film", null, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        final CategoryJpaEntity actualInvalidEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals("Film", actualInvalidEntity.getName());
        Assertions.assertNull(actualInvalidEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualInvalidEntity.isActive());

        final Category updatedCategory = aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);
        final Category actualCategory = categoryGateway.update(updatedCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualCategory.getDeletedAt());

        final CategoryJpaEntity actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    public void givenAPersistedCategory_whenCallsDelete_thenShouldDeleteCategory() {
        final Category aCategory = Category.newCategory("Filmes", "The most watched category", true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidCategoryId_whenCallsDelete_thenReturnNoError() {
        Assertions.assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid_id"));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPersistedCategoryAndAValidCategoryID_whenCallsFindById_thenShouldReturnACategory() {
        final String expectedName = "movies";
        final String expectedDescription = "most watched category";
        final boolean expectedIsActive = true;

        final Category aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        final Category actualCategory = categoryGateway.findById(aCategory.getId()).get();

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAnValidCategoryIDNotStored_whenCallsFindById_thenShouldReturnEmpty() {
        Assertions.assertEquals(0, categoryRepository.count());

        final Optional<Category> actualCategory = categoryGateway.findById(CategoryID.from("empty"));

        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAll_thenShouldReturnPaginatedCategories() {
        final int expectedPage = 0;
        final int expectedPerPage = 1;
        final int expectedTotal = 3;

        final Category moviesCategory = Category.newCategory("Movies", null, true);
        final Category seriesCategory = Category.newCategory("Series", null, true);
        final Category documentariesCategory = Category.newCategory("Documentaries", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(moviesCategory),
                CategoryJpaEntity.from(seriesCategory),
                CategoryJpaEntity.from(documentariesCategory)
                )
        );

        Assertions.assertEquals(3, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final Pagination<Category> actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(documentariesCategory.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenEmptyCategoriesTable_whenCallsFindAll_thenShouldReturnEmptyPage() {
        final int expectedPage = 0;
        final int expectedPerPage = 1;
        final int expectedTotal = 0;

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final Pagination<Category> actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(0, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_thenShouldReturnPage() {
        int expectedPage = 0;
        final int expectedPerPage = 1;
        final int expectedTotal = 3;

        final Category moviesCategory = Category.newCategory("Movies", null, true);
        final Category seriesCategory = Category.newCategory("Series", null, true);
        final Category documentariesCategory = Category.newCategory("Documentaries", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(moviesCategory),
                        CategoryJpaEntity.from(seriesCategory),
                        CategoryJpaEntity.from(documentariesCategory)
                )
        );

        Assertions.assertEquals(3, categoryRepository.count());

        CategorySearchQuery query = new CategorySearchQuery(0, 1, "", "name", "asc");
        Pagination<Category> actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(documentariesCategory.getId(), actualResult.items().get(0).getId());

        // Page 1
        expectedPage = 1;

        query = new CategorySearchQuery(1, 1, "", "name", "asc");
        actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(moviesCategory.getId(), actualResult.items().get(0).getId());

        // Page 2
        expectedPage = 2;

        query = new CategorySearchQuery(2, 1, "", "name", "asc");
        actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(seriesCategory.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndDocAsTerms_whenCallsFindAllAndMatchesTermsWithCategoryName_thenShouldReturnPaginatedCategories() {
        final int expectedPage = 0;
        final int expectedPerPage = 1;
        final int expectedTotal = 1;

        final Category moviesCategory = Category.newCategory("Movies", null, true);
        final Category seriesCategory = Category.newCategory("Series", null, true);
        final Category documentariesCategory = Category.newCategory("Documentaries", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(moviesCategory),
                        CategoryJpaEntity.from(seriesCategory),
                        CategoryJpaEntity.from(documentariesCategory)
                )
        );

        Assertions.assertEquals(3, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "doc", "name", "asc");
        final Pagination<Category> actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(documentariesCategory.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndMostWatchedAsTerms_whenCallsFindAllAndMatchesTermsWithCategoryDescription_thenShouldReturnPaginatedCategories() {
        final int expectedPage = 0;
        final int expectedPerPage = 1;
        final int expectedTotal = 1;

        final Category moviesCategory = Category.newCategory("Movies", "Most watched category", true);
        final Category seriesCategory = Category.newCategory("Series", "A category", true);
        final Category documentariesCategory = Category.newCategory("Documentaries", "Least watched category", true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                        CategoryJpaEntity.from(moviesCategory),
                        CategoryJpaEntity.from(seriesCategory),
                        CategoryJpaEntity.from(documentariesCategory)
                )
        );

        Assertions.assertEquals(3, categoryRepository.count());

        final CategorySearchQuery query = new CategorySearchQuery(0, 1, "MOST WATCHED", "name", "asc");
        final Pagination<Category> actualResult = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(moviesCategory.getId(), actualResult.items().get(0).getId());
    }
}
