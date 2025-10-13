package com.meetup.hereandnow.core.config.ncp;

import com.meetup.hereandnow.core.config.swagger.ExternalApiContributor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NcpSwaggerConfiguration implements ExternalApiContributor {

    private static final String REQUEST_SCHEMA_NAME = "PresignedUrlRequest";
    private static final String API_TAG = "ObjectStorage";

    @Value("${springdoc.ncp-cloud-functions-url}")
    private String ncpUrl;

    @Override
    public void contribute(OpenAPI openApi) {
        defineRequestSchema(openApi);
        PathItem pathItem = createPathItem();
        Tag apiTag = new Tag()
                .name(API_TAG) // 오퍼레이션에 할당한 태그 이름과 정확히 일치해야 합니다.
                .description("NCP Object storage 관련 컨트롤러");
        openApi.addTagsItem(apiTag);
        openApi.path("/", pathItem);
    }

    // 요청 스키마 정의
    private void defineRequestSchema(OpenAPI openApi) {
        Schema<String> dirnameSchema = new Schema<>();
        dirnameSchema.setType("string");
        dirnameSchema.setDescription("저장할 디렉토리 경로");
        dirnameSchema.setDefault("images");

        Schema<String> extensionSchema = new Schema<>();
        extensionSchema.setType("string");
        extensionSchema.setDescription("파일 확장자");
        extensionSchema.setDefault("jpeg");

        Schema<Object> requestSchema = new Schema<>();
        requestSchema.setType("object");
        requestSchema.addProperty("dirname", dirnameSchema);
        requestSchema.addProperty("extension", extensionSchema);

        openApi.getComponents().addSchemas(REQUEST_SCHEMA_NAME, requestSchema);
    }

    // path 정의
    private PathItem createPathItem() {
        Schema<Boolean> booleanSchema = new Schema<>();
        booleanSchema.setType("boolean");
        booleanSchema.setDefault(true);

        Parameter resultParameter = new Parameter()
                .in("query")
                .name("result")
                .description("무조건 true로 설정합니다. (NCP에서 결과값 받아오기 위함)")
                .required(true)
                .schema(booleanSchema);
        Parameter blockingParameter = new Parameter()
                .in("query")
                .name("blocking")
                .description("무조건 true로 설정합니다. (NCP에서 결과값 받아오기 위함)")
                .required(true)
                .schema(booleanSchema);

        Operation operation = new Operation()
                .summary("Presigned URL 생성 (Serverless)")
                .description("파일 업로드를 위한 Presigned URL을 요청하는 API입니다.<br>" +
                        "dirname에는 파일이 저장될 경로(프로필 사진이라면 profile_img 등)를 지정합니다.<br>" +
                        "extension에는 요청된 파일의 확장자를 정확히 지정합니다.")
                .tags(List.of(API_TAG))
                .servers(List.of(new Server().url(ncpUrl).description("NCP cloud functions API")))
                .addParametersItem(resultParameter)
                .addParametersItem(blockingParameter)
                .requestBody(new RequestBody()
                        .required(true)
                        .content(new Content()
                                .addMediaType("application/json", new MediaType().schema(
                                        new Schema<>().$ref("#/components/schemas/" + REQUEST_SCHEMA_NAME)
                                ))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("Presigned URL 생성 성공")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType().schema(
                                                new Schema<>().$ref("#/components/schemas/RestResponseString")
                                        )))));

        return new PathItem().post(operation);
    }
}
