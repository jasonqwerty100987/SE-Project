import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;


class TestOpenALPR {

    public static void main(int argc, char argv[], String[] args)
    {
        try
        {
            String secret_key = "sk_aeaf16bdbe49c343c5404ae2";

            // Read image file to byte array
            Path path = Paths.get(argv[1]);
            // image file location : need to be revised
            byte[] data = Files.readAllBytes(path);

            // Encode file bytes to base64
            byte[] encoded = Base64.getEncoder().encode(data);

            // Setup the HTTPS connection to api.openalpr.com
            URL url = new URL("https://api.openalpr.com/v2/recognize_bytes?recognize_vehicle=1&country=us&secret_key=" + secret_key);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setFixedLengthStreamingMode(encoded.length);
            http.setDoOutput(true);

            // Send our Base64 content over the stream
            try(OutputStream os = http.getOutputStream()) {
                os.write(encoded);
            }

            int status_code = http.getResponseCode();
            if (status_code == 200)
            {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(
                                        http.getInputStream()));
                String json_content = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    json_content += inputLine;
                in.close();

                System.out.println(json_content);
            }
            else
            {
                System.out.println("Got non-200 response: " + status_code);
            }


        }
        catch (MalformedURLException e)
        {
            System.out.println("Bad URL");
        }
        catch (IOException e)
        {
            System.out.println("Failed to open connection");
        }

    }
}