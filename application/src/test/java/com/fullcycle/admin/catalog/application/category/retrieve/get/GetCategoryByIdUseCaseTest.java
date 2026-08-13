package com.fullcycle.admin.catalog.application.category.retrieve.get;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseTest {
    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenGetCategoryById_thenShouldReturnCategory() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;

        final Category aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final CategoryID expectedId = aCategory.getId();

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        final CategoryOutput actualCategory = Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    public void givenAnInvalidId_whenGetCategoryById_thenShouldReturnNotFound() {
        final int expectedErrorCount = 1;
        final CategoryID expectedId = CategoryID.from("123");
        final String expectedErrorMessage = "Category with ID %s was not found".formatted(expectedId.getValue());

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.empty());

        final DomainException actualException = Assertions.assertThrows(DomainException.class, () -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenAValidId_whenGatewayReturnsRandomException_thenShouldReturnException() {
        final String expectedErrorMessage = "Gateway error";
        final CategoryID expectedId = CategoryID.from("123");

        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenThrow(new IllegalStateException(expectedErrorMessage));

        final IllegalStateException actualException = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
