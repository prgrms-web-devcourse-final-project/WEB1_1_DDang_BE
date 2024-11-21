package team9.ddang.chat.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;

// 명세용 응답 클래스
@Schema(description = "페이지 정보")
public class PageableResponse {

    @Schema(description = "오프셋", example = "0")
    private long offset;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;

    @Schema(description = "페이지 번호", example = "0")
    private int pageNumber;

    @Schema(description = "페이지 여부", example = "true")
    private boolean paged;

    @Schema(description = "페이징되지 않은 여부", example = "false")
    private boolean unpaged;

    @Schema(description = "정렬 정보")
    private SortResponse sort;

    public PageableResponse(Pageable pageable) {
        this.offset = pageable.getOffset();
        this.pageSize = pageable.getPageSize();
        this.pageNumber = pageable.getPageNumber();
        this.paged = pageable.isPaged();
        this.unpaged = pageable.isUnpaged();
        this.sort = new SortResponse(pageable.getSort());
    }

    public long getOffset() {
        return offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isPaged() {
        return paged;
    }

    public boolean isUnpaged() {
        return unpaged;
    }

    public SortResponse getSort() {
        return sort;
    }

    // Getters
}
