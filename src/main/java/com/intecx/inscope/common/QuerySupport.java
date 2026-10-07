package com.intecx.inscope.common;

import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@UtilityClass
public class QuerySupport {

    public <E, R> PaginatedResponse<R> search(
            JpaSpecificationExecutor<E> repository,
            QueryFields fields,
            QueryRequest query,
            Function<? super E, ? extends R> mapper,
            String collectionKey
    ) {
        return search(repository, Specification.unrestricted(), fields, query, mapper, collectionKey);
    }

    public <E, R> PaginatedResponse<R> search(
            JpaSpecificationExecutor<E> repository,
            Specification<E> scope,
            QueryFields fields,
            QueryRequest query,
            Function<? super E, ? extends R> mapper,
            String collectionKey
    ) {
        return searchWithBatchMapping(
                repository,
                scope,
                fields,
                query,
                entities -> entities.stream().<R>map(mapper).toList(),
                collectionKey
        );
    }

    public <E, R> PaginatedResponse<R> searchWithBatchMapping(
            JpaSpecificationExecutor<E> repository,
            Specification<E> scope,
            QueryFields fields,
            QueryRequest query,
            Function<List<E>, List<R>> mapper,
            String collectionKey
    ) {
        PaginationRequest pagination = query.pagination();
        Sort sort = fields.sort(pagination);
        Specification<E> specification = scope.and(FilterSpecifications.from(query.filters(), fields));

        Page<E> page = findPage(repository, specification, pagination.pageNumber(), pagination.pageSize(), sort);
        if (page.isEmpty() && page.getTotalElements() > 0) {
            page = findPage(repository, specification, page.getTotalPages() - 1, pagination.pageSize(), sort);
        }

        List<R> items = mapper.apply(page.getContent());
        return PaginatedResponse.of(collectionKey, items, PageMeta.of(page.getTotalElements(), page.getNumber(), page.getSize()));
    }

    private <E> Page<E> findPage(
            JpaSpecificationExecutor<E> repository,
            Specification<E> specification,
            int pageNumber,
            int pageSize,
            Sort sort
    ) {
        return repository.findAll(specification, PageRequest.of(pageNumber, pageSize, sort));
    }
}
