import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Classifier {

    /**
     *  LAST SUCCSESSFUL CONFIGURATION:
     *  learning const: 0.5;
     *  activation: sigma-function(bipolar);
     *
     *
     * **/

    private File file_weights;

   private static  double sucseedObs, allObs;

  private   List<double[]> training_set;

   private List<double[]> testing_set;

  private   List<String> names_training_set;

  private   List<String> names_testing_set;

  private List<String> weights;

   private String[][] results;

   private int[][] data_for_macierz_omylek;

    public Classifier() {
        file_weights = new File("/Users/Alex/IdeaProjects/NAi_mpp3/Source/weights.txt");

        start();

        new GUI(this).showGUI();

    }

    public void start(){

        try {

            prepareSource(new File("/Users/Alex/IdeaProjects/NAi_mpp3/Source"));

            training();

            testing();

        }catch (Exception ex){ex.printStackTrace();}

    }

    public void training(){

        List<Perceptron> perceptrons = new ArrayList<>();

        //add pereptron to each language
        for (int i = 0; i < MyFileVisitor.attributes.size(); i++) {
            perceptrons.add(new Perceptron(names_training_set,
                    training_set.get(0).length,
                    training_set.size(),
                    training_set,
                    MyFileVisitor.attributes.get(i)));


            allObs = perceptrons.get(i).getRows();

            double procent_accuracy = 0;

            sucseedObs = 0;

            while (procent_accuracy < 96) {

                sucseedObs = 0;

                try {

                    read_weights_from_file(perceptrons.get(i), file_weights,i);

                    perceptrons.get(i).mul_arr();

                    boolean res;

                    for (int j = 0; j < perceptrons.get(i).getRows(); j++) {

                        perceptrons.get(i).sum(j);

                        res = perceptrons.get(i).compare(j, perceptrons.get(i).getNames().get(j));

                        if (res) {

                            sucseedObs++;
                        }

                    }

                    write_weights_to_file(perceptrons.get(i), file_weights,i);

                    procent_accuracy = (sucseedObs * 100) / allObs;

                    System.out.println("Training finished, all observations: " + allObs +
                            "\nsuccseded observations: " + sucseedObs + "\nProcent of accuracy: " + procent_accuracy + "%");


                } catch (Exception ex) {
                    ex.printStackTrace();

                }

            }

        }

    }

    public void testing(){

        List<Perceptron> perceptrons = new ArrayList<>();

        results = new String[MyFileVisitor.attributes.size()][testing_set.size()];

        data_for_macierz_omylek = new int[MyFileVisitor.attributes.size()][MyFileVisitor.attributes.size()];

        for (int i = 0; i < data_for_macierz_omylek.length; i++) {
            for (int j = 0; j < data_for_macierz_omylek[i].length; j++) {
                data_for_macierz_omylek[i][j] = 0;
            }
        }


        for (int i = 0; i < MyFileVisitor.attributes.size(); i++) {
            perceptrons.add(new Perceptron(names_testing_set,
                    testing_set.get(0).length,
                    testing_set.size(),
                    testing_set,
                    MyFileVisitor.attributes.get(i)));


            allObs = perceptrons.get(i).getRows();




            try {

                read_weights_from_file(perceptrons.get(i), file_weights,i);

                perceptrons.get(i).mul_arr();

                for (int j = 0; j < perceptrons.get(i).getRows(); j++) {

                    perceptrons.get(i).sum(j);

                    boolean res;

                    res = perceptrons.get(i).compare_testing(perceptrons.get(i).getNames().get(j));

                    if (res) {

                        results[i][j] = "+";

                        data_for_macierz_omylek[i][i]++;

                      //  sucseedObs++;

                    } else results[i][j] = "-";
                }




            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        getStatistics();

    }

    public String testing_console(String string) {

        List<double[]> all_inputs = new ArrayList<>();

        double[] counters = new double[26];

        double[] results = new double[MyFileVisitor.attributes.size()];

        try {

            for (int k = 0; k < string.length(); k++) { //counting letters

                char letter = string.charAt(k);

                int val = (int) letter;

                if (val >= 97 && val <= 122) counters[letter - 'a']++;


            }

            all_inputs.add(new double[27]);

            int sum = sum(counters);

            for (int j = 0; j < counters.length; j++) all_inputs.get(0)[j] = counters[j]/sum;

            all_inputs.get(0)[counters.length] = -1;


            testing_set = new ArrayList<>();

            testing_set.add(all_inputs.get(0));


            List<Perceptron> perceptrons = new ArrayList<>();

            for (int i = 0; i < MyFileVisitor.attributes.size(); i++) {
                perceptrons.add(new Perceptron(null,
                        testing_set.get(0).length,
                        testing_set.size(),
                        testing_set,
                        MyFileVisitor.attributes.get(i)));


                read_weights_from_file(perceptrons.get(i), file_weights,i);

                perceptrons.get(i).mul_arr();

                results[i] = perceptrons.get(i).sum(0);

            }

            int counter = 0;
            for (int i = 0; i < results.length; i++) {
                if (results[i] >= 0){
                    counter++;
                }
            }

            if (counter == 1){
                for (int i = 0; i < results.length; i++) {
                    if (results[i] >= 0){
                        return  "It`s " + MyFileVisitor.attributes.get(i);
                    }
                }
            } else return "I don`t know";

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }





    public void write_weights_to_file(Perceptron perceptron, File file_to_write, int row){

        String res = "";

        for (int i = 0; i < perceptron.getWeights().length; i++) {
            //   perceptron.getWeights()[i][j] = 1; // MAKES WEIGHTS DEF VAL (1)

            res += perceptron.getWeights()[i] + "\t";
        }

        System.out.println(res);

        try {

            if (file_to_write.exists()) {
                file_to_write.delete();
                file_to_write.createNewFile();
            }

            weights.remove(row);

            weights.add(row,res);

            FileWriter fileWriter = new FileWriter(file_to_write, true);

            for (int i = 0; i < weights.size(); i++) {

                fileWriter.write(weights.get(i) + "\n");

                fileWriter.flush();
            }



        }catch (Exception ex){ex.printStackTrace();}


    }

    public void read_weights_from_file(Perceptron perceptron,File file_to_read,int row) throws Exception{

        weights = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_to_read), StandardCharsets.UTF_8));

        String line = "";

        while((line = reader.readLine()) != null) {

            weights.add(line);

        }


        for (int i = 0; i < perceptron.getWeights().length; i++) {

            perceptron.getWeights()[i] =  Double.parseDouble(weights.get(row).split("\\s+")[i]);
        }

    }

    public void getStatistics(){

        for (int i = 0; i < MyFileVisitor.attributes.size(); i++) {

            System.out.print(MyFileVisitor.attributes.get(i) + "\t");
        }

        System.out.println("Expected");

        System.out.println();


        for (int i = 0; i < results[0].length; i++) {

            for (int j = 0; j < results.length; j++) {

                if (j == results.length -1){

                    System.out.print(results[j][i] + "\t" + names_testing_set.get(i));

                } else {

                    System.out.print(results[j][i] + "\t");
                }
            }
            System.out.println();
        }

        System.out.println("All observations: " + allObs);

        System.out.println("\nMACIERZ OMYLEK");

        System.out.println();

        String[] column_names = {"P","R","F"};

        String[][] macierz_omylek = new String[MyFileVisitor.attributes.size()+1][MyFileVisitor.attributes.size()+4];

        macierz_omylek[0][0] = "->";

        for (int i = 1; i < macierz_omylek.length; i++) {
            macierz_omylek[i][0] = MyFileVisitor.attributes.get(i-1);
        }

        for (int i = 1; i < macierz_omylek.length; i++) {
            macierz_omylek[0][i] = MyFileVisitor.attributes.get(i-1);
        }

        for (int i = MyFileVisitor.attributes.size()+1; i < macierz_omylek[0].length; i++) {
            macierz_omylek[0][i] = column_names[i-(MyFileVisitor.attributes.size()+1)];
        }


        for (int i = 0; i < data_for_macierz_omylek.length; i++) {
            for (int j = 0; j < data_for_macierz_omylek[i].length; j++) {
                macierz_omylek[i+1][j+1] = Integer.toString(data_for_macierz_omylek[i][j]);
            }
        }



        //P
        for (int j = 0; j < MyFileVisitor.attributes.size(); j++) {

            int[] column = new int[MyFileVisitor.attributes.size()];
            for (int i = 0; i < data_for_macierz_omylek.length; i++) {
                column[i] = data_for_macierz_omylek[i][j];
            }

            String resulit = getPorR(column,j).toString();

            macierz_omylek[j+1][MyFileVisitor.attributes.size()+1] =  resulit;
        }

        //R
        for (int j = 0; j < MyFileVisitor.attributes.size(); j++) {

            int[] column = new int[MyFileVisitor.attributes.size()];
            for (int i = 0; i < data_for_macierz_omylek.length; i++) {
                column[i] = data_for_macierz_omylek[j][i];
            }

            String resulit = getPorR(column,j).toString();

            macierz_omylek[j+1][MyFileVisitor.attributes.size()+2] =  resulit;
        }

        //F
        for (int j = 0; j < MyFileVisitor.attributes.size(); j++) {
            macierz_omylek[j+1][macierz_omylek[0].length-1] = getF(macierz_omylek[j+1][macierz_omylek[0].length-3]
                                                                    ,macierz_omylek[j+1][macierz_omylek[0].length-2]);

        }

        for (int i = 0; i < macierz_omylek.length; i++) {
            for (int j = 0; j < macierz_omylek[i].length; j++) {
                System.out.print(macierz_omylek[i][j]+"\t");
            }
            System.out.println();
        }

    }

    public BigDecimal getPorR(int[] arr,int index){



        double[] arr_double = new double[arr.length];

        double sum = 0;

        for (int i = 0; i < arr.length; i++) {
            arr_double[i] = arr[i];
            sum += arr_double[i];
        }



        return BigDecimal.valueOf(arr_double[index]/sum).setScale(0,BigDecimal.ROUND_HALF_DOWN);
    }


    public String getF(String P, String R){

       double p_double = Double.parseDouble(P);

       double r_double = Double.parseDouble(R);

        return BigDecimal.valueOf((2 * p_double * r_double)/(p_double + r_double))
                        .setScale(0,BigDecimal.ROUND_HALF_DOWN).toString();

    }

    public void prepareSource(File file) throws Exception{

        MyFileVisitor myFileVisitor = new MyFileVisitor();

        Files.walkFileTree(file.toPath(), myFileVisitor);

        List<double[]> all_inputs = new ArrayList<>();

        double[] counters = new double[26];


        for (int i = 0; i <  myFileVisitor.getSource_set().size(); i++){ //iterating texts

            for (int j = 0; j < counters.length; j++) counters[j] = 0;


            for (int k = 0; k < myFileVisitor.getSource_set().get(i).length(); k++) { //counting letters

                        char letter = myFileVisitor.getSource_set().get(i).charAt(k);

                        int val = (int) letter;

                        if (val >= 97 && val <= 122) counters[letter - 'a']++;


            }

                    all_inputs.add(new double[27]);

                    int sum = sum(counters);

                    for (int j = 0; j < counters.length; j++) all_inputs.get(i)[j] = counters[j]/sum;

                    all_inputs.get(i)[counters.length] = -1;

        }

        
         training_set = new ArrayList<>();

         testing_set = new ArrayList<>();

         names_testing_set = new ArrayList<>();

         names_training_set = new ArrayList<>();

         //DIVIDE SOURCE ON TRAINIG/TESTING SET

        for (int i = 0; i < all_inputs.size(); i++) {

            if (i % 2 !=0){

                training_set.add(all_inputs.get(i));

                names_training_set.add(myFileVisitor.getNames().get(i));

            } else {

                testing_set.add(all_inputs.get(i));

                names_testing_set.add(myFileVisitor.getNames().get(i));

            }

        }

        //SHOWING SOURCE SETS

   /*     System.out.println("\nTRAINING SET\n");

        for (int j = 0; j < training_set.size(); j++) {
            for (int k = 0; k < training_set.get(j).length; k++) {
                System.out.print(training_set.get(j)[k] + "\t");
            }
            System.out.print(names_training_set.get(j));
            System.out.println();
        }

        System.out.println("\nTESTING SET\n");

        for (int j = 0; j < testing_set.size(); j++) {
            for (int k = 0; k < testing_set.get(j).length; k++) {
                System.out.print(testing_set.get(j)[k] + "\t");
            }
            System.out.print(names_testing_set.get(j));
            System.out.println();
        } */

    }

    public Integer sum(double[] arr){

        int sum = 0;

        for (int i = 0; i < arr.length; i++) sum+= arr[i];

        return sum;
    }


}


