package com.example.Database.file;

import com.example.Database.index.Index;
import com.example.Database.index.PropertyIndex;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public final class FileService {
    private static final String FILE_PATH = "src/main/resources/databases";
    private static String DB_DIRECTORY;

    private FileService(){}

    public static JSONObject readSchema(String collectionName) {
        try {
            File schemaFile = getSchemaPath(collectionName);
            System.out.println(schemaFile);
            String schemaString = Files.readString(Paths.get(schemaFile.getAbsolutePath()));
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(schemaString);
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return new JSONObject();
    }


    public static JsonNode readAdminCredentialsFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File(adminJsonFilePath());
            return objectMapper.readTree(jsonFile);
        } catch (IOException e) {
            throw new RuntimeException("Error reading admin credentials from JSON", e);
        }
    }


    public static Map<String, String> readIndexFile(String path) {
        File file = new File(path);
        Map<String, String> indexData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int separatorPosition = line.indexOf(",");
                if (separatorPosition == -1) continue;
                String key = line.substring(0, separatorPosition).trim();
                String value = line.substring(separatorPosition + 1).trim();
                indexData.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file.");
            e.printStackTrace();
        }
        return indexData;
    }


    public static void createDirectoryIfNotExist(File file){
        if (!file.exists()) {
            boolean dirCreated = file.mkdirs();
            if (!dirCreated) {
                System.err.println("Failed to create " + file.getName());
            }
        }
    }

    public static File getRootFile(){
        return new File(FILE_PATH);
    }

    public static File getCollectionFile(String collectionName) {
        return new File(DB_DIRECTORY + "/" + collectionName + ".json");
    }

    public static File getSchemaPath(String collectionName) {
        return new File(DB_DIRECTORY + "/schemas/" + collectionName + ".json");
    }

    public static File getDatabasePath() {
        return new File( DB_DIRECTORY);
    }

    public static String getIndexFilePath(String collectionName) {
        String dirPath = DB_DIRECTORY + "/indexes/" + collectionName + "_indexes";
        createDirectoryIfNotExist(new File(dirPath));
        return dirPath + "/" + collectionName + "_index.txt";
    }

    public static String getPropertyIndexFilePath(String collectionName, String propertyName) {
        String dirPath = DB_DIRECTORY + "/indexes/" + collectionName + "_indexes";
        createDirectoryIfNotExist(new File(dirPath));
        return dirPath + "/" + collectionName + "_" + propertyName + "_property_index.txt";
    }

    public static void setDatabaseDirectory(String dbName) {
        DB_DIRECTORY = FILE_PATH + "/" + dbName;
    }

    public static boolean isIndexFile(String fileName) {
        return fileName.endsWith("_index.txt");
    }

    public static boolean isPropertyIndexFile(String fileName) {
        return fileName.endsWith("_property_index.txt");
    }

    public static String adminJsonFilePath(){
        return "src/main/resources/static/admin.json";
    }

    public static boolean isFileExists(String filePath){
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    public static List<String> getAllKnownDatabases() {
        List<String> databases = new ArrayList<>();
        File databaseRoot = new File(getRootFile().toURI());
        System.out.println("Root directory: " + databaseRoot.getAbsolutePath());
        File[] dbDirectories = databaseRoot.listFiles(File::isDirectory);
        System.out.println(Arrays.toString(dbDirectories) + " database directories");
        if (dbDirectories != null) {
            for (File dbDir : dbDirectories) {
                databases.add(dbDir.getName());
            }
        }

        return databases;
    }

    public static String readFileAsString(File file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray readJsonArrayFile(File file) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            if (file.length() == 0) {
                return new JSONArray();
            }
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.err.println("Error while reading JSON file: " + e.getMessage());
            return null;
        }
    }

    public synchronized static void appendToIndexFile(String path, Object key, String value) {
        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            List<String> lines = Files.readAllLines(filePath);
            boolean keyExists = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts[0].equals(key.toString())) {
                    String[] docIds = parts[1].split(";");
                    List<String> docIdsList = new ArrayList<>(Arrays.asList(docIds));
                    if (!docIdsList.contains(value)) {
                        docIdsList.add(value);
                    }
                    lines.set(i, parts[0] + "," + String.join(";", docIdsList));
                    keyExists = true;
                    break;
                }
            }
            if (!keyExists) {
                lines.add(key + "," + value);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                try (Stream<Path> entries = Files.list(path)) {
                    entries.forEach(entry -> {
                        try {
                            deleteDirectoryRecursively(entry);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
            Files.delete(path);
        }
    }

    public synchronized static void rewriteIndexFile(String collectionName, Index index) {
        File file = new File(getIndexFilePath(collectionName));
        List<Map.Entry<String, String>> allEntries = index.getBPlusTree().getAllEntries();
        if (allEntries.isEmpty()) {
            if (FileService.isFileExists(file.getPath())) {
                file.delete();
            }
            return;
        }
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : allEntries) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void rewritePropertyIndexFile(String path, PropertyIndex propertyIndex) {
        try {
        File file = new File(path);
        List<Map.Entry<String, String>> allEntries = propertyIndex.getBPlusTree().getAllEntries();
        if (allEntries.isEmpty()) {
            if (FileService.isFileExists(file.getPath())) {
                file.delete();
                File parentDir = file.getParentFile();
                if (isDirectoryExists(parentDir) && Objects.requireNonNull(parentDir.list()).length == 0) {
                    System.out.println("indexes directory is empty deleting it..........");
                    deleteDirectoryRecursively(file.toPath());
                }
            }
            return;
        }
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : allEntries) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static JSONObject addIdToDocument(JSONObject inputJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(inputJson.toString());
            if (!node.has("_id")) {  // Check if the _id field already exists
                System.out.println("generating a new id..................");
                UUID uuid = UUID.randomUUID();      // Generate a new id if it doesn't exist
                System.out.println(uuid + " idddddddddddddddddddddddddddddd");
                ((ObjectNode) node).put("_id", uuid.toString());
            }
            Map<String, Object> resultMap = mapper.convertValue(node, Map.class);
            return new JSONObject(resultMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean writeJsonArrayFile(File file, JSONArray jsonArray) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error while creating the file: " + e.getMessage());
                return false;
            }
        }
        try (RandomAccessFile stream = new RandomAccessFile(file, "rw");
             FileChannel channel = stream.getChannel()) {
            channel.truncate(0);
            byte[] bytes = jsonArray.toJSONString().getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.clear();
            buffer.put(bytes);
            buffer.flip();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while writing JSON file: " + e.getMessage());
            return false;
        }
    }

    public static String getUserJsonPath(String fileName){
        String usersDirectoryPath="src/main/resources/static";
        return usersDirectoryPath + "/" + fileName + ".json";
    }

    public static boolean isDirectoryExists(File file) {
        return !file.exists() || !file.isDirectory();
    }
}
