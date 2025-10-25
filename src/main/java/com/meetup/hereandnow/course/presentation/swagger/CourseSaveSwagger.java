package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Course", description = "데이트 코스 관련 컨트롤러")
public interface CourseSaveSwagger {

    @Operation(
            summary = "코스 저장 API - 이미지 업로드를 위한 objectKey를 받아오고 레디스에 코스관련 메타데이터를 저장한다.",
            description = "presigned url을 활용하여 이미지를 업로드 하기 위한 dirname과 메타데이터 식별자인 uuid를 받아옵니다.<br>"
                    + "코스 저장 방법은 다음과 같습니다.<br>"
                    + "1. /course/save를 호출하여 dirname과 uuid 받아오기<br>"
                    + "2. presigned url을 받아오는 API를 호출하고, 해당 url로 이미지 업로드<br>"
                    + "3. /course/{courseUuid}/commit를 호출하여 이미지 object Key 전달 및 DB 업데이트<br>",
            operationId = "/course/save"
    )
    @ApiErrorCode({MemberErrorCode.class})
    ResponseEntity<RestResponse<CourseSaveResponseDto>> courseSave(
            @RequestBody CourseSaveDto courseSaveDto
    );

    @Operation(
            summary = "코스 저장 API - 이미지 업로드 이후 DB에 코스 데이터를 저장한다.",
            description = "presigned url을 통해 업로드한 이미지의 object key를 전달하고 DB에 코스 데이터를 저장합니다.<br>"
                    + "/course/save에서 받은 courseUuid를 통해 메타데이터를 찾고, S3 이미지 존재 여부 확인 후 DB에 저장합니다.",
            operationId = "/course/{courseUuid}/commit"
    )
    @ApiErrorCode({CourseErrorCode.class, PinErrorCode.class})
    ResponseEntity<RestResponse<CommitSaveCourseResponseDto>> commitSaveCourse(
            @PathVariable String courseUuid,
            @RequestBody CommitSaveCourseRequestDto commitSaveCourseRequestDto
    );
}
