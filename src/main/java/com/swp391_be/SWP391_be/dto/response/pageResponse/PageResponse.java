package com.swp391_be.SWP391_be.dto.response.pageResponse;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {

  private List<T> data;
  private int page;
  private int pageSize;
  private long total;
  private long totalPages;
  private long count;

  public PageResponse(
      List<T> data,
      int page,
      int pageSize,
      long total,
      long totalPages,
      long count) {
    this.data = data;
    this.page = page;
    this.pageSize = pageSize;
    this.total = total;
    this.totalPages = totalPages;
    this.count = count;
  }

  public static <T, R> PageResponse<R> fromPage(
      Page<T> page,
      Function<T, R> mapper) {
    List<R> data = page
        .getContent()
        .stream()
        .map(mapper)
        .toList();

    return new PageResponse<>(
        data,
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        data.size());
  }

}
