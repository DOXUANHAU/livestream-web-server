package com.example.auth.controller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

@RestController
@RequestMapping("/api/v1")
public class ToxicController {

    private OrtEnvironment env;
    private OrtSession session;

    public ToxicController() {
        try {
            System.out.println("ToxicController constructor start");
            env = OrtEnvironment.getEnvironment();

            // Lấy resource stream
            InputStream modelStream = getClass().getResourceAsStream("/models/toxic_model.onnx");
            if (modelStream == null) {
                throw new FileNotFoundException("Cannot find toxic_model.onnx in resources");
            }

            // Copy sang file tạm
            Path tempFile = Files.createTempFile("toxic_model", ".onnx");
            Files.copy(modelStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            modelStream.close();

            System.out.println("Temporary ONNX model path: " + tempFile.toAbsolutePath());

            // Tạo ONNX session từ file tạm
            session = env.createSession(tempFile.toAbsolutePath().toString(), new OrtSession.SessionOptions());
            System.out.println("ONNX session created successfully");

        } catch (Exception e) {
            System.err.println("Failed to initialize ToxicController: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

   @PostMapping("/predict")
    public Map<String,Object> predict(@RequestBody Map<String,String> request) throws OrtException {
        String text = request.get("text");

        OnnxTensor inputTensor = OnnxTensor.createTensor(env, new String[][]{{text}});
        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("string_input", inputTensor);

        OrtSession.Result result = session.run(inputs);

        long[] output = (long[]) result.get(0).getValue(); 

        long prediction = output[0];
        
        String label = (prediction == 1) ? "TOXIC" : "NOT TOXIC";

        Map<String,Object> resp = new HashMap<>();
        resp.put("score", (float) prediction);
        resp.put("label", label);
        return resp;
    }
}
