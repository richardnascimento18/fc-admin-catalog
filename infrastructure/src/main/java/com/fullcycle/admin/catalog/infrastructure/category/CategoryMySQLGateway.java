package com.fullcycle.admin.catalog.infrastructure.category;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.Optional;

@Repository
public class CategoryMySQLGateway implements CategoryGateway {
    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(final CategoryRepository aCategoryRepository) {
        this.categoryRepository = aCategoryRepository;
    }

    @Override
    public Category create(final Category aCategory) {
        return this.categoryRepository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    @Override
    public void deleteById(CategoryID anId) {
        final String actualIdValue = anId.getValue();
        if (this.categoryRepository.existsById(actualIdValue)) {
            this.categoryRepository.deleteById(actualIdValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID anId) {
        return this.categoryRepository.findById(anId.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category aCategory) {
        return this.categoryRepository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
        // Pagination
        final PageRequest page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        // Dynamic Search (by category name or description)
        final Specification<CategoryJpaEntity> specifications = Optional.ofNullable(aQuery.terms())
                .filter(term -> !term.isBlank())
                .map(term -> SpecificationUtils
                        .<CategoryJpaEntity>like("name", term)
                        .or(SpecificationUtils.like("description", term))
                )
                .orElse(null);

        final Page<CategoryJpaEntity> pageResult = this.categoryRepository.findAll(Specification.where(specifications), page);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }
}
