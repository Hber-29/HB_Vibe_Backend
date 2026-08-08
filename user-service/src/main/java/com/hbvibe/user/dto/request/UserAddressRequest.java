package com.hbvibe.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên người nhận không được vượt quá 100 ký tự")
    String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không đúng định dạng")
    String receiverPhone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100, message = "Tên Tỉnh/Thành phố không hợp lệ")
    String cityProvince;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Tên Quận/Huyện không hợp lệ")
    String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 100, message = "Tên Phường/Xã không hợp lệ")
    String ward;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    String streetAddress;

}
