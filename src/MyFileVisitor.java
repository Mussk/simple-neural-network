import sun.util.resources.uk.CalendarData_uk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class MyFileVisitor implements FileVisitor<Path> {

    private File resFile;

    private String resultFileName;

    private String DirName;

    private Perceptron perceptron;

    private List<String> source_set;

    private  List<String> names;

    static List<String> attributes = new ArrayList<>();

    public MyFileVisitor() {

       // this.perceptron = perceptron;

       source_set = new ArrayList<>();

       names = new ArrayList<>();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

        if (dir.toFile().isDirectory()){

            DirName = dir.toFile().getName();

            return FileVisitResult.CONTINUE;

        }

        else return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        if (!file.toFile().isHidden() && !file.toFile().getName().equals("weights.txt")) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8));
            String current_line = "";
            String data_set = "";
            while ((current_line = reader.readLine()) != null) {
                data_set += current_line.replaceAll("[^\\x20-\\x7e]", "")
                                        .replaceAll("\\s+","")
                                        .replaceAll("[^a-zA-Z ]","")
                                        .toLowerCase();

            }

            if (attributes.isEmpty()) attributes.add(DirName);
            else { if (!attributes.contains(DirName)) attributes.add(DirName); }



            reader.close();

            source_set.add(data_set);

            names.add(DirName);

            data_set = "";
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getSource_set() {
        return source_set;
    }

}
