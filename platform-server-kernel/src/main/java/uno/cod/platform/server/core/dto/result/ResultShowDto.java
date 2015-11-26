package uno.cod.platform.server.core.dto.result;


import org.springframework.beans.BeanUtils;
import uno.cod.platform.server.core.domain.Result;

public class ResultShowDto {
    private Long id;

    public ResultShowDto(Result result){
        BeanUtils.copyProperties(result, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
