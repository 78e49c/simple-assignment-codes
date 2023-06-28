import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class c20190808056 {

    public static void main(String[] args)
    {
        StringBuilder text = new StringBuilder();

        try
        {
            File myObj = new File(args[0]);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                String data = myReader.nextLine();
                text.append(data);
            }
            myReader.close();
        }

        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        int[][][] split = construe(regulated(text.toString()));
        FCFS(split);
    }

    //Sort text
    public static String regulated(String text)
    {
        StringBuilder ret = new StringBuilder();
        int[] PID = PIDArray(text);
        String[] line = lineArray(text);


        int n = PID.length;
        for (int i = 0; i < n - 1; i++)
        {
            for (int j = 0; j < n - i - 1; j++)
            {
                if (PID[j] > PID[j + 1])
                {
                    int temp = PID[j];
                    PID[j] = PID[j + 1];
                    PID[j + 1] = temp;

                    String temp2 = line[j];
                    line[j] = line[j+1];
                    line[j+1] = temp2;
                }
            }
        }


        for (String s : line) {
            ret.append(s);
        }
        return ret.toString();
    }

    //Text PID to int array
    public static int[] PIDArray(String text)
    {
        int[] PID = new int[lineCounter(text)];
        boolean flag = true;

        StringBuilder temp = new StringBuilder();
        for (int i = 0,j=0; i <text.length()-3; i++)
        {
            if (text.charAt(i) == ':' )
            {
                flag = false;

                PID[j] = Integer.parseInt(temp.toString());
                j++;
                temp = new StringBuilder();
            }
            if(text.charAt(i) == '-')
            {
                i=i+3;
                flag = true;
            }
            if(flag)
            {
                temp.append(text.charAt(i));
            }
        }

        return PID;
    }

    //Text line to string array
    public static String[] lineArray(String text)
    {
        String[] s = new String[lineCounter(text)];

        Arrays.fill(s, "");

        for (int i = 0,j=0; i <text.length(); i++)
        {
            if (i > 2)
            {
                if (text.charAt(i-3) == '-')
                {j++;}
            }
            s[j] = s[j] + text.charAt(i);
        }
        return s;
    }

    static void FCFS(int[][][] split)
    {
        //Counts idle
        int idleNumber =0;
        int time =0;
        int[] returnTime = new int[split.length];
        //Saves if process halted
        boolean[] returnTimeLock = new boolean[split.length];

        //Saves the number of halted processes
        int finished = split.length;
        //Saves the number of can't run processes
        int none = split.length;
        int line =0;

        int[] tuple = new int[split.length];

        returnTime[line] = time + split[line][tuple[line]][0] + split[line][tuple[line]][1];
        time = time + split[line][tuple[line]][0];
        tuple[line]++;
        line++;


        while(finished > 0)
        {
            //If process can run
            if(returnTime[line]<=time)
            {
                //If process not in the last tuple
                if(split[line][tuple[line]][1] != -1)
                {

                    returnTime[line] = time + split[line][tuple[line]][0] + split[line][tuple[line]][1];

                    time = time + split[line][tuple[line]][0];
                    //if any process can run then restart
                    none = split.length;
                    tuple[line]++;

                }
                //If process in the last tuple and if not halted
                else if(!returnTimeLock[line])
                {
                    returnTime[line] = time + split[line][tuple[line]][0];

                    time = time + split[line][tuple[line]][0];
                    returnTimeLock[line]=true;
                    finished--;
                }

            }
            //If this process can't run
            else if(none !=0)
            {
                none--;
            }
            //If any process can't run
            else
            {
                idleNumber++;
                int min = 2147483647;
                for (int i = 0; i < returnTime.length ; i++)
                {
                    if(min > returnTime[i] && !returnTimeLock[i])
                    {min = returnTime[i];}
                }
                time = min;
            }

            line++;
            line = line%split.length;
        }

        System.out.println("Average turnaround time: "+calculateTT(returnTime));
        System.out.println("Average waiting time: "+calculateWT(returnTime,split));
        System.out.println("The number of times that the IDLE process executed: "+idleNumber);
        System.out.println("HALT");
    }

    //Finds the average turnaround time
    static double calculateTT (int[] returnTime)
    {
        double a =0;
        for (int j : returnTime) {
            a = a + j;
        }
        a=a/returnTime.length;

        return a;
    }

    //Finds the average waiting time
    static double calculateWT (int[] returnTime,int[][][] split)
    {

       int[] sum1 = new int[split.length];

        for (int i = 0; i <split.length ; i++)
        {
            for (int j = 0; j <split[i].length ; j++)
            {
                sum1[i] = sum1[i] + split[i][j][0];
            }
        }

        int[] WT = new int[split.length];
        double WTSum =0;

        for (int i = 0; i <WT.length ; i++)
        {
            WT[i] = returnTime[i]  - sum1[i];
            WTSum = WTSum + WT[i];
        }


        return WTSum/WT.length;
    }

    //Finds the number of line
    static int lineCounter(String text)
    {
        int line = 0;
        for(int i=0;text.length()>i;i++)
        {
            if (text.charAt(i) == '-')
            {line++;}
        }
        return line;
    }

    //Finds the number of tuples for each line separately
    static int[] tupleCounter(String text)
    {
        int[] tuple = new int[lineCounter(text)];

        for(int i=0, j=0, k=1;text.length()>i;i++)
        {
            if (text.charAt(i) == '-')
            {
                tuple[j]= k;
                j++;
                k=1;
            }
            if (text.charAt(i) == ';')
            {
                k++;
            }
        }
        return tuple;
    }

    //Creates an appropriately structured String array according to text
    static String[][][] createStringArray(String text)
    {
        String[][][] split = new String[lineCounter(text)][][];
        for (int i = 0; i <lineCounter(text) ; i++)
        {split[i] = new String[tupleCounter(text)[i]][2];}

        return split;
    }

    //Creates an appropriately structured int array according to text
    static int[][][] createIntArray(String text)
    {
        int[][][] split = new int[lineCounter(text)][][];
        for (int i = 0; i <lineCounter(text) ; i++)
        {split[i] = new int[tupleCounter(text)[i]][2];}

        return split;
    }

    //Fills the appropriately structured array, according to the text
    static String[][][] fillArray(String text , String[][][] split)
    {

        for (String[][] strings : split) {
            for (String[] string : strings) {
                Arrays.fill(string, "");
            }
        }

        for (int i = 0, j = 0, k = 0, l = 0; text.length() > i; i++)
        {
            if(i>2)
            {
                //By design, it jumps after ")"
                //We take one step back
                if (text.charAt(i-1) == ')' && text.charAt(i-3) == '-')
                {
                    j++;
                    k = 0;
                    l = 0;
                    i++;
                }
            }

            if (text.charAt(i) == '(')
            {
                l=0;
                i++;
                while(text.charAt(i) != ',')
                {
                    split[j][k][l] = split[j][k][l] + text.charAt(i);
                    i++;
                }
            }

            //We take one step back
            if(i>1)
            {
                if(text.charAt(i-1) == ')'){k++;}
            }

            if (text.charAt(i) == ',')
            {
                l++;
                i++;
                while(text.charAt(i) != ')')
                {
                    split[j][k][l] = split[j][k][l] + text.charAt(i);
                    i++;
                }

            }

        }

        return split;
    }

    //Converts 3D String array to int array with same structure
    static int[][][] convertArray(String text ,String[][][] split)
    {
        int[][][] a =createIntArray(text);

        for (int i = 0; i < split.length; i++)
        {
            for (int j = 0; j < split[i].length; j++)
            {
                for (int k = 0; k <  split[i][j].length; k++)
                {
                    a[i][j][k] = Integer.parseInt(split[i][j][k]);
                }
            }

        }

        return a;
    }

    //Manages the entire construe job (for brevity)
    static int[][][] construe(String text)
    {return convertArray(text,fillArray(text,createStringArray(text)));}

}