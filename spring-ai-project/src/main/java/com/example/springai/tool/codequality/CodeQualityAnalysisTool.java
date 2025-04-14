package com.example.springai.tool.codequality;

import com.example.springai.mcp.McpToolDefinition;
import com.example.springai.mcp.McpToolParameter;
import com.example.springai.mcp.McpToolResult;
import com.example.springai.mcp.Tool;
import com.example.springai.service.codequality.CodeQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Tool for analyzing code quality.
 */
@Component
public class CodeQualityAnalysisTool implements Tool {

    @Autowired
    private CodeQualityService codeQualityService;

    @Override
    public String getName() {
        return "analyze_code_quality";
    }

    @Override
    public String getDescription() {
        return "Analyzes code quality and generates a PDF report with metrics for style, quality, complexity, and security.";
    }

    @Override
    public McpToolDefinition getDefinition() {
        return new McpToolDefinition(
                getName(),
                getDescription(),
                List.of(
                        new McpToolParameter(
                                "code_content",
                                "string",
                                "The content of the code file to analyze, either as plain text or base64 encoded.",
                                true
                        ),
                        new McpToolParameter(
                                "file_name",
                                "string",
                                "The name of the file being analyzed, including extension (e.g., Main.java).",
                                true
                        ),
                        new McpToolParameter(
                                "is_base64",
                                "boolean",
                                "Whether the code_content is base64 encoded.",
                                false
                        )
                )
        );
    }

    @Override
    public McpToolResult execute(Map<String, Object> parameters) {
        try {
            String codeContent = (String) parameters.get("code_content");
            String fileName = (String) parameters.get("file_name");
            boolean isBase64 = parameters.containsKey("is_base64") && (boolean) parameters.get("is_base64");

            if (isBase64) {
                codeContent = new String(Base64.getDecoder().decode(codeContent));
            }

            Path tempFile = createTempFile(fileName, codeContent);
            
            MultipartFile multipartFile = createMultipartFile(tempFile, fileName);
            
            String reportPath = codeQualityService.analyzeCode(multipartFile);
            
            Files.deleteIfExists(tempFile);
            
            return McpToolResult.success(Map.of(
                    "message", "Code quality analysis completed successfully",
                    "report_path", reportPath,
                    "download_url", "/api/code-quality/report?reportPath=" + reportPath
            ));
        } catch (Exception e) {
            return McpToolResult.failure("Failed to analyze code quality: " + e.getMessage());
        }
    }

    /**
     * Create a temporary file with the given content.
     *
     * @param fileName the name of the file
     * @param content the content of the file
     * @return path to the created file
     * @throws IOException if file operations fail
     */
    private Path createTempFile(String fileName, String content) throws IOException {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "code-quality-analysis");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        
        Path tempFile = tempDir.resolve(fileName);
        Files.write(tempFile, content.getBytes());
        return tempFile;
    }

    /**
     * Create a MultipartFile from a file path.
     *
     * @param filePath path to the file
     * @param fileName name of the file
     * @return MultipartFile instance
     */
    private MultipartFile createMultipartFile(Path filePath, String fileName) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                try {
                    return Files.size(filePath);
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public byte[] getBytes() throws IOException {
                return Files.readAllBytes(filePath);
            }

            @Override
            public java.io.InputStream getInputStream() throws IOException {
                return Files.newInputStream(filePath);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream outputStream = new FileOutputStream(dest)) {
                    outputStream.write(getBytes());
                }
            }
        };
    }
}
