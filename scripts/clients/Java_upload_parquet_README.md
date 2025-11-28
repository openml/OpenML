Java example: upload existing parquet file to OpenML

This is a minimal Java example that demonstrates how to upload an existing
parquet file to the OpenML `/data` endpoint using an HTTP multipart POST.

It uses `OkHttp` as HTTP client. The example assumes you already have a
`dataset.parquet` file on disk.

Maven dependency (add to `pom.xml`):

```xml
<dependency>
  <groupId>com.squareup.okhttp3</groupId>
  <artifactId>okhttp</artifactId>
  <version>4.11.0</version>
</dependency>
```

Example snippet:

```java
OkHttpClient client = new OkHttpClient();
String apiUrl = "https://openml.example.org/data/v1";
String apiKey = "YOUR_API_KEY";

File parquetFile = new File("dataset.parquet");
String descriptionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    + "<oml:data xmlns:oml=\"http://openml.org/openml\">"
    + "<oml:name>my_dataset</oml:name>"
    + "<oml:description>Uploaded via Java</oml:description>"
    + "<oml:version>1</oml:version>"
    + "<oml:format>parquet</oml:format>"
    + "</oml:data>";

RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
    .addFormDataPart("api_key", apiKey)
    .addFormDataPart("description", "description.xml",
        RequestBody.create(descriptionXml.getBytes(StandardCharsets.UTF_8), MediaType.parse("application/xml")))
    .addFormDataPart("dataset", parquetFile.getName(),
        RequestBody.create(parquetFile, MediaType.parse("application/octet-stream")))
    .build();

Request request = new Request.Builder().url(apiUrl).post(requestBody).build();
try (Response response = client.newCall(request).execute()) {
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
    System.out.println(response.body().string());
}
```

Notes:
- This snippet does not perform DataFrame->parquet conversion; it assumes you have a parquet file.
- For DataFrame conversion in Java, consider using Apache Arrow Java or parquet-mr libraries.
