package airdnb.docs.common;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import airdnb.docs.RestDocsSupport;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;

public class CommonResponseDocs extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CommonController();
    }

    @DisplayName("공통 응답 형식")
    @Test
    void test() throws Exception {
        // then
        mockMvc.perform(get("/docs"))
                .andExpect(status().isOk())
                .andDo(document("common-api-response",   // generated-snippets 에 생기는 폴더명
                        customResponseSnippet(
                                "common-response",  // test-resources-org-springframework-restdocs-templates 의 snippet 파일명과 매칭
                                null,
                                attributes(key("title").value("Common Response")), // snippet 양식에 mustache 속성 추가
                                fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                                subsectionWithPath("data").type(JsonFieldType.VARIES).description("응답 데이터")
                        )
                ));

    }

    public static CustomResponseFieldsSnippet customResponseSnippet(
            String type,
            PayloadSubsectionExtractor<?> subsectionExtractor,
            Map<String, Object> attributes,
            FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(
                type,
                Arrays.asList(descriptors),
                attributes,
                true,
                subsectionExtractor
        );
    }
}
