package team9.ddang.chat.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;
// 명세용 응답 클래스
@Schema(description = "정렬 정보")
public class SortResponse {

    @Schema(description = "정렬이 비었는지 여부", example = "false")
    private boolean empty;

    @Schema(description = "정렬된 상태", example = "true")
    private boolean sorted;

    @Schema(description = "정렬되지 않은 상태", example = "false")
    private boolean unsorted;

    public SortResponse(Sort sort) {
        this.empty = sort.isEmpty();
        this.sorted = sort.isSorted();
        this.unsorted = sort.isUnsorted();
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isSorted() {
        return sorted;
    }

    public boolean isUnsorted() {
        return unsorted;
    }
}