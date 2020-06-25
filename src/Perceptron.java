


import java.util.List;

public class Perceptron {

   private List<double[]> inputs;

   private double[][] muls;

   private double[] weights;

   private List<String> names;

    final static double e = 2.718281828;

   private double sum;

   private int cols, rows;

  private String name_of_looking_class;

    public Perceptron(List<String> names, int num_of_columns, int num_of_rows, List<double[]> inputs, String name_of_looking_class) { //x,y,z...

        this.cols = num_of_columns;

        this.rows = num_of_rows;

        this.weights = new double[num_of_columns]; //+prog

        this.names = names;

        this.inputs = inputs;

        this.muls = new double[num_of_rows][num_of_columns];

        this.name_of_looking_class = name_of_looking_class;
    }

    public void mul_arr(){



        for (int x = 0; x < muls.length; x++) {
            for (int y = 0; y < muls[x].length; y++) {

                muls[x][y] = inputs.get(x)[y] * weights[y];


            }

        }


    }

    public double sum(int row) {

        sum = 0;

        for (int y = 0; y < cols; y++) {

                sum += muls[row][y];
            }

        //funkcja aktywacji
        sum = (2/(1+(Math.pow(e,-sum))))-1;

           // System.out.println("sum " + sum);
        return sum;
    }

    public boolean compare(int row,String name){

        if (sum >= 0){

            System.out.println("Its " + name_of_looking_class);
           // System.out.println(name);
            System.out.println();

            if (!name.equals(name_of_looking_class)) {
                delta(row, true);

                return false;

            } else return true;



        } else {

            System.out.println("Its not " + name_of_looking_class);
         //   System.out.println(name);
            System.out.println();

            if (name.equals(name_of_looking_class)){

                delta(row,false);

                return false;

            }else return true;

        }


    }

    public boolean compare_testing(String name) {

        if (sum >= 0){

        //    System.out.println("Its " + name_of_looking_class);

            return true;

        } else {

        //    System.out.println("Its not " + name_of_looking_class);

                return false;

        }


    }

    public boolean compare_testing_console() {

        if (sum >= 0){

            System.out.println("Its setosa");

            return true;

        } else {

            System.out.println("Its not setosa");

                return false;
        }


    }

    public void delta(int row, boolean res){

        for (int i = 0; i < weights.length; i++) {

           // System.out.println("delta: " + weights[i] + " + (" + (res ? 0 : 1) + " - " + (res ? 1 : 0) + ") * " + 0.3 + " * " + inputs.get(row)[i]);
            weights[i] = weights[i] + ((res ? 0 : 1) - (res ? 1 : 0)) * 0.5 * inputs.get(row)[i];
        }

    }


    public List<String> getNames() {
        return names;
    }

    public double[] getWeights() {
        return weights;
    }

    public List<double[]> getInputs() {
        return inputs;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }


}
