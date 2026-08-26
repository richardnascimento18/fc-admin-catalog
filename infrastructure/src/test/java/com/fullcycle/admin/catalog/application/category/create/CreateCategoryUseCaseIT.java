package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@IntegrationTest
public class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway gateway;

    @Test
    public void givenAValidCommand_whenCallCreateCategory_thenShouldReturnCategoryId() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;

        Assertions.assertEquals(0, categoryRepository.count());

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final CreateCategoryOutput actualOutput = useCase.execute(aCommand).get();

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final CategoryJpaEntity actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAnInvalidName_whenCallCreateCategory_thenShouldReturnADomainException() {
        final String expectedName = null;
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;
        final String expectedErrorMessage = "'name' should not be null";
        final int expectedErrorCount = 1;

        Assertions.assertEquals(0, categoryRepository.count());

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final Notification notification = useCase.execute(aCommand).getLeft();

        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        Assertions.assertEquals(0, categoryRepository.count());

        Mockito.verify(gateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCreateCategory_thenShouldReturnInactiveCategoryId() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = false;

        Assertions.assertEquals(0, categoryRepository.count());

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final CreateCategoryOutput actualOutput = useCase.execute(aCommand).get();

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        final CategoryJpaEntity actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;
        final String expectedErrorMessage = "Gateway error";
        final int expectedErrorCount = 1;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.doThrow(new IllegalStateException(expectedErrorMessage)).when(gateway).create(Mockito.any());

        final Notification notification = useCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(notification);
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
    }
}
