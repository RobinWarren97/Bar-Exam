import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Robin Warren
 * This class is used to read in the question and answers text file. The text is processed,
 * and a JSON file is created. JSON file contains the 4 pairs of Q/A and boolean correct.
 * Specfically this class is used for the new NCBE questions.
 */
public class ReadFile {
    public static void main(String args[]) {

        //Reading file line by line using a BufferedReader
        FileInputStream fis = null;
        BufferedReader reader = null;
        //Arraylists to store the objects
        ArrayList<Question> listOfQuestions = new ArrayList<>();
        ArrayList<Answer> answerList = new ArrayList<>();

        try{
            //Read in answers set as object use basic File obj and Scanner - doesn't need to be a stream.
            File myObj = new File("10Qs_ans.txt");
            Scanner myReader = new Scanner(myObj);
            //Answers are in the format NUMBER. LETTER or NUMBER LETTER
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] splitLine = data.split(" ");
                //Some of the answer files need a "." to be ignored
                String answerNum = splitLine[0].replace(".","");
                String actualAns = splitLine[1];
                Answer a = new Answer(Integer.parseInt(answerNum), actualAns);
                answerList.add(a);
            }
        }catch (FileNotFoundException ex){
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        //An inital count number is used to read in Question 1 and reads until question 2
        //initCount is incremented according to the number of questions in a given file
        int initCount = 2;
        try {
            fis = new FileInputStream("10Qs.txt");
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            ArrayList<StringBuilder> sbs = new ArrayList<>();
            //Read in file until end line
            while(line != null){
                StringBuilder sb = new StringBuilder();
                //Read in all the lines/strings until the next question number
                while (!line.contains(initCount + ".")){
                    sb.append(line + " ");
                    line = reader.readLine();
                }
                sbs.add(sb);
                initCount++;
                //This specific file has 10Qs so count will end at 11.
                if(initCount == 11){
                    break;
                }
            }
            //After we have read all the text
            for (StringBuilder sb: sbs) {
                //Take away the question number eg 2., 3., 4. this will be different for the bigger question files
                String nums = sb.substring(0,2);
                String trimmed = nums.replace(".", "");
                Question q = new Question(Integer.valueOf(trimmed), sb.toString());
                listOfQuestions.add(q);
            }
            fis.close();
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Create a Feature Obj for each question and answer pair.
        ArrayList<Feature> features = new ArrayList<>();
        for (Question q : listOfQuestions){
            String questionNumber = q.getQuestionNumber().toString();
            String questionText = q.getQuestionText();
            String[] answersText = q.getAnswerTexts();

            //Pair the Question num to Answer num
            String answerString = "";
            for (Answer a : answerList){
                if (a.getAnswerNumber() == q.getQuestionNumber()){
                    answerString = a.getAnswer();
                }
            }

            String temp = "(" + answerString + ")";
            String actualAnswer = "";
            //Depending on the correct answer (A,B,C,D). Create a Feature for each answer, setting the correct ans
            //as correct and the others as false.
            for (String s: answersText){
                if (s.contains(temp)){
                    actualAnswer = s;
                    String fullFeature = questionText + actualAnswer;
                    Feature f = new Feature(fullFeature.replaceAll("\\t+", ""), true);
                    features.add(f);
                } else {
                    String fullFeature = questionText + s;
                    Feature f = new Feature(fullFeature.replaceAll("\\t+", ""), false);
                    features.add(f);
                }
            }
        }
        //Using a JSON Lib to convert POJO to JSON
        for (Feature feature : features){
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = null;
            try {
                json = ow.writeValueAsString(feature);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println(json);
            ObjectMapper objectMapper = new ObjectMapper();
            //Write to file
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("./9of10Qs.json"), features);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
