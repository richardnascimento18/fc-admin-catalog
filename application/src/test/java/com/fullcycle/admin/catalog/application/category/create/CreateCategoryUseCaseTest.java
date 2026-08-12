package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {
    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;

    @Mock
    private CategoryGateway gateway;

    @Test
    public void givenAValidCommand_whenCallCreateCategory_thenShouldReturnCategoryId() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(gateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final CreateCategoryOutput actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(gateway, Mockito.times(1))
                .create(Mockito.argThat(aCategory ->
                         Objects.equals(aCategory.getName(), expectedName)
                                && Objects.equals(aCategory.getDescription(), expectedDescription)
                                && Objects.equals(aCategory.isActive(), expectedIsActive)
                                && Objects.nonNull(aCategory.getId())
                                && Objects.nonNull(aCategory.getCreatedAt())
                                && Objects.nonNull(aCategory.getUpdatedAt())
                                && Objects.isNull(aCategory.getDeletedAt())

                ));
    }

    @Test
    public void givenAnInvalidName_whenCallCreateCategory_thenShouldReturnADomainException() {
        final String expectedName = null;
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;
        final String expectedErrorMessage = "'name' should not be null";
        final int expectedErrorCount = 1;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final Notification notification = useCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(notification);
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());

        Mockito.verify(gateway, Mockito.times(0)).create(Mockito.any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallCreateCategory_thenShouldReturnInactiveCategoryId() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = false;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(gateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final CreateCategoryOutput actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(gateway, Mockito.times(1))
                .create(Mockito.argThat(aCategory ->
                        Objects.equals(aCategory.getName(), expectedName)
                                && Objects.equals(aCategory.getDescription(), expectedDescription)
                                && Objects.equals(aCategory.isActive(), expectedIsActive)
                                && Objects.nonNull(aCategory.getId())
                                && Objects.nonNull(aCategory.getCreatedAt())
                                && Objects.nonNull(aCategory.getUpdatedAt())
                                && Objects.nonNull(aCategory.getDeletedAt())

                ));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;
        final String expectedErrorMessage = "Gateway error";
        final int expectedErrorCount = 1;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(gateway.create(Mockito.any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final Notification notification = useCase.execute(aCommand).getLeft();

        Assertions.assertNotNull(notification);
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());

        Mockito.verify(gateway, Mockito.times(1))
                .create(Mockito.argThat(aCategory ->
                        Objects.equals(aCategory.getName(), expectedName)
                                && Objects.equals(aCategory.getDescription(), expectedDescription)
                                && Objects.equals(aCategory.isActive(), expectedIsActive)
                                && Objects.nonNull(aCategory.getId())
                                && Objects.nonNull(aCategory.getCreatedAt())
                                && Objects.nonNull(aCategory.getUpdatedAt())
                                && Objects.isNull(aCategory.getDeletedAt())

                ));
    }
}
