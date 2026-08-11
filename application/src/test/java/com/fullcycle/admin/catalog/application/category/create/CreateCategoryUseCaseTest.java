package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class CreateCategoryUseCaseTest {
    @Test
    public void givenAValidCommand_whenCallCreateCategory_thenShouldReturnCategoryId() {
        final String expectedName = "Movies";
        final String expectedDescription = "This is a movies category";
        final boolean expectedIsActive = true;

        final CreateCategoryCommand aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final CategoryGateway gateway = Mockito.mock(CategoryGateway.class);
        Mockito.when(gateway.create(Mockito.any())).thenAnswer(returnsFirstArg());

        final CreateCategoryUseCase useCase = new DefaultCreateCategoryUseCase(gateway);

        final CreateCategoryOutput actualOutput = useCase.execute(aCommand);

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
}
