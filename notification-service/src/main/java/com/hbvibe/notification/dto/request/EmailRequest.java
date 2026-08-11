package com.hbvibe.notification.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequest {
    String eventId;
    Sender sender;
    List<Recepient> to;
    @JsonProperty("templateId") // Ép tên trường thành templateId khi chuyển sang JSON gửi đi
    Integer templateCode;
    @JsonProperty("params")    // Ép tên trường thành params cho khớp với Brevo
    Map<String, Object> param;
    String subject;
    String htmlContent;
    Long timestamp;

}
