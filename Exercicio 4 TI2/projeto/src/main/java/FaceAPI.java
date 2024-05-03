import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class FaceAPI{
    private static final String subscriptionKey = "bba57bdae0d54fdf85f7933316f1ca96";
    private static final String endpoint = "https://<sua-região>.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceAttributes=emotion";

    public static void main(String[] args) {
        try {
            File imageFile = new File("caminho/imagem.jpg");
            byte[] imgBytes = Files.readAllBytes(imageFile.toPath());
            String emotion = detectEmotion(imgBytes);
            System.out.println("Sentimento detectado: " + emotion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String detectEmotion(byte[] imgBytes) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(imgBytes);
            os.flush();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.toString());
            JsonNode emotionNode = jsonNode.get(0).get("faceAttributes").get("emotion");
            String maxEmotion = "";
            double maxScore = 0;
            for (JsonNode entry : emotionNode) {
                double score = entry.asDouble();
                if (score > maxScore) {
                    maxScore = score;
                    maxEmotion = entry.fieldNames().next();
                }
            }
            return maxEmotion;
        }
    }
}
