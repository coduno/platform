package uno.cod.platform.server.core.mapper;

import uno.cod.platform.server.core.domain.Result;
import uno.cod.platform.server.core.dto.result.ResultShowDto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultMapper {
    public static ResultShowDto map(Result result) {
        if(result == null){
            return null;
        }
        List<Long> startTimes = new ArrayList<>();
        if (result.getStartTimes() != null) {
            for (ZonedDateTime time : result.getStartTimes()) {
                if(time != null) {
                    startTimes.add(time.toInstant().toEpochMilli());
                }
            }
        }
        return new ResultShowDto(result.getId(), startTimes);
    }
}
