package ee.pw.testowanie1.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateDTO {
    @NotBlank
    @Size(min = 1, max = 1024, message = "Content must be between 1 and 1024 characters")
    private String content;

    @NotBlank
    private UUID userId;
}
