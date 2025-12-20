package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/*
DTO -> DATA TRANSFER OBJECT, SEBUAH JSON YANG BAKALAN KITA PASSING KE FRONTEND DAN SEBALIKNYA
KALO BISA JGN KEBANYAKAN REUSE DTO YG KEBETULAN STRUKTURNYA SAMA
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterDto implements Serializable {
    @JsonProperty("name") // NANTI DI JSON NAMA INI YG BAKALAN DITAMPILIN
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("dob")
    @JsonFormat(pattern = "dd/MM/yyyy") // INI BUAT GANTI PATTERNT DARI TIMESTAMP -> KALO MW STRUKTUR LAIN BISA COBA NGULIK BUAT NGATUR JAM DLL
    private LocalDate dob;
}
