package team9.ddang.chat.service.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

// 명세용 응답 클래스
@Schema(description = "페이징된 데이터 응답")
public class SliceResponse<T> {

    @Schema(description = "현재 페이지 번호", example = "0")
    private int number;

    @Schema(description = "페이지 크기", example = "10")
    private int size;

    @Schema(description = "현재 페이지의 데이터 수", example = "1")
    private int numberOfElements;

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    @Schema(description = "페이지가 비었는지 여부", example = "false")
    private boolean empty;

    @Schema(description = "데이터 목록")
    private List<ChatResponse> content;

    @Schema(description = "정렬 정보", example = "List<ChatResponse>")
    private SortResponse sort;

    @Schema(description = "페이지 정보")
    private PageableResponse pageable;

    public SliceResponse(org.springframework.data.domain.Slice<ChatResponse> slice) {
        this.number = slice.getNumber();
        this.size = slice.getSize();
        this.numberOfElements = slice.getNumberOfElements();
        this.first = slice.isFirst();
        this.last = slice.isLast();
        this.empty = slice.isEmpty();
        this.content = slice.getContent();
        this.sort = new SortResponse(slice.getSort());
        this.pageable = new PageableResponse(slice.getPageable());
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public List<ChatResponse> getContent() {
        return content;
    }

    public SortResponse getSort() {
        return sort;
    }

    public PageableResponse getPageable() {
        return pageable;
    }
}