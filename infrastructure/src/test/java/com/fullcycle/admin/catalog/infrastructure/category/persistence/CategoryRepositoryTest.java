package com.fullcycle.admin.catalog.infrastructure.category.persistence;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.infrastructure.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLGatewayTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenANullName_whenSave_thenShouldReturnError() {
        final String expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.name";
        final String expectedPropertyName = "name";
        final Category aCategory = Category.newCategory("movies", "most watched category", true);

        CategoryJpaEntity anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setName(null);

        final DataIntegrityViolationException actualException = Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));

        final PropertyValueException actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    public void givenANullCreatedAt_whenSave_thenShouldReturnError() {
        final String expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final String expectedPropertyName = "createdAt";
        final Category aCategory = Category.newCategory("movies", "most watched category", true);

        CategoryJpaEntity anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setCreatedAt(null);

        final DataIntegrityViolationException actualException = Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));

        final PropertyValueException actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    public void givenANullUpdatedAt_whenSave_thenShouldReturnError() {
        final String expectedMessage = "not-null property references a null or transient value : com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final String expectedPropertyName = "updatedAt";
        final Category aCategory = Category.newCategory("movies", "most watched category", true);

        CategoryJpaEntity anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setUpdatedAt(null);

        final DataIntegrityViolationException actualException = Assertions.assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(anEntity));

        final PropertyValueException actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedMessage, actualCause.getMessage());
    }
}
